package com.example.jobnest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentPurple,
    tertiary = LightBlue,

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),

    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Color(0xFFE1E1E1),
    onSurface = Color(0xFFE1E1E1),
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = White,

    outline = Color(0xFF4A4A4A),
    outlineVariant = Color(0xFF3A3A3A)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = AccentPurple,
    tertiary = LightBlue,

    background = SoftBackground,
    surface = White,
    surfaceVariant = SurfaceLight,

    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,

    primaryContainer = PrimaryBlue.copy(alpha = 0.1f),
    secondaryContainer = AccentPurple.copy(alpha = 0.1f),
    tertiaryContainer = LightBlue.copy(alpha = 0.1f),

    onPrimaryContainer = PrimaryBlue,
    onSecondaryContainer = AccentPurple,
    onTertiaryContainer = LightBlue,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    outline = BorderLight,
    outlineVariant = Color(0xFFE8E8E8)
)

@Composable
fun JobnestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}