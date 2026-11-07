package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ServiceState
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet

@Composable
fun AnimatedStatusRing(
    modifier: Modifier = Modifier,
    state: ServiceState,
    size: Dp = 140.dp,
    centerContent: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_animation")

    val rotationFast by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationFast"
    )

    val rotationSlow by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationSlow"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val primaryColor = when (state) {
        ServiceState.RUNNING -> NeonGreen
        ServiceState.STANDBY -> NeonCyan
        ServiceState.CONNECTING -> NeonAmber
        ServiceState.ERROR -> NeonRed
    }

    val secondaryColor = when (state) {
        ServiceState.RUNNING -> NeonCyan
        ServiceState.STANDBY -> NeonViolet
        ServiceState.CONNECTING -> NeonCyan
        ServiceState.ERROR -> NeonAmber
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) - 10.dp.toPx()

            // 1. Ambient Glow Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.25f * pulseGlow),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.25f
                ),
                radius = radius * 1.25f,
                center = center
            )

            // 2. Outer Track
            drawCircle(
                color = primaryColor.copy(alpha = 0.12f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )

            // 3. Fast Rotating Dash Arcs
            rotate(rotationFast, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(primaryColor, secondaryColor, Color.Transparent)
                    ),
                    startAngle = 0f,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(secondaryColor, primaryColor, Color.Transparent)
                    ),
                    startAngle = 180f,
                    sweepAngle = 80f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 4. Inner Counter-Rotating Ring
            rotate(rotationSlow, pivot = center) {
                drawArc(
                    color = primaryColor.copy(alpha = 0.6f * pulseGlow),
                    startAngle = 45f,
                    sweepAngle = 45f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    color = secondaryColor.copy(alpha = 0.6f * pulseGlow),
                    startAngle = 225f,
                    sweepAngle = 45f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 5. Orbiting Quantum Dot
            rotate(rotationFast, pivot = center) {
                val dotAngleRad = Math.toRadians(0.0)
                val dotX = center.x + radius * Math.cos(dotAngleRad).toFloat()
                val dotY = center.y + radius * Math.sin(dotAngleRad).toFloat()
                drawCircle(
                    color = Color.White,
                    radius = 3.5.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }

        centerContent()
    }
}
