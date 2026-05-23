package com.example.eduvault.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ─── Light Color Scheme (mặc định dùng cho app) ──────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = ColorAmber,
    onPrimary = ColorInk,
    primaryContainer = ColorAmberLight,
    onPrimaryContainer = ColorInk,

    secondary = ColorForest,
    onSecondary = ColorTextOnDark,
    secondaryContainer = ColorForestLight,
    onSecondaryContainer = ColorInk,

    background = ColorPaper,
    onBackground = ColorTextOnLight,

    surface = ColorCream,
    onSurface = ColorTextOnLight,
    surfaceVariant = ColorBorder,
    onSurfaceVariant = ColorTextOnLightSecondary,

    outline = ColorBorder,
    error = ColorError,
    onError = ColorTextOnDark,
)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = ColorAmber,
    onPrimary = ColorInk,
    primaryContainer = ColorAmberDark,
    onPrimaryContainer = ColorTextOnDark,

    secondary = ColorForestLight,
    onSecondary = ColorInk,

    background = ColorInk,
    onBackground = ColorTextOnDark,

    surface = ColorInkLight,
    onSurface = ColorTextOnDark,
    surfaceVariant = ColorInkLighter,
    onSurfaceVariant = ColorTextOnDarkSecondary,

    outline = ColorInkLighter,
    error = ColorError,
    onError = ColorTextOnDark,
)

@Composable
fun EduVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
