package com.example.hotroom.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.data.model.Plant
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.HomeUiState
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: HomeUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onMarkTaskComplete: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌿", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Issiqxona",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimaryDark
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profil", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPlant,
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yangi o'simlik")
            }
        }
    ) { padding ->
        if (homeState.isLoading) {
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
                // === Health + Stats Card ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenPrimary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (homeState.totalPlants == 0) "O'simlik qo'shing"
                                else "${homeState.totalPlants} ta o'simlik",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    homeState.totalPlants == 0 -> "Hali o'simlik yo'q"
                                    homeState.averageHealth >= 80 -> "Yaxshi holat ✨"
                                    homeState.averageHealth >= 50 -> "O'rtacha holat"
                                    else -> "⚠️ E'tibor kerak"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            if (homeState.totalTasksToday > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📋 ${homeState.completedTasksToday}/${homeState.totalTasksToday} vazifa bajarildi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (homeState.totalPlants == 0) "—" else "${homeState.averageHealth}%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // === Sensor Stats ===
                val temp = homeState.latestReading?.temperature
                val humidity = homeState.latestReading?.humidity

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Thermostat, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Harorat", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (temp != null) "${String.format("%.1f", temp)}°C" else "—",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Namlik", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (humidity != null) "${String.format("%.0f", humidity)}%" else "—",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // === Sug'orish kerak bo'lgan o'simliklar ===
                if (homeState.plantsNeedingWater.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💧 Sug'orish kerak",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${homeState.plantsNeedingWater.size} ta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    homeState.plantsNeedingWater.take(3).forEach { plant ->
                        NeedWaterCard(plant = plant)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === Bugungi vazifalar ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bugungi vazifalar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onNavigateToSchedule) {
                        Text("Jadvalni ko'rish", color = GreenPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (homeState.todayTasks.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✅", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bugungi vazifa yo'q",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    homeState.todayTasks.forEach { task ->
                        val plantName = homeState.plants.find { it.id == task.plantId }?.name
                        TaskItem(
                            task = task,
                            plantName = plantName,
                            onComplete = { onMarkTaskComplete(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun NeedWaterCard(plant: Plant) {
    val waterText = if (plant.lastWatered != null) {
        try {
            val last = Instant.parse(plant.lastWatered)
            val diff = Duration.between(last, Instant.now())
            when {
                diff.toHours() < 24 -> "${diff.toHours()} soat oldin"
                else -> "${diff.toDays()} kun oldin"
            }
        } catch (e: Exception) { "Noma'lum" }
    } else "Hech qachon"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌱", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Oxirgi sug'orish: $waterText",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentOrange
                )
            }
            Icon(
                Icons.Default.WaterDrop,
                contentDescription = null,
                tint = AccentOrange,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: CareTask,
    plantName: String?,
    onComplete: () -> Unit
) {
    val iconData = when (task.taskType) {
        "watering" -> Pair(Icons.Default.WaterDrop, InfoBlue)
        "fertilizing" -> Pair(Icons.Default.Science, AccentOrange)
        "pruning" -> Pair(Icons.Default.ContentCut, GreenPrimary)
        "inspection" -> Pair(Icons.Default.Visibility, GreenPrimaryDark)
        else -> Pair(Icons.Default.Spa, GreenPrimary)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconData.second.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconData.first, contentDescription = null, tint = iconData.second, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    if (plantName != null) {
                        Text(
                            text = "🌿 $plantName",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenPrimary
                        )
                        Text(text = " • ", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Text(
                        text = task.scheduledTime ?: task.taskType,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            IconButton(onClick = onComplete) {
                if (task.isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Bajarildi", tint = GreenPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Bajarilmagan", tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
