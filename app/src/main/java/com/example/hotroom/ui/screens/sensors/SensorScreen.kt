package com.example.hotroom.ui.screens.sensors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.SensorReading
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.SensorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(
    sensorState: SensorUiState,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Datchiklar va Statistika",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yangilash", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (sensorState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                if (sensorState.latestReadings.isEmpty()) {
                    // Ma'lumot yo'q holati
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📡", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sensor ma'lumotlari yo'q",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Datchiklar ulanganda ma'lumotlar\nbu yerda ko'rsatiladi",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    // Temperature Card with Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HARORAT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.Thermostat,
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val currentTemp = sensorState.currentTemperature
                                Text(
                                    text = if (currentTemp != null) "${String.format("%.1f", currentTemp)}°C" else "—",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Temperature bar chart
                            if (sensorState.temperatureHistory.isNotEmpty()) {
                                TemperatureBarChart(sensorState.temperatureHistory)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Humidity Card with Chart
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NAMLIK",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = InfoBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val currentHumidity = sensorState.currentHumidity
                                Text(
                                    text = if (currentHumidity != null) "${String.format("%.0f", currentHumidity)}%" else "—",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Humidity line chart
                            if (sensorState.humidityHistory.isNotEmpty()) {
                                HumidityLineChart(sensorState.humidityHistory)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Zone summary
                    if (sensorState.zoneReadings.isNotEmpty()) {
                        Text(
                            text = "Zona holati",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val zones = sensorState.zoneReadings.entries.toList()
                        zones.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                pair.forEach { (zone, reading) ->
                                    val statusColor = if ((reading.humidity ?: 0f) < 40f) AccentOrange else GreenPrimary
                                    ZoneCard(
                                        zone = zone,
                                        temp = if (reading.temperature != null) "${String.format("%.1f", reading.temperature)}°C" else "—",
                                        humidity = if (reading.humidity != null) "${String.format("%.0f", reading.humidity)}%" else "—",
                                        statusColor = statusColor,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun TemperatureBarChart(values: List<Float>) {
    if (values.isEmpty()) return
    val maxVal = values.maxOrNull() ?: 1f
    val normalizedValues = values.map { it / maxVal }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val barWidth = size.width / (normalizedValues.size * 2)
        val spacing = barWidth
        val maxHeight = size.height

        normalizedValues.forEachIndexed { index, value ->
            val x = index * (barWidth + spacing) + spacing / 2
            val barHeight = value * maxHeight
            val y = maxHeight - barHeight

            drawRoundRect(
                color = ChartGreenMedium,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}

@Composable
private fun HumidityLineChart(values: List<Float>) {
    if (values.isEmpty()) return
    val maxVal = values.maxOrNull() ?: 1f
    val normalizedValues = values.map { it / maxVal }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val spacing = size.width / (normalizedValues.size - 1).coerceAtLeast(1)
        val maxHeight = size.height

        val path = Path()
        normalizedValues.forEachIndexed { index, value ->
            val x = index * spacing
            val y = maxHeight - (value * maxHeight)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = ChartGreenDark,
            style = Stroke(width = 3f)
        )

        normalizedValues.forEachIndexed { index, value ->
            val x = index * spacing
            val y = maxHeight - (value * maxHeight)
            drawCircle(
                color = ChartGreenDark,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun ZoneCard(
    zone: String,
    temp: String,
    humidity: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Zona $zone",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🌡️ $temp",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "💧 $humidity",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
