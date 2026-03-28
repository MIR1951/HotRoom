package com.example.hotroom.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.data.model.Plant
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    homeState: HomeUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onMarkTaskComplete: (String) -> Unit,
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting text
            Text(
                text = "HOZIRGI HOLAT",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sizning bog'ingiz",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "gullab-yashnamoqda.",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = GreenPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Optimal Conditions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "O'simlik salomatligi",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${homeState.averageHealth}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "OPTIMAL SHAROITLAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    // Decorative shapes (placeholder for SVG stars)
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = GreenPrimary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 32.dp, y = (-10).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hayotiy ko'rsatkichlar Card
            val temp = homeState.latestReading?.temperature ?: 24.0f
            val humidity = homeState.latestReading?.humidity ?: 65.0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hayotiy ko'rsatkichlar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Temp Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(InfoBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Thermostat, contentDescription = null, tint = InfoBlue)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Harorat", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Text(
                                    text = "${String.format("%.0f", temp)}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Humidity Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(InfoBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = InfoBlue)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Namlik", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Text(
                                    text = "${String.format("%.0f", humidity)}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bugungi vazifalar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bugungi vazifalar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Jadvalni ko'rish",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary,
                            modifier = Modifier.clickable { onNavigateToSchedule() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (homeState.todayTasks.isEmpty()) {
                        Text(
                            text = "Bugungi vazifa yo'q",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            homeState.todayTasks.forEach { task ->
                                val plant = homeState.plants.find { it.id == task.plantId }
                                TaskRow(task = task, plant = plant, onComplete = { onMarkTaskComplete(task.id) })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tanlangan Namuna Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2C3E2D)))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                startY = 100f
                            ))
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TANLANGAN NAMUNA",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Monstera Deliciosa",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "O'sish sikli cho'qqisiga yetmoqda. Optimal namlik\n70% da saqlanmoqda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
        
        FloatingActionButton(
            onClick = onNavigateToAddPlant,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp),
            containerColor = GreenPrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Qo'shish")
        }
    }
}

@Composable
fun TaskRow(
    task: CareTask,
    plant: Plant?,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🪴", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                val zoneText = plant?.zone ?: "Umumiy hudud"
                val timeText = task.scheduledTime ?: "08:00 AM"
                Text(
                    text = "$zoneText • $timeText",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (task.isCompleted) GreenPrimary else Color(0xFFD1D5DB), CircleShape)
                    .background(if (task.isCompleted) GreenPrimary else Color.Transparent)
                    .clickable { onComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
