package com.example.hotroom.ui.screens.sensors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.SensorUiState

@Composable
fun SensorScreen(
    sensorState: SensorUiState,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Region
            Column {
                Text(
                    text = "JONLI MA'LUMOTLAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Datchiklar va statistika",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
            }

            // Iqlim Holati Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iqlim holati",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 12.dp.toPx()
                            val sizeValue = size.width - strokeWidth
                            
                            // Background full track
                            drawArc(
                                color = SurfaceContainerLow,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(strokeWidth/2, strokeWidth/2),
                                size = Size(sizeValue, sizeValue),
                                style = Stroke(width = strokeWidth)
                            )
                            
                            // Value track (82%)
                            drawArc(
                                color = GreenPrimaryDark,
                                startAngle = -90f,
                                sweepAngle = 360f * 0.82f,
                                useCenter = false,
                                topLeft = Offset(strokeWidth/2, strokeWidth/2),
                                size = Size(sizeValue, sizeValue),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        
                        // Icon Overlay Top Right
                        Icon(
                            Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color(0xFFE5E7EB),
                            modifier = Modifier
                                .size(60.dp)
                                .offset(x = 40.dp, y = (-40).dp)
                        )
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "82%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "OPTIMAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimaryDark,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Umumiy sharoitlar barcha 4\nzona uchun ideal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Faol ogohlantirishlar
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Faol ogohlantirishlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFE4E6))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "2 KRITIK",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }

                // Alert 1 (Critical)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(ErrorRed))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFE4E6)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text("2 Kritik", style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Zone D da past namlik", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Text("2m ago", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Namlik tsikli soat 10:45 ga rejalashtirilgan. Joriy daraja: 38%.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Alert 2 (Info)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.fillMaxHeight().width(4.dp).background(InfoBlue))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0F2FE)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Zone D da past namlik", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Text("1hr ago", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Namlik tsikli soat 10:45 ga rejalashtirilgan, Joriy daraja: 38%.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Charts
            ChartCard(
                title = "HARORAT",
                value = "24.5°C",
                trend = "+0.8°",
                trendPositive = true,
                icon = Icons.Default.Thermostat,
                iconBg = Color(0xFFE8F5E9),
                iconTint = GreenPrimary,
                chartContent = {
                    BarChartMock(colors = listOf(
                        GreenPrimary.copy(alpha=0.3f), GreenPrimary.copy(alpha=0.2f), 
                        GreenPrimary.copy(alpha=0.4f), GreenPrimary.copy(alpha=0.2f),
                        GreenPrimaryDark, GreenPrimary.copy(alpha=0.8f)
                    ), heights = listOf(0.4f, 0.3f, 0.6f, 0.5f, 0.9f, 0.7f))
                }
            )

            ChartCard(
                title = "NAMLIK",
                value = "62%",
                trend = "-4%",
                trendPositive = false,
                icon = Icons.Default.WaterDrop,
                iconBg = Color(0xFFE0F2FE),
                iconTint = InfoBlue,
                chartContent = {
                    BarChartMock(colors = listOf(
                        InfoBlue.copy(alpha=0.2f), InfoBlue.copy(alpha=0.3f),
                        InfoBlue.copy(alpha=0.4f), InfoBlue.copy(alpha=0.2f),
                        Color(0xFF0369A1), InfoBlue
                    ), heights = listOf(0.5f, 0.6f, 0.4f, 0.3f, 0.2f, 0.4f))
                }
            )

            ChartCard(
                title = "YORUG'LIK",
                value = "12.4k",
                valueSuffix = "LUXS",
                tagText = "LYUKS",
                tagBg = AccentOrange.copy(alpha = 0.2f),
                tagTint = AccentOrange,
                chartContent = {
                    AreaChartMock(chartColor = AccentOrange.copy(alpha = 0.2f))
                }
            )

            // Zona ko'rsatkichlari Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Zona ko'rsatkichlari",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("ZONA", style = MaterialTheme.typography.labelSmall.copy(fontSize=9.sp), color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                        Text("HOLATI", style = MaterialTheme.typography.labelSmall.copy(fontSize=9.sp), color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("P/H DARAJASi", style = MaterialTheme.typography.labelSmall.copy(fontSize=9.sp), color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("CO2", style = MaterialTheme.typography.labelSmall.copy(fontSize=9.sp), color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                        Text("VPD", style = MaterialTheme.typography.labelSmall.copy(fontSize=9.sp), color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Row 1
                    TableRow(zona = "Zone A -\nTropical", isOptimal = true, ph = "6.2", co2 = "410", vpd = "0.85")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Row 2
                    TableRow(zona = "Zone B -\nSeedling", isOptimal = false, ph = "5.8", co2 = "395", vpd = "1.20")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Row 3
                    TableRow(zona = "Zone C -\nSucculent", isOptimal = true, ph = "7.1", co2 = "405", vpd = "0.95")
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    value: String,
    valueSuffix: String = "",
    trend: String = "",
    trendPositive: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconBg: Color = Color.Transparent,
    iconTint: Color = Color.Transparent,
    tagText: String? = null,
    tagBg: Color = Color.Transparent,
    tagTint: Color = Color.Transparent,
    chartContent: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (valueSuffix.isNotEmpty()) {
                            Text(
                                text = " $valueSuffix",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        if (trend.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = trend,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (trendPositive) GreenPrimary else InfoBlue,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
                
                if (tagText != null) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(tagBg).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(tagText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = tagTint)
                    }
                } else if (icon != null) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                chartContent()
            }
        }
    }
}

@Composable
fun BarChartMock(colors: List<Color>, heights: List<Float>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(height)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(colors[index])
            )
        }
    }
}

@Composable
fun AreaChartMock(chartColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val w = size.width
        val h = size.height
        
        path.moveTo(0f, h)
        // Draw some smooth curves
        path.cubicTo(w*0.2f, h*0.8f, w*0.4f, h*0.2f, w*0.6f, h*0.5f)
        path.cubicTo(w*0.8f, h*0.8f, w*0.9f, h*0.1f, w, 0f)
        path.lineTo(w, h)
        path.close()
        
        drawPath(
            path = path,
            color = chartColor,
            style = Fill
        )
    }
}

@Composable
fun TableRow(zona: String, isOptimal: Boolean, ph: String, co2: String, vpd: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(zona, style = MaterialTheme.typography.labelMedium.copy(lineHeight=14.sp), color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isOptimal) GreenPrimary else ErrorRed))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isOptimal) "Optimal" else "Kritik", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }
        
        Text("$ph pH", style = MaterialTheme.typography.bodySmall, color = TextPrimary, modifier = Modifier.weight(1f))
        
        Column(modifier = Modifier.weight(0.7f)) {
            Text(co2, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text("ppm", style = MaterialTheme.typography.bodySmall.copy(fontSize=9.sp), color = TextSecondary)
        }
        
        Column(modifier = Modifier.weight(0.8f)) {
            Text(vpd, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text("kPa", style = MaterialTheme.typography.bodySmall.copy(fontSize=9.sp), color = TextSecondary)
        }
    }
}
