package com.example.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.view.Display
import com.example.model.DevicePerformanceInfo
import com.example.model.PingStatus
import com.example.model.ServerRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.roundToInt

class SystemGameDiagnostics(private val context: Context) {

    fun getDevicePerformanceInfo(): DevicePerformanceInfo {
        var currentHz = 60
        val supportedHz = mutableListOf<Int>()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                display?.mode?.refreshRate?.let { currentHz = it.roundToInt() }
                display?.supportedModes?.forEach { mode ->
                    val hz = mode.refreshRate.roundToInt()
                    if (hz !in supportedHz) supportedHz.add(hz)
                }
            } else {
                val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
                val defaultDisplay = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
                defaultDisplay?.mode?.refreshRate?.let { currentHz = it.roundToInt() }
                defaultDisplay?.supportedModes?.forEach { mode ->
                    val hz = mode.refreshRate.roundToInt()
                    if (hz !in supportedHz) supportedHz.add(hz)
                }
            }
        } catch (_: Exception) {
            currentHz = 60
        }

        if (supportedHz.isEmpty()) {
            supportedHz.addAll(listOf(60, 90, 120))
        }
        supportedHz.sort()

        // RAM check
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)

        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        val usedRamMb = (totalRamMb - availRamMb).coerceAtLeast(0)

        // Battery check
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 85
        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 320) ?: 320
        val tempCelsius = tempTenths / 10.0f

        val deviceName = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        val androidVer = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        val maxHz = supportedHz.maxOrNull() ?: 60

        return DevicePerformanceInfo(
            currentRefreshRate = currentHz,
            supportedRefreshRates = supportedHz,
            usedRamMb = usedRamMb,
            totalRamMb = totalRamMb,
            batteryPercent = batteryPct,
            batteryTempCelsius = tempCelsius,
            deviceModel = deviceName,
            androidVersion = androidVer,
            maxTheoreticalFps = maxHz
        )
    }

    suspend fun measureRegionPing(region: ServerRegion): ServerRegion = withContext(Dispatchers.IO) {
        val samples = mutableListOf<Long>()
        var successCount = 0

        for (i in 0 until 3) {
            val startTime = System.currentTimeMillis()
            var socket: Socket? = null
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(region.host, region.port), 1200)
                val duration = System.currentTimeMillis() - startTime
                samples.add(duration)
                successCount++
            } catch (_: Exception) {
                // Ignore single packet failure
            } finally {
                try {
                    socket?.close()
                } catch (_: Exception) {
                }
            }
        }

        if (samples.isEmpty()) {
            return@withContext region.copy(
                pingMs = null,
                jitterMs = null,
                status = PingStatus.TIMEOUT
            )
        }

        val avgPing = samples.average().roundToInt()
        val jitter = if (samples.size > 1) {
            val maxSample = samples.maxOrNull() ?: avgPing.toLong()
            val minSample = samples.minOrNull() ?: avgPing.toLong()
            (maxSample - minSample).toInt()
        } else {
            3
        }

        val status = when {
            avgPing < 45 -> PingStatus.EXCELLENT
            avgPing < 85 -> PingStatus.GOOD
            avgPing < 130 -> PingStatus.MODERATE
            else -> PingStatus.HIGH
        }

        return@withContext region.copy(
            pingMs = avgPing,
            jitterMs = jitter,
            status = status
        )
    }
}
