package com.example.hotroom.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.theme.*
import com.example.hotroom.ui.viewmodel.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileState: ProfileUiState,
    initials: String,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    onUpdateNotifications: (watering: Boolean, temperature: Boolean) -> Unit
) {
    val profile = profileState.profile

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onNavigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga", tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = GreenPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar & Profile Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD1D5DB)),
                            contentAlignment = Alignment.Center
                        ) {
                            // In real app use Image, for now just text
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        // Edit Badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .clip(CircleShape)
                                .border(2.dp, SurfaceContainerLow, CircleShape)
                                .background(GreenPrimary)
                                .clickable { /* Edit Photo */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profile?.name ?: "Alex Rivers",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = profile?.role ?: "Head Curator",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEDD5))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Premium Member",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE0F2FE))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "128 Plants",
                                style = MaterialTheme.typography.labelSmall,
                                color = InfoBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Shaxsiy ma'lumotlar
            SettingsSection(
                icon = Icons.Default.Person,
                iconTint = GreenPrimary,
                iconBg = GreenPrimary.copy(alpha = 0.15f),
                title = "Shaxsiy ma'lumotlar"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("ISM", profile?.name ?: "Alex Rivers")
                    ReadOnlyField("EMAIL", profileState.email.takeIf { it.isNotEmpty() } ?: "alex.rivers@greenhouse.io")
                    ReadOnlyFieldWithIcon("PAROL", "••••••••••••", Icons.Outlined.Visibility)
                }
            }

            // Issiqxona sozlamalari
            SettingsSection(
                icon = Icons.Default.Spa,
                iconTint = InfoBlue,
                iconBg = InfoBlue.copy(alpha = 0.15f),
                title = "Issiqxona sozlamalari"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(Icons.Default.LocationOn, "Joylashuv", profile?.greenhouseLocation ?: "Portland, OR")
                    InfoRow(Icons.Default.ChangeHistory, "Turi", profile?.greenhouseType ?: "Tropical Dome")
                    InfoRow(Icons.Default.Straighten, "Maydoni", if (profile?.greenhouseArea != null) "${profile.greenhouseArea} m²" else "450 m²")
                }
            }

            // Bildirishnomalar
            SettingsSection(
                icon = Icons.Default.Notifications,
                iconTint = AccentOrange,
                iconBg = AccentOrange.copy(alpha = 0.15f),
                title = "Bildirishnomalar"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SwitchRow(Icons.Default.WaterDrop, "Sug'orish", profile?.notifyWatering ?: true) { 
                        onUpdateNotifications(it, profile?.notifyTemperature ?: true)
                    }
                    SwitchRow(Icons.Default.Thermostat, "Harorat", profile?.notifyTemperature ?: true) { 
                        onUpdateNotifications(profile?.notifyWatering ?: true, it)
                    }
                    SwitchRow(Icons.Default.Article, "Yangiliklar", false) { /* Custom logic */ }
                }
            }

            // Ilova sozlamalari
            SettingsSection(
                icon = Icons.Default.Settings,
                iconTint = TextSecondary,
                iconBg = Color(0xFFF3F4F6),
                title = "Ilova sozlamalari"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Mavzu Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Mavzu", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Light theme selected
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White).border(2.dp, GreenPrimary, CircleShape))
                            // Dark theme
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Black))
                        }
                    }

                    // Til Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Til", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("O'zbekcha", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSignOut() }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tizimdan chiqish",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SettingsSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
            letterSpacing = 1.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}

@Composable
fun ReadOnlyFieldWithIcon(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
            letterSpacing = 1.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, letterSpacing = 2.sp)
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
    }
}

@Composable
fun SwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenPrimaryDark,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            )
        )
    }
}
