package com.example.hotroom.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hotroom.ui.screens.home.HomeScreen
import com.example.hotroom.ui.screens.plants.PlantListScreen
import com.example.hotroom.ui.screens.schedule.ScheduleScreen
import com.example.hotroom.ui.screens.sensors.SensorScreen
import com.example.hotroom.ui.theme.GreenPrimary
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
            homeViewModel.loadDashboard()
            scheduleViewModel.loadMonthTasks()
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem("home", "Asosiy", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("plants", "O'simliklar", Icons.Filled.Spa, Icons.Outlined.Spa),
        BottomNavItem("schedule", "Jadval", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
        BottomNavItem("sensors", "Datchiklar", Icons.Filled.Sensors, Icons.Outlined.Sensors)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                // Tab o'zgarganda data yangilash
                                when (item.route) {
                                    "home" -> homeViewModel.loadDashboard()
                                    "plants" -> plantViewModel.loadPlants()
                                    "schedule" -> scheduleViewModel.loadMonthTasks()
                                    "sensors" -> sensorViewModel.loadSensorData()
                                }
                            }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = GreenPrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
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
                SensorScreen(
                    sensorState = sensorState,
                    onRefresh = { sensorViewModel.loadSensorData() }
                )
            }
        }
    }
}
