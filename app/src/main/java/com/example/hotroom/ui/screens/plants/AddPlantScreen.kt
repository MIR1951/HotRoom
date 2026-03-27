package com.example.hotroom.ui.screens.plants

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    isAdding: Boolean,
    onAddPlant: (name: String, scientificName: String?, category: String, plantedDate: String?, hasSensor: Boolean, wateringIntervalHours: Int, zone: String?, notes: String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    var plantName by remember { mutableStateOf("") }
    var scientificName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Sabzavot") }
    var plantedDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var hasSensor by remember { mutableStateOf(false) }
    var wateringInterval by remember { mutableStateOf("24") }
    var zone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Sabzavot", "Ko'kat", "Gul", "Meva")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Yangi O'simlik",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            Spacer(modifier = Modifier.height(8.dp))

            // O'simlik nomi
            SectionLabel("O'SIMLIK NOMI *")
            OutlinedTextField(
                value = plantName,
                onValueChange = { plantName = it },
                placeholder = { Text("Masalan: Pomidor") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = greenFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ilmiy nomi
            SectionLabel("ILMIY NOMI (ixtiyoriy)")
            OutlinedTextField(
                value = scientificName,
                onValueChange = { scientificName = it },
                placeholder = { Text("Masalan: Solanum lycopersicum") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = greenFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Toifasi
            SectionLabel("TOIFASI")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = TextOnGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sug'orish oralig'i va Zona
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("SUG'ORISH ORALIG'I (soat)")
                    OutlinedTextField(
                        value = wateringInterval,
                        onValueChange = { wateringInterval = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("24") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp)) },
                        colors = greenFieldColors()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("ZONA (ixtiyoriy)")
                    OutlinedTextField(
                        value = zone,
                        onValueChange = { zone = it },
                        placeholder = { Text("A1") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp)) },
                        colors = greenFieldColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ekilgan sana
            SectionLabel("EKILGAN SANA")
            OutlinedTextField(
                value = plantedDate,
                onValueChange = { plantedDate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GreenPrimary) },
                singleLine = true,
                placeholder = { Text("2024-01-15") },
                colors = greenFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Eslatmalar
            SectionLabel("ESLATMALAR (ixtiyoriy)")
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("O'simlik haqida qo'shimcha ma'lumot...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 4,
                colors = greenFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Smart Sensor Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Smart datchikka ulanish", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("Tuproq namligi va harorat avtomatik", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Switch(
                        checked = hasSensor,
                        onCheckedChange = { hasSensor = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = GreenPrimary, checkedThumbColor = SurfaceLight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Add button
            Button(
                onClick = {
                    if (plantName.isNotBlank()) {
                        val interval = wateringInterval.toIntOrNull() ?: 24
                        onAddPlant(
                            plantName,
                            scientificName.ifBlank { null },
                            selectedCategory,
                            plantedDate.ifBlank { null },
                            hasSensor,
                            interval,
                            zone.ifBlank { null },
                            notes.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = plantName.isNotBlank() && !isAdding
            ) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("O'SIMLIKNI QO'SHISH", style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun greenFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenPrimary,
    cursorColor = GreenPrimary,
    focusedLabelColor = GreenPrimary
)
