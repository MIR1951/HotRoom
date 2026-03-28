package com.example.hotroom.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hotroom.ui.theme.GlassBorder
import com.example.hotroom.ui.theme.GlassSurface

/**
 * Premium shisha (Glassmorphism) dizayn uslubidagi karta vidjeti.
 * Oq fonda kichik ko'lankali va nozik gradient border ga ega.
 */
@Composable
fun StitchCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 4.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = GlassSurface,
        border = BorderStroke(
            1.5.dp, 
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    GlassBorder.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
