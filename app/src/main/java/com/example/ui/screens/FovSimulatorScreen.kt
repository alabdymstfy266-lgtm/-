package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FovType
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FovSimulatorScreen(
    viewModel: MmmxxViewModel,
    modifier: Modifier = Modifier
) {
    val fovState by viewModel.fovState.collectAsState()
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
                text = "محاكي منظور الآيباد والحساسية (iPad FOV)",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "مقارنة حقل الرؤية وكشف الأطراف مع معايرة الحساسية الدقيقة",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // FOV Mode Selector Chips
        Text(
            text = "اختر نوع شاشة العرض والمنظور:",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FovType.values().forEach { type ->
                val isSelected = type == fovState.selectedFovType
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberAmber else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isSelected) CyberAmber else DarkOutline,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setFovType(type) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = type.ratio,
                            color = if (isSelected) Color.Black else CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${type.horizontalFovDeg}°",
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Tactical Canvas: Visual FOV Cone & Enemy Detection Comparison
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = CyberAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "محاكاة الرادار ومجال الرؤية المحيطي",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = fovState.selectedFovType.labelAr,
                        color = CyberAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Interactive Radar Canvas
                val currentFovDeg = fovState.selectedFovType.horizontalFovDeg
                val animatedAngle by animateFloatAsState(
                    targetValue = currentFovDeg.toFloat(),
                    label = "fovAngle"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090D14))
                        .border(1.dp, DarkOutline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val originX = w / 2f
                        val originY = h * 0.85f
                        val radius = h * 0.72f

                        // Radar concentric rings
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = radius * 0.4f,
                            center = Offset(originX, originY),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = radius * 0.75f,
                            center = Offset(originX, originY),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = radius,
                            center = Offset(originX, originY),
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Center guideline
                        drawLine(
                            color = Color(0xFF334155),
                            start = Offset(originX, originY),
                            end = Offset(originX, originY - radius),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Vision Cone according to FOV
                        val halfAngleRad = (animatedAngle / 2f) * (PI / 180f).toFloat()
                        val leftAngleRad = (-PI / 2f - halfAngleRad).toFloat()
                        val rightAngleRad = (-PI / 2f + halfAngleRad).toFloat()

                        val leftX = originX + radius * cos(leftAngleRad)
                        val leftY = originY + radius * sin(leftAngleRad)
                        val rightX = originX + radius * cos(rightAngleRad)
                        val rightY = originY + radius * sin(rightAngleRad)

                        val conePath = Path().apply {
                            moveTo(originX, originY)
                            lineTo(leftX, leftY)
                            // Approximate arc with line
                            lineTo(rightX, rightY)
                            close()
                        }

                        // Fill FOV cone with translucent amber/cyan
                        drawPath(
                            path = conePath,
                            color = CyberAmber.copy(alpha = 0.15f)
                        )
                        drawPath(
                            path = conePath,
                            color = CyberAmber.copy(alpha = 0.6f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Draw Player icon (blue triangle) at origin
                        val playerPath = Path().apply {
                            moveTo(originX, originY - 14.dp.toPx())
                            lineTo(originX - 9.dp.toPx(), originY + 7.dp.toPx())
                            lineTo(originX + 9.dp.toPx(), originY + 7.dp.toPx())
                            close()
                        }
                        drawPath(playerPath, color = CyberCyan)

                        // Draw Simulated Enemies
                        if (fovState.showEnemyTargets) {
                            // Enemy 1: Right in front (visible on all screens)
                            val e1X = originX
                            val e1Y = originY - radius * 0.6f
                            drawCircle(CyberRed, radius = 5.dp.toPx(), center = Offset(e1X, e1Y))

                            // Enemy 2: Medium Left (visible on 90+ deg)
                            val e2AngleRad = (-PI / 2f - 42f * (PI / 180f).toFloat()).toFloat()
                            val e2X = originX + radius * 0.7f * cos(e2AngleRad)
                            val e2Y = originY + radius * 0.7f * sin(e2AngleRad)
                            val isE2Visible = animatedAngle >= 84f
                            drawCircle(
                                color = if (isE2Visible) CyberRed else Color(0xFF475569),
                                radius = 5.dp.toPx(),
                                center = Offset(e2X, e2Y)
                            )

                            // Enemy 3: Far Flanker Right (only visible in iPad 105°+ FOV!)
                            val e3AngleRad = (-PI / 2f + 50f * (PI / 180f).toFloat()).toFloat()
                            val e3X = originX + radius * 0.82f * cos(e3AngleRad)
                            val e3Y = originY + radius * 0.82f * sin(e3AngleRad)
                            val isE3Visible = animatedAngle >= 100f
                            drawCircle(
                                color = if (isE3Visible) CyberRed else Color(0xFF475569),
                                radius = 5.dp.toPx(),
                                center = Offset(e3X, e3Y)
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyberRed)
                        )
                        Text(text = "عدو داخل زاوية الرؤية", color = TextSecondary, fontSize = 10.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF475569))
                        )
                        Text(text = "عدو خارج الرؤية (مخفي بالهاتف)", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Text(
                    text = "ملاحظة: في شاشة الهاتف العادية (20:9)، زاوية الرؤية ضيقة (80°)، بينما منظور الآيباد (4:3) يتيح زاوية 105° إلى 115° فتظهر أطراف الأعداء على اليمين واليسار دون الحاجة لأي كشف أماكن غير قانوني.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Sensitivity Calibration Calculator for iPad FOV
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "حاسبة معايرة الحساسية لمنظور الآيباد",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "عند زيادة زاوية الرؤية (FOV)، تتحرك العناصر أسرع على الأطراف. اضبط حساسيتك الحالية لحساب الحساسية المكافئة بدقة:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                val multiplier = fovState.selectedFovType.sensMultiplier

                // Camera Slider
                SensSliderItem(
                    label = "حساسية الكاميرا (بدون منظار)",
                    currentValue = fovState.cameraSens,
                    adjustedValue = (fovState.cameraSens * multiplier).roundToInt().toFloat(),
                    onValueChange = { viewModel.updateCameraSens(it) },
                    valueRange = 40f..300f
                )

                // ADS Slider
                SensSliderItem(
                    label = "حساسية إطلاق النار (ADS)",
                    currentValue = fovState.adsSens,
                    adjustedValue = (fovState.adsSens * multiplier).roundToInt().toFloat(),
                    onValueChange = { viewModel.updateAdsSens(it) },
                    valueRange = 40f..300f
                )

                // Gyroscope Slider
                SensSliderItem(
                    label = "حساسية الجيروسكوب (Gyroscope)",
                    currentValue = fovState.gyroSens,
                    adjustedValue = (fovState.gyroSens * multiplier).roundToInt().toFloat(),
                    onValueChange = { viewModel.updateGyroSens(it) },
                    valueRange = 100f..400f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SensSliderItem(
    label: String,
    currentValue: Float,
    adjustedValue: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = TextPrimary, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${currentValue.roundToInt()}%",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(text = "➔", color = CyberCyan, fontSize = 12.sp)
                Text(
                    text = "${adjustedValue.roundToInt()}% للآيباد",
                    color = CyberAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Slider(
            value = currentValue,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = CyberCyan,
                activeTrackColor = CyberCyan,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
