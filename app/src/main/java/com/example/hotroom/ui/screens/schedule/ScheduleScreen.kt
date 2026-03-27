package com.example.hotroom.ui.screens.schedule

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.CareTask
import com.example.hotroom.data.model.Plant
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.ScheduleUiState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    scheduleState: ScheduleUiState,
    onSelectDate: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMarkComplete: (String) -> Unit,
    onAddTask: (title: String, taskType: String, time: String?, plantId: String?) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Parvarish Jadvali", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Vazifa qo'shish", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            val currentMonth = scheduleState.currentMonth
            val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("uz"))
                .replaceFirstChar { it.uppercase() }

            // === Calendar ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onPreviousMonth) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Oldingi oy")
                        }
                        Text("$monthName ${currentMonth.year}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = onNextMonth) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Keyingi oy")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val daysInMonth = currentMonth.lengthOfMonth()
                    val firstDay = currentMonth.atDay(1).dayOfWeek.value - 1
                    val totalSlots = firstDay + daysInMonth
                    val rows = (totalSlots + 6) / 7

                    for (row in 0 until rows) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (col in 0 until 7) {
                                val index = row * 7 + col
                                val day = index - firstDay + 1

                                Box(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (day in 1..daysInMonth) {
                                        val isSelected = day == scheduleState.selectedDate.dayOfMonth &&
                                                currentMonth == YearMonth.from(scheduleState.selectedDate)
                                        val hasTask = day in scheduleState.taskDays
                                        val isToday = LocalDate.now().dayOfMonth == day && YearMonth.now() == currentMonth

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .then(
                                                    if (isSelected) Modifier.background(GreenPrimary)
                                                    else if (isToday) Modifier.background(GreenPrimary.copy(alpha = 0.15f))
                                                    else Modifier
                                                )
                                                .clickable { onSelectDate(day) },
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = day.toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else TextPrimary
                                            )
                                            if (hasTask && !isSelected) {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(GreenPrimary))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === Tanlangan kun vazifalari ===
            Text(
                text = "${scheduleState.selectedDate.dayOfMonth}-kun vazifalari",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (scheduleState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (scheduleState.tasks.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📋", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bu kunda vazifa yo'q", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showAddDialog = true }) {
                            Text("Vazifa qo'shish", color = GreenPrimary)
                        }
                    }
                }
            } else {
                scheduleState.tasks.forEach { task ->
                    val plantName = scheduleState.plants.find { it.id == task.plantId }?.name
                    ScheduleTaskItem(task = task, plantName = plantName, onComplete = { onMarkComplete(task.id) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // === Bajarilish ===
            val completedCount = scheduleState.tasks.count { it.isCompleted }
            val totalCount = scheduleState.tasks.size
            if (totalCount > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Bajarilish", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("$completedCount / $totalCount vazifa", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            val percent = if (totalCount > 0) (completedCount * 100 / totalCount) else 0
                            Text("$percent%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = GreenPrimary,
                            trackColor = GreenPrimaryContainer,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            plants = scheduleState.plants,
            onDismiss = { showAddDialog = false },
            onAdd = { title, type, time, plantId ->
                onAddTask(title, type, time, plantId)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    plants: List<Plant>,
    onDismiss: () -> Unit,
    onAdd: (title: String, taskType: String, time: String?, plantId: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("watering") }
    var time by remember { mutableStateOf("") }
    var selectedPlantId by remember { mutableStateOf<String?>(null) }
    var plantDropdownExpanded by remember { mutableStateOf(false) }

    val taskTypes = listOf(
        "watering" to "💧 Sug'orish",
        "fertilizing" to "🧪 O'g'itlash",
        "pruning" to "✂️ Kesish",
        "inspection" to "🔍 Tekshirish"
    )

    val selectedPlantName = plants.find { it.id == selectedPlantId }?.name ?: "Tanlanmagan"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yangi vazifa", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Vazifa nomi
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Vazifa nomi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // O'simlik tanlash
                Text("O'simlik:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = plantDropdownExpanded,
                    onExpandedChange = { plantDropdownExpanded = !plantDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPlantName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary)
                    )
                    ExposedDropdownMenu(
                        expanded = plantDropdownExpanded,
                        onDismissRequest = { plantDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tanlanmagan (umumiy vazifa)") },
                            onClick = {
                                selectedPlantId = null
                                plantDropdownExpanded = false
                            }
                        )
                        plants.forEach { plant ->
                            DropdownMenuItem(
                                text = { Text("🌿 ${plant.name}") },
                                onClick = {
                                    selectedPlantId = plant.id
                                    plantDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Turi
                Text("Turi:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    taskTypes.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { (type, label) ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Vaqt
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Vaqt (ixtiyoriy)") },
                    placeholder = { Text("08:00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title, selectedType, time.ifBlank { null }, selectedPlantId) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = title.isNotBlank()
            ) {
                Text("Qo'shish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ScheduleTaskItem(
    task: CareTask,
    plantName: String?,
    onComplete: () -> Unit
) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(50.dp)
            ) {
                Text(
                    text = task.scheduledTime ?: "—",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimaryDark
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (task.isCompleted) GreenPrimary else GreenPrimaryLight)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isCompleted) TextSecondary else TextPrimary
                )
                Row {
                    if (plantName != null) {
                        Text("🌿 $plantName", style = MaterialTheme.typography.bodySmall, color = GreenPrimary)
                        Text(" • ", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Text(
                        text = when (task.taskType) {
                            "watering" -> "Sug'orish"
                            "fertilizing" -> "O'g'itlash"
                            "pruning" -> "Kesish"
                            "inspection" -> "Tekshirish"
                            else -> task.taskType
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onComplete) {
                if (task.isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Bajarildi", tint = GreenPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Bajarish", tint = TextSecondary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
