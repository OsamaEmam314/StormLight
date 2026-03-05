package com.example.stormlight.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,

    secondary = SubtextDark,
    onSecondary = White,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = OnSurfaceVariantDark,

    tertiary = RainBlue,
    onTertiary = White,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = GlassBorder,
    outlineVariant = DividerDark,

    error = StormRed,
    onError = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = PrimaryDark,

    secondary = SubtextLight,
    onSecondary = White,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = OnSurfaceVariantLight,

    tertiary = RainBlue,
    onTertiary = White,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = DividerLight,
    outlineVariant = DividerLight,

    error = StormRed,
    onError = White,
)

@Composable
fun StormLightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make status bar transparent so content renders edge-to-edge
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
