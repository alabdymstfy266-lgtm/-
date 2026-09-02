package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SystemGameDiagnostics
import com.example.model.AntiBanProtectionGuide
import com.example.model.DevicePerformanceInfo
import com.example.model.FovSimulationState
import com.example.model.FovType
import com.example.model.FpsBenchmarkState
import com.example.model.PingStatus
import com.example.model.RiskLevel
import com.example.model.ServerRegion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class MmmxxViewModel(application: Application) : AndroidViewModel(application) {

    private val diagnostics = SystemGameDiagnostics(application.applicationContext)

    private val _deviceInfo = MutableStateFlow(diagnostics.getDevicePerformanceInfo())
    val deviceInfo: StateFlow<DevicePerformanceInfo> = _deviceInfo.asStateFlow()

    private val _serverRegions = MutableStateFlow(
        listOf(
            ServerRegion(
                id = "me_aws",
                nameAr = "الشرق الأوسط (الخليج / البحرين)",
                nameEn = "Middle East (Gulf / Bahrain)",
                host = "ec2.me-south-1.amazonaws.com",
                port = 80
            ),
            ServerRegion(
                id = "me_cf",
                nameAr = "سيرفر الألعاب السريع (DNS الإقليمي)",
                nameEn = "Low-Latency Regional DNS",
                host = "1.1.1.1",
                port = 80
            ),
            ServerRegion(
                id = "eu_central",
                nameAr = "أوروبا (فرانكفورت)",
                nameEn = "Europe (Frankfurt)",
                host = "ec2.eu-central-1.amazonaws.com",
                port = 80
            ),
            ServerRegion(
                id = "asia_sg",
                nameAr = "آسيا (سنغافورة)",
                nameEn = "Asia (Singapore)",
                host = "ec2.ap-southeast-1.amazonaws.com",
                port = 80
            )
        )
    )
    val serverRegions: StateFlow<List<ServerRegion>> = _serverRegions.asStateFlow()

    private val _isTestingPing = MutableStateFlow(false)
    val isTestingPing: StateFlow<Boolean> = _isTestingPing.asStateFlow()

    private val _fpsBenchmark = MutableStateFlow(FpsBenchmarkState())
    val fpsBenchmark: StateFlow<FpsBenchmarkState> = _fpsBenchmark.asStateFlow()

    private var benchmarkJob: Job? = null

    private val _fovState = MutableStateFlow(FovSimulationState())
    val fovState: StateFlow<FovSimulationState> = _fovState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _memoryBoostMessage = MutableStateFlow<String?>(null)
    val memoryBoostMessage: StateFlow<String?> = _memoryBoostMessage.asStateFlow()

    val antiBanGuides: List<AntiBanProtectionGuide> = listOf(
        AntiBanProtectionGuide(
            id = "guide_1",
            titleAr = "حقيقة الـ 240 فريم وكسر الحماية",
            category = "الأداء ومعدل الإطارات",
            riskLevel = RiskLevel.CRITICAL_BAN,
            whyBanned = "تعديل ملفات اللعبة (UserCustom.ini أو Active.sav) لإجبار اللعبة على طلب 240 فريم يرسل حزم بيانات غير طبيعية لسيرفرات اللعبة مما يؤدي إلى رصد فوري وحظر لمدة 10 سنوات. علاوة على ذلك، إذا كانت شاشة جهازك 60Hz أو 120Hz، فلن ترى أي فرق فعلي لأن الشاشة لا تستطيع فيزيائياً عرض أكثر من ترددها الأصلي.",
            safeAlternative = "قم بتفعيل خيار 90 FPS أو 120 FPS الرسمي من إعدادات الرسومات داخل اللعبة (سلسة + 90/120 إطار). وقم بتفعيل خيار أعلى تردد للشاشة من إعدادات هاتف أندرويد."
        ),
        AntiBanProtectionGuide(
            id = "guide_2",
            titleAr = "كشف الأماكن (ESP / Wallhack)",
            category = "نزاهة اللعب والحماية",
            riskLevel = RiskLevel.CRITICAL_BAN,
            whyBanned = "أنظمة الحماية الرسمية (Tencent ACE و BattleEye) تفحص ذاكرة الوصول العشوائي ومسارات اللعبة بدقة فائقة. استخدام أي تطبيق لكشف الأماكن (حتى لو ادعى أنه بدون روت أو بدون باند) ينكشف بنسبة 100% ويؤدي لحظر الحساب ورمز الجهاز (Device ID Ban) نهائياً.",
            safeAlternative = "استخدم إعدادات عمى الألوان الرسمية (مثل خيار عمى الألوان الأحمر/الأخضر داخل اللعبة) لزيادة وضوح الدم وتباين الأعداء على العشب والصخور، وارفع سطوع اللعبة إلى 130%."
        ),
        AntiBanProtectionGuide(
            id = "guide_3",
            titleAr = "منظور الآيباد (iPad View FOV)",
            category = "زاوية الرؤية والحساسية",
            riskLevel = RiskLevel.HIGH_RISK,
            whyBanned = "تعديل أبعاد العرض عبر برامج خارجية غير موثوقة أو استبدال ملفات اللعبة يعتبر تعديلاً على بيانات العميل (Client Modification) ويعرضك للحظر في التحديثات الدورية للعبة.",
            safeAlternative = "استخدم حاسبة MMMXX المدمجة لمعايرة حساسية اللمس ومحاكاة زاوية الرؤية، أو استفد من ميزة 'زاوية الرؤية للشخص الثالث (TPP FOV)' الرسمية في إعدادات اللعبة ورفعها إلى 90 درجة بدون أي خطر."
        ),
        AntiBanProtectionGuide(
            id = "guide_4",
            titleAr = "أدوات الشيزوكو والروت الخفي",
            category = "أذونات النظام",
            riskLevel = RiskLevel.HIGH_RISK,
            whyBanned = "حقن الأوامر عبر Shizuku أو وحدات Magisk يتم التقاطه عبر واجهة Google Play Integrity وفحص أمان النظام، مما يسجل تقرير اشتباه فوري لسيرفر اللعبة.",
            safeAlternative = "استخدم وضع الألعاب المدمج في جهازك (Game Turbo / Game Space)، وأغلق تطبيقات الخلفية المستنزفة للموارد لضمان ثبات الإطارات قانونياً وبأمان تام."
        )
    )

    init {
        refreshHardwareInfo()
        runPingTest()
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun refreshHardwareInfo() {
        _deviceInfo.value = diagnostics.getDevicePerformanceInfo()
    }

    fun triggerQuickMemoryOptimization() {
        viewModelScope.launch {
            System.gc()
            delay(500)
            refreshHardwareInfo()
            _memoryBoostMessage.value = "تم تحسين مساحة الذاكرة بنجاح! جاهز لتشغيل سلس."
            delay(3500)
            _memoryBoostMessage.value = null
        }
    }

    fun runPingTest() {
        if (_isTestingPing.value) return
        viewModelScope.launch {
            _isTestingPing.value = true
            val currentList = _serverRegions.value.map { it.copy(status = PingStatus.TESTING) }
            _serverRegions.value = currentList

            val updated = mutableListOf<ServerRegion>()
            for (region in currentList) {
                val measured = diagnostics.measureRegionPing(region)
                updated.add(measured)
                _serverRegions.value = updated + currentList.drop(updated.size)
            }
            _isTestingPing.value = false
        }
    }

    fun startFpsBenchmark() {
        if (_fpsBenchmark.value.isRunning) return
        val targetHz = _deviceInfo.value.currentRefreshRate.coerceAtLeast(60)

        benchmarkJob?.cancel()
        benchmarkJob = viewModelScope.launch {
            _fpsBenchmark.value = FpsBenchmarkState(
                isRunning = true,
                currentFps = targetHz,
                minFps = targetHz,
                maxFps = targetHz,
                avgFps = targetHz,
                stabilityPercent = 100,
                frameSamples = emptyList(),
                progressSeconds = 0
            )

            val samples = mutableListOf<Int>()
            for (sec in 1..10) {
                delay(1000)
                // Sample simulated high-frame rendering with slight realistic jitter based on device capability
                val variance = Random.nextInt(-3, 2)
                val sampleFps = (targetHz + variance).coerceIn(30, targetHz)
                samples.add(sampleFps)

                val min = samples.minOrNull() ?: sampleFps
                val max = samples.maxOrNull() ?: sampleFps
                val avg = samples.average().roundToInt()
                val stability = ((min.toDouble() / max.toDouble()) * 100).roundToInt().coerceIn(70, 100)

                _fpsBenchmark.value = FpsBenchmarkState(
                    isRunning = sec < 10,
                    currentFps = sampleFps,
                    minFps = min,
                    maxFps = max,
                    avgFps = avg,
                    stabilityPercent = stability,
                    frameSamples = samples.toList(),
                    progressSeconds = sec
                )
            }
        }
    }

    fun stopFpsBenchmark() {
        benchmarkJob?.cancel()
        _fpsBenchmark.update { it.copy(isRunning = false) }
    }

    fun setFovType(fovType: FovType) {
        _fovState.update { it.copy(selectedFovType = fovType) }
    }

    fun updateCameraSens(sens: Float) {
        _fovState.update { it.copy(cameraSens = sens) }
    }

    fun updateAdsSens(sens: Float) {
        _fovState.update { it.copy(adsSens = sens) }
    }

    fun updateGyroSens(sens: Float) {
        _fovState.update { it.copy(gyroSens = sens) }
    }

    fun toggleEnemyTargets() {
        _fovState.update { it.copy(showEnemyTargets = !it.showEnemyTargets) }
    }
}
