package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AzukuDarkPrimary,
    onPrimary = AzukuDarkOnPrimary,
    primaryContainer = AzukuDarkPrimaryContainer,
    onPrimaryContainer = AzukuDarkOnPrimaryContainer,
    secondary = AzukuDarkSecondary,
    onSecondary = AzukuDarkOnSecondary,
    secondaryContainer = AzukuDarkSecondaryContainer,
    onSecondaryContainer = AzukuDarkOnSecondaryContainer,
    tertiary = AzukuDarkTertiary,
    onTertiary = AzukuDarkOnTertiary,
    tertiaryContainer = AzukuDarkTertiaryContainer,
    onTertiaryContainer = AzukuDarkOnTertiaryContainer,
    background = AzukuDarkBackground,
    onBackground = AzukuDarkOnBackground,
    surface = AzukuDarkSurface,
    onSurface = AzukuDarkOnSurface,
    surfaceVariant = AzukuDarkSurfaceVariant,
    onSurfaceVariant = AzukuDarkOnSurfaceVariant,
    outline = AzukuDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AzukuLightPrimary,
    onPrimary = AzukuLightOnPrimary,
    primaryContainer = AzukuLightPrimaryContainer,
    onPrimaryContainer = AzukuLightOnPrimaryContainer,
    secondary = AzukuLightSecondary,
    onSecondary = AzukuLightOnSecondary,
    secondaryContainer = AzukuLightSecondaryContainer,
    onSecondaryContainer = AzukuLightOnSecondaryContainer,
    tertiary = AzukuLightTertiary,
    onTertiary = AzukuLightOnTertiary,
    background = AzukuLightBackground,
    onBackground = AzukuLightOnBackground,
    surface = AzukuLightSurface,
    onSurface = AzukuLightOnSurface,
    surfaceVariant = AzukuLightSurfaceVariant,
    onSurfaceVariant = AzukuLightOnSurfaceVariant,
    outline = AzukuLightOutline
)

@Composable
fun AzukuTheme(
    darkTheme: Boolean = true, // Default to stunning 3D cyberpunk glass dark theme
    dynamicColor: Boolean = false, // Keep distinctive glass cyber aesthetic by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AzukuTypography,
        content = content
    )
}
