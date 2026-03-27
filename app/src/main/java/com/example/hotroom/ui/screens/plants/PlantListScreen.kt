package com.example.hotroom.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.data.model.Plant
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.PlantUiState
import java.time.Instant
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plantState: PlantUiState,
    onNavigateToAddPlant: () -> Unit,
    onSearchChange: (String) -> Unit,
    onWaterPlant: (String) -> Unit,
    onDeletePlant: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "O'simliklar Ro'yxati",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = plantState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("O'simliklarni qidirish...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = GreenPrimary,
                    unfocusedContainerColor = SurfaceLight,
                    focusedContainerColor = SurfaceLight,
                    cursorColor = GreenPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (plantState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (plantState.plants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌱", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Hali o'simlik qo'shilmagan", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onNavigateToAddPlant,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("O'simlik qo'shish")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(plantState.plants) { plant ->
                        PlantCard(
                            plant = plant,
                            onWater = { onWaterPlant(plant.id) },
                            onDelete = { onDeletePlant(plant.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    onWater: () -> Unit,
    onDelete: () -> Unit
) {
    val emoji = when (plant.category.lowercase()) {
        "sabzavot" -> "🥬"
        "ko'kat" -> "🌿"
        "gul" -> "🌸"
        "meva" -> "🍎"
        else -> "🌱"
    }

    val healthColor = when {
        plant.healthStatus >= 80 -> GreenPrimary
        plant.healthStatus >= 50 -> AccentOrange
        else -> ErrorRed
    }

    val waterText = if (plant.lastWatered != null) {
        try {
            val lastWatered = Instant.parse(plant.lastWatered)
            val diff = Duration.between(lastWatered, Instant.now())
            when {
                diff.toMinutes() < 60 -> "${diff.toMinutes()} daqiqa oldin"
                diff.toHours() < 24 -> "${diff.toHours()} soat oldin"
                else -> "${diff.toDays()} kun oldin"
            }
        } catch (e: Exception) { "Noma'lum" }
    } else "Hali sug'orilmagan"

    // Sug'orish muddati o'tdimi?
    val needsWater = if (plant.lastWatered != null) {
        try {
            val lastWatered = Instant.parse(plant.lastWatered)
            val hoursSince = Duration.between(lastWatered, Instant.now()).toHours()
            hoursSince >= plant.wateringIntervalHours
        } catch (e: Exception) { true }
    } else true

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (plant.scientificName != null) {
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    if (plant.zone != null) {
                        Text(
                            text = "📍 Zona ${plant.zone}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Quick water button
                IconButton(onClick = onWater) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = "Sug'orish",
                        tint = if (needsWater) AccentOrange else InfoBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Health progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sog'ligi", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(
                    text = "${plant.healthStatus}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { plant.healthStatus / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = healthColor,
                trackColor = healthColor.copy(alpha = 0.15f),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💧 ", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = waterText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (needsWater) AccentOrange else GreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "⏱ Har ${plant.wateringIntervalHours} soatda",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}
