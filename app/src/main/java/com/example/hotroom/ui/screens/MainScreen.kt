package com.example.hotroom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hotroom.ui.screens.home.HomeScreen
import com.example.hotroom.ui.screens.plants.PlantListScreen
import com.example.hotroom.ui.screens.schedule.ScheduleScreen
import com.example.hotroom.ui.screens.sensors.SensorScreen
import com.example.hotroom.ui.theme.GreenPrimary
import com.example.hotroom.ui.theme.GreenPrimaryDark
import com.example.hotroom.ui.theme.TextSecondary
import com.example.hotroom.ui.viewmodel.HomeViewModel
import com.example.hotroom.ui.viewmodel.PlantViewModel
import com.example.hotroom.ui.viewmodel.ScheduleViewModel
import com.example.hotroom.ui.viewmodel.SensorViewModel

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToAddPlant: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeViewModel: HomeViewModel = viewModel()
    val plantViewModel: PlantViewModel = viewModel()
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val sensorViewModel: SensorViewModel = viewModel()

    // PlantViewModel'da o'zgarish bo'lganda boshqalarni yangilash
    val plantRefresh by plantViewModel.refreshTrigger.collectAsState()

    LaunchedEffect(plantRefresh) {
        if (plantRefresh > 0) {
            homeViewModel.loadDashboard(showLoading = false)
            scheduleViewModel.loadMonthTasks(showLoading = false)
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem("home", "Asosiy", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("plants", "O'simliklar", Icons.Filled.Spa, Icons.Outlined.Spa),
        BottomNavItem("schedule", "Jadval", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
        BottomNavItem("sensors", "Datchiklar", Icons.Filled.Sensors, Icons.Outlined.Sensors),
        BottomNavItem("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        topBar = {
            if (currentRoute != "profile") {
                Surface(
                    color = com.example.hotroom.ui.theme.Background,
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.EnergySavingsLeaf, contentDescription = "Logo", tint = GreenPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Emerald Canopy", fontWeight = FontWeight.ExtraBold, color = GreenPrimaryDark, style = MaterialTheme.typography.titleMedium)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Bildirishnoma", tint = com.example.hotroom.ui.theme.TextSecondary)
                        }
                    }
                }
            }
        },
        bottomBar = {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        val bgColor = if (isSelected) com.example.hotroom.ui.theme.SurfaceContainerLow else androidx.compose.ui.graphics.Color.Transparent
                        val contentColor = if (isSelected) com.example.hotroom.ui.theme.Primary else com.example.hotroom.ui.theme.Secondary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            when (item.route) {
                                                "home" -> homeViewModel.loadDashboard(showLoading = false)
                                                "plants" -> plantViewModel.loadPlants(showLoading = false)
                                                "schedule" -> scheduleViewModel.loadMonthTasks(showLoading = false)
                                                "sensors" -> sensorViewModel.loadSensorData(showLoading = false)
                                            }
                                        }
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                    .background(bgColor)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.title.uppercase(),
                                    color = contentColor,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.05.em
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = com.example.hotroom.ui.theme.Background
    ) { padding ->
        com.example.hotroom.ui.components.DotBackground {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    val homeState by homeViewModel.uiState.collectAsState()
                    HomeScreen(
                        homeState = homeState,
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToAddPlant = onNavigateToAddPlant,
                        onNavigateToSchedule = {
                            navController.navigate("schedule") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onMarkTaskComplete = { homeViewModel.markTaskComplete(it) },
                        onRefresh = { homeViewModel.loadDashboard() }
                    )
                }
                composable("plants") {
                    val plantState by plantViewModel.uiState.collectAsState()
                    PlantListScreen(
                        plantState = plantState,
                        onNavigateToAddPlant = onNavigateToAddPlant,
                        onSearchChange = { plantViewModel.searchPlants(it) },
                        onWaterPlant = { plantViewModel.waterPlant(it) },
                        onDeletePlant = { plantViewModel.deletePlant(it) }
                    )
                }
                composable("schedule") {
                    val scheduleState by scheduleViewModel.uiState.collectAsState()
                    ScheduleScreen(
                        scheduleState = scheduleState,
                        onSelectDate = { scheduleViewModel.selectDate(it) },
                        onPreviousMonth = { scheduleViewModel.previousMonth() },
                        onNextMonth = { scheduleViewModel.nextMonth() },
                        onMarkComplete = { scheduleViewModel.markComplete(it) },
                        onAddTask = { title, type, time, plantId ->
                            scheduleViewModel.addTask(title, type, scheduleState.selectedDate, time, plantId)
                        }
                    )
                }
                composable("sensors") {
                    val sensorState by sensorViewModel.uiState.collectAsState()
                    com.example.hotroom.ui.screens.sensors.SensorScreen(
                        sensorState = sensorState,
                        onRefresh = { sensorViewModel.loadSensorData() }
                    )
                }
                composable("profile") {
                    com.example.hotroom.ui.screens.profile.ProfileScreen(
                        profileState = com.example.hotroom.ui.viewmodel.ProfileUiState(),
                        initials = "U",
                        onNavigateBack = { navController.popBackStack() },
                        onSignOut = { },
                        onUpdateNotifications = { _, _ -> }
                    )
                }
            }
        }
    }
}
