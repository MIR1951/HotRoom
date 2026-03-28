package com.example.hotroom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.hotroom.ui.theme.BackgroundLight

/**
 * Stitch UI da bo'lgani kabi ekranning hamma joyiga
 * nozik nuqtalarni (Dot canvas) kiygizib beruvchi Layout.
 */
@Composable
fun DotBackground(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Black.copy(alpha = 0.04f),
    dotSpacing: Float = 50f,
    dotRadius: Float = 3f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            var x = 0f
            while (x < width) {
                var y = 0f
                while (y < height) {
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                    y += dotSpacing
                }
                x += dotSpacing
            }
        }
        content()
    }
}
