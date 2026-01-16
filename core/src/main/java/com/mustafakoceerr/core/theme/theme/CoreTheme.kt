package com.mustafakoceerr.core.theme.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mustafakoceerr.core.theme.model.AppThemeMode

/**
 * The main entry point for the Design System library.
 * It handles theme switching logic and applies Material 3 styling.
 *
 * @param themeMode The current theme mode (System, Light, or Dark).
 * @param lightColorScheme The color scheme to use in light mode.
 * @param darkColorScheme The color scheme to use in dark mode.
 * @param typography The typography styles to apply (defaults to MaterialTheme.typography).
 * @param shapes The shapes to apply (defaults to MaterialTheme.shapes).
 * @param dynamicColor Whether to use Android 12+ dynamic colors (defaults to false).
 * @param content The composable content to display.
 */
@Composable
fun CoreTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val useDarkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
