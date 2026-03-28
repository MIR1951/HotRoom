package com.example.hotroom.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plantState: PlantUiState,
    onNavigateToAddPlant: () -> Unit,
    onSearchChange: (String) -> Unit,
    onWaterPlant: (String) -> Unit,
    onDeletePlant: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
         LazyColumn(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(horizontal = 24.dp),
             contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
         ) {
             // Header Section
             item {
                 Text(
                     text = "Bizning o'simliklarimiz",
                     style = MaterialTheme.typography.headlineLarge,
                     fontWeight = FontWeight.ExtraBold,
                     color = TextPrimary
                 )
                 Spacer(modifier = Modifier.height(8.dp))
                 Text(
                     text = "Har bir barg bilan jonli raqamli issiqxonani barpo etamiz.",
                     style = MaterialTheme.typography.bodyMedium,
                     color = TextSecondary,
                     lineHeight = 20.sp
                 )
                 Spacer(modifier = Modifier.height(24.dp))
             }

             // Search and Filter Bar
             item {
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.spacedBy(16.dp),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     TextField(
                         value = plantState.searchQuery,
                         onValueChange = onSearchChange,
                         placeholder = {
                             Text(
                                 "Issiqxonangizdan qidiring...",
                                 color = TextSecondary,
                                 style = MaterialTheme.typography.bodyMedium
                             )
                         },
                         leadingIcon = {
                             Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary)
                         },
                         modifier = Modifier
                             .weight(1f)
                             .height(56.dp),
                         shape = RoundedCornerShape(28.dp),
                         colors = TextFieldDefaults.colors(
                             unfocusedIndicatorColor = Color.Transparent,
                             focusedIndicatorColor = Color.Transparent,
                             focusedContainerColor = Color.White,
                             unfocusedContainerColor = Color.White
                         ),
                         singleLine = true
                     )
                     
                     Box(
                         modifier = Modifier
                             .size(56.dp)
                             .clip(CircleShape)
                             .background(Color.White)
                             .clickable { /* Filter Action */ },
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(Icons.Outlined.Tune, contentDescription = "Filtrlash", tint = TextPrimary)
                     }
                 }
                 Spacer(modifier = Modifier.height(24.dp))
             }

             // Plant List
             if (plantState.isLoading) {
                 item {
                     Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = GreenPrimary)
                     }
                 }
             } else {
                 items(plantState.plants) { plant ->
                     PlantFullWidthCard(plant = plant, onClick = { /* Navigate to detail */ })
                     Spacer(modifier = Modifier.height(24.dp))
                 }
             }

             // Add Plant Placeholder Card
             item {
                 Card(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { onNavigateToAddPlant() },
                     shape = RoundedCornerShape(24.dp),
                     colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                 ) {
                     Box(
                         modifier = Modifier
                             .fillMaxWidth()
                             .border(
                                 width = 2.dp,
                                 color = Color(0xFFD1D5DB),
                                 shape = RoundedCornerShape(24.dp)
                             )
                             .padding(vertical = 32.dp),
                         contentAlignment = Alignment.Center
                     ) {
                         // Dotted border is simulated via simple border on this snippet without writing complex path logic, 
                         // but it visually represents the "Add" block. To make true dotted border, standard drawBehind is needed.
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Box(
                                 modifier = Modifier
                                     .size(48.dp)
                                     .clip(CircleShape)
                                     .background(Color(0xFFF3F4F6)),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(Icons.Default.Add, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                             }
                             Spacer(modifier = Modifier.height(16.dp))
                             Text(
                                 text = "Yangi o'simlik qo'shish",
                                 style = MaterialTheme.typography.titleMedium,
                                 fontWeight = FontWeight.Bold,
                                 color = TextPrimary
                             )
                             Spacer(modifier = Modifier.height(4.dp))
                             Text(
                                 text = "Issiqxonangiz kolleksiyasini kengaytiring",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = TextSecondary
                             )
                         }
                     }
                 }
                 Spacer(modifier = Modifier.height(24.dp))
             }
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
fun PlantFullWidthCard(
    plant: Plant,
    onClick: () -> Unit
) {
    val healthTag = if (plant.healthStatus >= 80) "O'SISHDA" else "DIQQAT"
    val tagBg = if (plant.healthStatus >= 80) GreenPrimary else InfoBlue
    
    // Status Logic for the bottom row
    // We mock logic to show either "34% Namlik", "2 soat oldin (sug'orish)", "Yorqin yorug'lik", "Tuproq quruq"
    val isPrimaryAction = plant.healthStatus < 80 // To make the button Green or Grey
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.DarkGray) // Placeholder for photo
            ) {
                // Top-left Tag
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = healthTag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                     )
                }
            }
            
            // Info Area
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.scientificName ?: "Noma'lum tur",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val labelText = if (plant.healthStatus >= 80) "NAMLIK" else "OXIRGI SUG'ORISH"
                        val valText = if (plant.healthStatus >= 80) "34% Namlik" else "2 soat oldin"
                        val valColor = if (plant.healthStatus >= 80) InfoBlue else GreenPrimary
                        
                        Text(
                            text = labelText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = valText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = valColor
                        )
                    }
                    
                    // BATAFSIL Button
                    val btnBg = if (isPrimaryAction) GreenPrimary else SurfaceContainerLow
                    val btnText = if (isPrimaryAction) Color.White else TextPrimary
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(btnBg)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "BATAFSIL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = btnText,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
