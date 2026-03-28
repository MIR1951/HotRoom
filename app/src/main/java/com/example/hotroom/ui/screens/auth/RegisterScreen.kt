package com.example.hotroom.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.components.EmeraldTextField
import com.example.hotroom.ui.theme.*

@Composable
fun RegisterScreen(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean,
    errorMessage: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(72.dp))

        // Title
        Text(
            text = "Hisob yaratish",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "Yashil olamingizni boshqarishni bugundan boshlang.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
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

        // Full Name Field
        EmeraldTextField(
            value = name,
            onValueChange = onNameChange,
            label = "To'liq ism",
            placeholder = "Ismingizni kiriting",
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        EmeraldTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "example@mail.com",
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        EmeraldTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Parol",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        EmeraldTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Parolni tasdiqlash",
            placeholder = "••••••••",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ro'yxatdan o'tish",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Login Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hisobingiz bormi? ",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "Kirish",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
