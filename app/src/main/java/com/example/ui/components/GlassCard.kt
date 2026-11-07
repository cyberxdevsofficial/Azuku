package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassWhite05
import com.example.ui.theme.NeonCyan

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderColor: Color = NeonCyan.copy(alpha = 0.25f),
    backgroundColor: Color = DarkNavyGlass.copy(alpha = 0.72f),
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.6f),
                        GlassBorderHighlight.copy(alpha = 0.2f),
                        borderColor.copy(alpha = 0.1f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 400f)
                ),
                shape = shape
            )
            .background(backgroundColor, shape)
            .drawBehind {
                // Subtle specular highlight on the top edge
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.35f
                    )
                )
            }
            .then(clickModifier)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun AnimatedGlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonCyan,
    shape: Shape = RoundedCornerShape(22.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_shimmer")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alphaAnim),
                        glowColor.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkNavyGlass.copy(alpha = 0.85f),
                        Color(0xFF070B14).copy(alpha = 0.92f)
                    )
                ),
                shape = shape
            )
            .padding(18.dp),
        content = content
    )
}
