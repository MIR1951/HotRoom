package com.example.hotroom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotroom.ui.theme.GreenPrimary
import com.example.hotroom.ui.theme.TextSecondary
import com.example.hotroom.ui.theme.SurfaceContainerLow

@Composable
fun EmeraldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    ),
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    cursorBrush = SolidColor(GreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailingIcon()
            }
        }
    }
}
