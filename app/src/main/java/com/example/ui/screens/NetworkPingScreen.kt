package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CellWifi
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PingStatus
import com.example.model.ServerRegion
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
fun NetworkPingScreen(
    viewModel: MmmxxViewModel,
    modifier: Modifier = Modifier
) {
    val regions by viewModel.serverRegions.collectAsState()
    val isTesting by viewModel.isTestingPing.collectAsState()
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
                text = "فحص بنج السيرفرات وجودة الشبكة",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "قياس زمن الاستجابة الفعلي لسيرفرات الألعاب العالمية بدون انقطاع",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Test Trigger Action Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "قياس مباشر للاتصال (Ping / RTT)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isTesting) "جاري إرسال حزم البيانات..." else "اضغط لإعادة فحص السيرفرات",
                        color = if (isTesting) CyberCyan else TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { viewModel.runPingTest() },
                    enabled = !isTesting,
                    modifier = Modifier.testTag("run_ping_test_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTesting) "جاري الفحص" else "فحص الآن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Server Regions List
        Text(
            text = "نتائج استجابة السيرفرات الإقليمية:",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        regions.forEach { region ->
            ServerRegionCard(region = region)
        }

        // Network Optimization Advice
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
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "نصائح تقليل البنج وتثبيت الاتصال (MS Drop)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                NetworkTipItem(
                    title = "الاتصال عبر تردد 5GHz في الراوتر",
                    desc = "تردد 2.4GHz يعاني من تشويش البلوتوث والأجهزة المحيطة. الاتصال عبر 5GHz يخفض البنج بمقدار 20ms على الأقل."
                )

                NetworkTipItem(
                    title = "تعطيل مزامنة التطبيقات والتحميل التلقائي",
                    desc = "تطبيقات مثل Google Photos و TikTok تستخدم الرفع في الخلفية مما يسبب ارتفاع مفاجئ في البنج (Ping Spikes)."
                )

                NetworkTipItem(
                    title = "استخدام DNS مخصص للألعاب",
                    desc = "استخدام سيرفرات Cloudflare (1.1.1.1) أو Google (8.8.8.8) يقلل مسار العقد (Hop Count) للوصول إلى سيرفر اللعبة."
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ServerRegionCard(region: ServerRegion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(getStatusColor(region.status).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        tint = getStatusColor(region.status),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = region.nameAr,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = region.nameEn,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                when (region.status) {
                    PingStatus.TESTING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CyberCyan,
                            strokeWidth = 2.dp
                        )
                    }
                    PingStatus.TIMEOUT -> {
                        Text(
                            text = "تعذر القياس",
                            color = CyberRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    PingStatus.IDLE -> {
                        Text(
                            text = "-- ms",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                    else -> {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${region.pingMs}",
                                color = getStatusColor(region.status),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " ms",
                                color = getStatusColor(region.status),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = getStatusLabel(region.status),
                            color = getStatusColor(region.status),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun getStatusColor(status: PingStatus): Color {
    return when (status) {
        PingStatus.EXCELLENT -> CyberGreen
        PingStatus.GOOD -> CyberCyan
        PingStatus.MODERATE -> CyberAmber
        PingStatus.HIGH, PingStatus.TIMEOUT -> CyberRed
        PingStatus.TESTING -> CyberCyan
        PingStatus.IDLE -> TextSecondary
    }
}

private fun getStatusLabel(status: PingStatus): String {
    return when (status) {
        PingStatus.EXCELLENT -> "ممتاز وسلس"
        PingStatus.GOOD -> "جيد جداً"
        PingStatus.MODERATE -> "متوسط"
        PingStatus.HIGH -> "مرتفع (لاغ)"
        PingStatus.TIMEOUT -> "انقطاع"
        PingStatus.TESTING -> "فحص..."
        PingStatus.IDLE -> "في الانتظار"
    }
}

@Composable
private fun NetworkTipItem(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(CyberCyan)
        )
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
