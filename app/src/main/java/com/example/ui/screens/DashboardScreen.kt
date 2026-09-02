package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MmmxxViewModel

@Composable
fun DashboardScreen(
    viewModel: MmmxxViewModel,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val boostMsg by viewModel.memoryBoostMessage.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF162544),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "أداة MMMXX للألعاب",
                            color = CyberCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Game Performance & Safety Suite",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberGreen.copy(alpha = 0.15f))
                            .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "بدون روت 100%",
                            color = CyberGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "${deviceInfo.deviceModel} • ${deviceInfo.androidVersion}",
                    color = TextPrimary,
                    fontSize = 13.sp
                )

                // Quick Hardware Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBadge(
                        title = "تردد الشاشة",
                        value = "${deviceInfo.currentRefreshRate} Hz",
                        color = CyberCyan,
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "أقصى فريم مادي",
                        value = "${deviceInfo.maxTheoreticalFps} FPS",
                        color = CyberAmber,
                        icon = Icons.Default.Bolt,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadge(
                        title = "حرارة البطارية",
                        value = "${deviceInfo.batteryTempCelsius}°C",
                        color = if (deviceInfo.batteryTempCelsius > 39f) Color(0xFFFF5252) else CyberGreen,
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Memory Usage Card
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "استهلاك الذاكرة (RAM)",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "${deviceInfo.usedRamMb} MB / ${deviceInfo.totalRamMb} MB (${deviceInfo.ramUsagePercent}%)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                LinearProgressIndicator(
                    progress = { deviceInfo.ramUsagePercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (deviceInfo.ramUsagePercent > 80) Color(0xFFFF5252) else CyberCyan,
                    trackColor = DarkSurfaceVariant
                )

                AnimatedVisibility(visible = boostMsg != null) {
                    Text(
                        text = boostMsg ?: "",
                        color = CyberGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerQuickMemoryOptimization() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("boost_memory_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "تنظيف الذاكرة الفوري",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.refreshHardwareInfo() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("refresh_device_info_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "تحديث البيانات",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Section Cards / Quick Navigation Grid
        Text(
            text = "الأدوات والخدمات المتاحة",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        QuickModuleCard(
            title = "تشخيص 240 / 120 / 90 فريم",
            subtitle = "فحص تردد الشاشة الحقيقي، اختبار ثبات الفريمات، والحماية من وهم ملفات 240 فريم",
            icon = Icons.Default.Speed,
            badge = "FPS Monitor",
            tint = CyberCyan,
            onClick = { onNavigateToTab(1) },
            testTag = "nav_to_fps_tab"
        )

        QuickModuleCard(
            title = "محاكي منظور الآيباد (4:3 FOV)",
            subtitle = "مقارنة زاوية الرؤية، كشف الأطراف، وحاسبة تحويل الحساسية للحفاظ على دقة الأيم",
            icon = Icons.Default.TabletMac,
            badge = "iPad FOV",
            tint = CyberAmber,
            onClick = { onNavigateToTab(2) },
            testTag = "nav_to_fov_tab"
        )

        QuickModuleCard(
            title = "فحص بنج السيرفرات (Ping Test)",
            subtitle = "قياس سرعة الاستجابة لسيرفرات الشرق الأوسط، أوروبا، وآسيا بدقة بالمللي ثانية",
            icon = Icons.Default.NetworkCheck,
            badge = "Live Ping",
            tint = CyberGreen,
            onClick = { onNavigateToTab(3) },
            testTag = "nav_to_ping_tab"
        )

        QuickModuleCard(
            title = "درع الأمان وتجنب الحظر (Anti-Ban)",
            subtitle = "دليل الحماية من الباند 10 سنوات، مخاطر ESP والشيزوكو، والبدائل الرسمية الآمنة",
            icon = Icons.Default.Security,
            badge = "Anti-Ban Safe",
            tint = Color(0xFFFF5252),
            onClick = { onNavigateToTab(4) },
            testTag = "nav_to_safety_tab"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatBadge(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f))
                    .border(1.dp, tint.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tint.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = tint,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
