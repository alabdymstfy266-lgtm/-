package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MmmxxViewModel

@Composable
fun FpsAnalyzerScreen(
    viewModel: MmmxxViewModel,
    modifier: Modifier = Modifier
) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val fpsBenchmark by viewModel.fpsBenchmark.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "تشخيص معدل الإطارات والشاشة",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "تحليل قدرات العرض المادية للشاشة واختبار استقرار الفريمات",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Active Hardware Refresh Rate Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "تردد الشاشة الفعلي الآن",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${deviceInfo.currentRefreshRate}",
                                color = CyberCyan,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hz (إطار/ثانية)",
                                color = CyberCyan.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.12f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Supported refresh rates chips
                Text(
                    text = "الترددات المدعومة من شاشة جهازك:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val supported = if (deviceInfo.supportedRefreshRates.isNotEmpty()) {
                        deviceInfo.supportedRefreshRates
                    } else {
                        listOf(60, 90, 120)
                    }

                    supported.forEach { rate ->
                        val isCurrent = rate == deviceInfo.currentRefreshRate
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) CyberCyan else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isCurrent) CyberCyan else DarkOutline,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$rate Hz",
                                color = if (isCurrent) Color.Black else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // The 240 FPS Reality Check Banner (Crucial clarification)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "توضيح تقني: حقيقة الـ 240 فريم",
                        color = CyberAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "• شاشة الهاتف المادية (Hardware Display) تحدد السقف الأقصى. إذا كانت شاشتك 60Hz أو 90Hz أو 120Hz، فلا يمكن للعين رؤية 240 إطاراً في الثانية فيزيائياً لأن الشاشة لا ترسم سوى 60/120 صورة بالثانية كحد أقصى.\n\n• الملفات أو التطبيقات المعدلة التي تدعي 'تفعيل 240 فريم' تقوم فقط بتزوير القراءات، مما يرفع حرارة المعالج بشكل خطير، ويتسبب في لاغ دروب حاد، ويؤدي إلى حظر الحساب (باند 10 سنوات) بسبب التلاعب ببيانات اللعبة.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Live Benchmark Simulator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "اختبار استقرار وثبات الفريمات",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "قياس تقلبات الإطارات أثناء الضغط العالي",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (fpsBenchmark.isRunning) {
                        Text(
                            text = "${fpsBenchmark.progressSeconds}/10 ثانية",
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Bench Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BenchStatItem(
                        label = "الفريم الحالي",
                        value = "${fpsBenchmark.currentFps}",
                        color = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    BenchStatItem(
                        label = "أدنى فريم (1% Low)",
                        value = "${fpsBenchmark.minFps}",
                        color = CyberAmber,
                        modifier = Modifier.weight(1f)
                    )
                    BenchStatItem(
                        label = "ثبات الأداء",
                        value = "${fpsBenchmark.stabilityPercent}%",
                        color = CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Graph Simulation
                if (fpsBenchmark.frameSamples.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        fpsBenchmark.frameSamples.forEach { sample ->
                            val heightFraction = (sample / 144f).coerceIn(0.2f, 1f)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .fillMaxWidth()
                                    .height((60 * heightFraction).dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(CyberCyan)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (fpsBenchmark.isRunning) {
                            viewModel.stopFpsBenchmark()
                        } else {
                            viewModel.startFpsBenchmark()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_fps_benchmark_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fpsBenchmark.isRunning) CyberRed else CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (fpsBenchmark.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (fpsBenchmark.isRunning) "إيقاف الاختبار" else "بدء اختبار الفريمات (10 ثوان)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Official Safe 90/120 FPS Setup Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "كيف تفعل 90 و 120 فريم بالطريقة الرسمية والآمنة؟",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                SafeStep(
                    number = "1",
                    title = "ضبط شاشة الهاتف على أعلى تردد",
                    desc = "ادخل على إعدادات الهاتف > الشاشة > معدل التحديث (Refresh Rate) واختر 120Hz أو 144Hz بدلاً من التلقائي."
                )

                SafeStep(
                    number = "2",
                    title = "اختيار رسومات سلسة (Smooth)",
                    desc = "داخل اللعبة اختر جودة الرسومات 'سلسة' ومعدل الإطارات '90 إطار' أو 'فائق للغاية / 120 إطار' إذا كان مدعوماً."
                )

                SafeStep(
                    number = "3",
                    title = "إلغاء وضع توفير الطاقة",
                    desc = "وضع توفير البطارية يقفل تردد الشاشة تلقائياً على 60Hz. عطل توفير الطاقة لتفادي هبوط الفريمات."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BenchStatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SafeStep(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CyberGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = CyberGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
