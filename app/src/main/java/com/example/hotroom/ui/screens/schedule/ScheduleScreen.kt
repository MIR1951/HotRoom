package com.example.hotroom.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.ScheduleUiState
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleScreen(
    scheduleState: ScheduleUiState,
    onSelectDate: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMarkComplete: (String) -> Unit,
    onAddTask: (title: String, taskType: String, time: String?, plantId: String?) -> Unit
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

            // Header Region
            Text(
                text = "KUNDALIK TARTIB",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Parvarish jadvali",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Calendar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val currentMonth = scheduleState.currentMonth
                    val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("uz")).replaceFirstChar { it.uppercase() }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$monthName ${currentMonth.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Dummy repeated text in image "SESH SESH" - Maybe representing controls
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary, modifier = Modifier.clickable { onPreviousMonth() })
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.clickable { onNextMonth() })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Days Row
                    val daysInMonth = currentMonth.lengthOfMonth()
                    // Generating a snippet around selected date for better view matching Image 3 layout context
                    val startDay = maxOf(1, scheduleState.selectedDate.dayOfMonth - 3)
                    val daysList = (startDay..minOf(daysInMonth, startDay + 6)).toList()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysList.forEach { day ->
                            val isSelected = day == scheduleState.selectedDate.dayOfMonth
                            val date = currentMonth.atDay(day)
                            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("uz")).uppercase()
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isSelected) GreenPrimary else Color.Transparent)
                                    .clickable { onSelectDate(day) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = dayOfWeek,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bugungi vazifalar Header
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
                    text = "${scheduleState.tasks.size} Eslatmalar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            if (scheduleState.tasks.isEmpty()) {
                Text(
                    text = "Bugungi vazifa yo'q",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                scheduleState.tasks.forEachIndexed { index, task ->
                    // To match the nice styling we vary the icons slightly
                    val isWatering = task.taskType.lowercase().contains("watering") || index % 3 == 0
                    val isFertilizing = task.taskType.lowercase().contains("fertilizer") || index % 3 == 1
                    
                    val iconColor = if (isWatering) InfoBlue else if (isFertilizing) AccentOrange else GreenPrimary
                    val iconBg = if (isWatering) InfoBlue.copy(alpha=0.15f) else if (isFertilizing) AccentOrange.copy(alpha=0.15f) else GreenPrimary.copy(alpha=0.15f)
                    val iconRes = if (isWatering) Icons.Default.WaterDrop else if (isFertilizing) Icons.Default.Science else Icons.Default.Spa
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconRes, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                val topTagText = task.scheduledTime?.let { "${it.substringBefore("T")} • " } ?: "8:00 AM • "
                                val typeText = task.taskType.replaceFirstChar { it.uppercase() }
                                Text(
                                    text = "$topTagText $typeText",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description ?: "Tavsifi kiritilmagan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (task.isCompleted) GreenPrimary else Color(0xFFD1D5DB), CircleShape)
                                    .background(if (task.isCompleted) GreenPrimary else Color.Transparent)
                                    .clickable { onMarkComplete(task.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // O'sish Natijasi Card
            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimaryDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.radialGradient(
                        colors = listOf(GreenPrimary, GreenPrimaryDark),
                        center = androidx.compose.ui.geometry.Offset(800f, 100f),
                        radius = 800f
                    )
                )) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "O'sish natijasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Siz haftalik parvarish\ntartibining 75% qismini\nbajardingiz!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Progress Bar
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(0.75f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.25f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha=0.3f))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navbatda Card
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8B807B))) // Placeholder for greenhouse image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 100f
                            ))
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "NAVBATDA",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Butash jarayoni",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ertaga soat 9:00 da Fikuslar\nkolleksiyasi uchun.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
