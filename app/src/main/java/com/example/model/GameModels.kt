package com.example.model

enum class PingStatus {
    IDLE,
    TESTING,
    EXCELLENT, // < 40ms
    GOOD,      // 40 - 80ms
    MODERATE,  // 80 - 120ms
    HIGH,      // > 120ms
    TIMEOUT
}

data class ServerRegion(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val host: String,
    val port: Int = 80,
    val pingMs: Int? = null,
    val jitterMs: Int? = null,
    val status: PingStatus = PingStatus.IDLE
)

data class DevicePerformanceInfo(
    val currentRefreshRate: Int = 60,
    val supportedRefreshRates: List<Int> = listOf(60),
    val usedRamMb: Long = 0,
    val totalRamMb: Long = 0,
    val batteryPercent: Int = 100,
    val batteryTempCelsius: Float = 32.0f,
    val deviceModel: String = "Android Device",
    val androidVersion: String = "14",
    val maxTheoreticalFps: Int = 60
) {
    val ramUsagePercent: Int
        get() = if (totalRamMb > 0) ((usedRamMb.toDouble() / totalRamMb) * 100).toInt() else 0
}

data class FovSimulationState(
    val selectedFovType: FovType = FovType.IPAD_VIEW_4_3,
    val cameraSens: Float = 100f,
    val adsSens: Float = 95f,
    val gyroSens: Float = 280f,
    val showEnemyTargets: Boolean = true
)

enum class FovType(
    val labelAr: String,
    val labelEn: String,
    val ratio: String,
    val horizontalFovDeg: Int,
    val verticalFovDeg: Int,
    val sensMultiplier: Float,
    val widthToHeightRatio: Float
) {
    PHONE_NARROW("هاتف شاشة عريضة", "Phone 20:9", "20:9", 80, 52, 1.0f, 20f / 9f),
    PHONE_STANDARD("هاتف قياسي", "Phone 16:9", "16:9", 90, 59, 1.05f, 16f / 9f),
    IPAD_VIEW_4_3("منظور الآيباد المتكامل", "iPad 4:3 View", "4:3", 105, 78, 1.18f, 4f / 3f),
    TABLET_EXPANDED("أقصى منظور تابلت", "Tablet 3:2 View", "3:2", 115, 84, 1.25f, 3f / 2f)
}

data class FpsBenchmarkState(
    val isRunning: Boolean = false,
    val currentFps: Int = 60,
    val minFps: Int = 60,
    val maxFps: Int = 60,
    val avgFps: Int = 60,
    val stabilityPercent: Int = 98,
    val frameSamples: List<Int> = emptyList(),
    val progressSeconds: Int = 0
)

data class AntiBanProtectionGuide(
    val id: String,
    val titleAr: String,
    val category: String,
    val riskLevel: RiskLevel,
    val whyBanned: String,
    val safeAlternative: String
)

enum class RiskLevel {
    CRITICAL_BAN, // Hacks, ESP, Memory hooks
    HIGH_RISK,    // Unverified Configs, Shizuku injectors
    SAFE_OFFICIAL // In-game display, Dev Options, Refresh Rate
}
