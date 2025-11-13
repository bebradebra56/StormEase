package com.stromeese.appsofr.ui.theme

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

// Olympus Night Theme
private val OlympusNightColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = GoldenAccent,
    tertiary = NightGoldenAccent,
    background = NightOlympusBackground,
    surface = NightCardBackground,
    onPrimary = LightningWhite,
    onSecondary = NightOlympusBackground,
    onTertiary = NightOlympusBackground,
    onBackground = MarbleGray,
    onSurface = MarbleGray,
    surfaceVariant = NightSkyBlue,
    onSurfaceVariant = NightSecondaryText,
    outline = NightSecondaryText,
    outlineVariant = NightSkyBlue
)

// Olympus Day Theme
private val OlympusDayColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = GoldenAccent,
    tertiary = SkyBlueDark,
    background = SkyBlueLight,
    surface = CardBackground,
    onPrimary = LightningWhite,
    onSecondary = SkyBlueDark,
    onTertiary = LightningWhite,
    onBackground = SkyBlueDark,
    onSurface = SkyBlueDark,
    surfaceVariant = LightGlow,
    onSurfaceVariant = SecondaryGrayBlue,
    outline = SecondaryGrayBlue,
    outlineVariant = SkyBlueDark
)

@Composable
fun StormEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        OlympusNightColorScheme
    } else {
        OlympusDayColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
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