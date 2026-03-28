package com.example.hotroom.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.components.EmeraldTextField
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
    var selectedCategory by remember { mutableStateOf("Ko'kat") }
    var plantedDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))) }
    var hasSensor by remember { mutableStateOf(false) }

    val categories = listOf("Sabzavot", "Ko'kat", "Gul")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Yangi namuna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = GreenPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Batafsil", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "INVENTARNI KENGAYTIRISH",
                style = MaterialTheme.typography.labelMedium,
                color = GreenPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "O'simlik qo'shish",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Avtomatlashtirilgan parvarish va o'sish monitoringini boshlash uchun issiqxonangizning yangi a'zosini ro'yxatdan o'tkazing.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Image Upload Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
                    .background(SurfaceContainerLow)
                    .clickable { /* TODO: Open Image Picker */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.PhotoCamera,
                        contentDescription = "Rasm yuklash",
                        tint = GreenPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "O'simlik rasmini yuklash",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PNG, JPG. up to 10MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Plant Name
            EmeraldTextField(
                value = plantName,
                onValueChange = { plantName = it },
                label = "O'SIMLIK NOMI",
                placeholder = "masalan: Yarim tun jadosi",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scientific Name (Navi)
            EmeraldTextField(
                value = scientificName,
                onValueChange = { scientificName = it },
                label = "NAVI",
                placeholder = "masalan: Sanseveria",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // CATEGORY (NAMUNA TURI)
            Text(
                text = "NAMUNA TURI",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    val chipBg = if (isSelected) GreenPrimary else Color.White
                    val chipBorder = if (isSelected) GreenPrimary else Color(0xFFE5E7EB)
                    val contentColor = if (isSelected) Color.White else TextPrimary
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                color = contentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Planted Date
            EmeraldTextField(
                value = plantedDate,
                onValueChange = { plantedDate = it },
                label = "EKILGAN SANA",
                placeholder = "10/24/2023",
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Add button
            Button(
                onClick = {
                    if (plantName.isNotBlank()) {
                        onAddPlant(
                            plantName,
                            scientificName.ifBlank { null },
                            selectedCategory,
                            plantedDate.ifBlank { null },
                            hasSensor,
                            24, // Default interval
                            null,
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = plantName.isNotBlank() && !isAdding
            ) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "O'SIMLIKNI QO'SHISH",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Smart Sensor Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(24.dp)) // Placeholder icon
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text("Smart datchikka ulanish", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tuproq namligi va harorat avtomatik", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = hasSensor,
                        onCheckedChange = { hasSensor = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GreenPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFD1D5DB)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
