package com.example.hotroom.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hotroom.ui.screens.MainScreen
import com.example.hotroom.ui.screens.auth.LoginScreen
import com.example.hotroom.ui.screens.auth.RegisterScreen
import com.example.hotroom.ui.screens.plants.AddPlantScreen
import com.example.hotroom.ui.screens.profile.ProfileScreen
import com.example.hotroom.ui.viewmodel.AuthViewModel
import com.example.hotroom.ui.viewmodel.PlantViewModel
import com.example.hotroom.ui.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")
    data object AddPlant : Screen("add_plant")
    data object Profile : Screen("profile")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = remember {
        if (authState.isLoggedIn) Screen.Main.route else Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login
        composable(Screen.Login.route) {
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                email = authState.email,
                password = authState.password,
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage,
                onEmailChange = authViewModel::updateEmail,
                onPasswordChange = authViewModel::updatePassword,
                onLoginClick = { authViewModel.signIn() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Register
        composable(Screen.Register.route) {
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                name = authState.name,
                email = authState.email,
                password = authState.password,
                confirmPassword = authState.confirmPassword,
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage,
                onNameChange = authViewModel::updateName,
                onEmailChange = authViewModel::updateEmail,
                onPasswordChange = authViewModel::updatePassword,
                onConfirmPasswordChange = authViewModel::updateConfirmPassword,
                onRegisterClick = { authViewModel.signUp() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Main
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAddPlant = { navController.navigate(Screen.AddPlant.route) }
            )
        }

        // Add Plant
        composable(Screen.AddPlant.route) {
            val plantViewModel: PlantViewModel = viewModel()
            val plantState by plantViewModel.uiState.collectAsState()

            LaunchedEffect(plantState.addPlantSuccess) {
                if (plantState.addPlantSuccess) {
                    plantViewModel.resetAddPlantSuccess()
                    navController.popBackStack()
                }
            }

            AddPlantScreen(
                isAdding = plantState.isAddingPlant,
                onAddPlant = { name, scientificName, category, plantedDate, hasSensor, wateringInterval, zone, notes ->
                    plantViewModel.addPlant(name, scientificName, category, plantedDate, hasSensor, wateringInterval, zone, notes)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            val profileState by profileViewModel.uiState.collectAsState()

            ProfileScreen(
                profileState = profileState,
                initials = profileViewModel.getInitials(),
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onUpdateNotifications = { watering, temperature ->
                    profileViewModel.updateNotificationSettings(watering, temperature)
                }
            )
        }
    }
}
