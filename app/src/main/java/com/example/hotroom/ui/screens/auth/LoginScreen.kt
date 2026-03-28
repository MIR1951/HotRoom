package com.example.hotroom.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.components.EmeraldTextField
import com.example.hotroom.ui.theme.*

@Composable
fun LoginScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // App Logo & Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings, // Closest default icon to the design's filter_vintage
                contentDescription = "Logo",
                tint = GreenPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Emerald Canopy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Xush kelibsiz",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Davom etish uchun hisobingizga kiring.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email Field
        EmeraldTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "EMAIL",
            placeholder = "misol@pochta.uz",
            leadingIcon = {
                Text(
                    text = "@",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAROL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Parolni unutdingizmi?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = GreenPrimary,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            
            EmeraldTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "", // Handled by custom row above
                placeholder = "••••••••",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(100),

            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(text = "Kirish", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
            Text(
                text = "YOKI",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFF9CA3AF),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Google Button
        OutlinedButton(
            onClick = { /* TODO: Google Sign In */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Colored letter 'G' as placeholder for google logo
                Text(text = "G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Google orqali kirish",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register Link
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Hisobingiz yo'qmi? ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(
                text = "Ro'yxatdan o'tish",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
