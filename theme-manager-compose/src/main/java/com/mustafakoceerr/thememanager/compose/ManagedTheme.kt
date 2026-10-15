package com.mustafakoceerr.thememanager.compose

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import com.mustafakoceerr.thememanager.isDynamicColorAvailable
import com.mustafakoceerr.thememanager.settingsOrDefault

/**
 * [MaterialTheme] driven by [ThemeManager]: collects [ThemeManager.themeState] and applies it.
 *
 * While the state is [ThemeState.Loading] the default [ThemeSettings] are used. To avoid drawing them on
 * cold start, keep the splash screen visible until the state is [ThemeState.Ready].
 *
 * @param lightColorScheme the app's light color scheme.
 * @param darkColorScheme the app's dark color scheme.
 * @param typography the typography to apply.
 * @param shapes the shapes to apply.
 * @param applySystemBarsAppearance whether to switch the status and navigation bar icons between light and dark
 * to stay readable over the theme's background. Disable it if the app manages the system bars itself.
 * @param themeManager the source of the settings.
 * @param content the themed content.
 */
@Composable
public fun ManagedTheme(
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    applySystemBarsAppearance: Boolean = true,
    themeManager: ThemeManager = rememberThemeManager(),
    content: @Composable () -> Unit,
) {
    val themeState by rememberThemeState(themeManager)
    ManagedTheme(
        settings = themeState.settingsOrDefault,
        lightColorScheme = lightColorScheme,
        darkColorScheme = darkColorScheme,
        typography = typography,
        shapes = shapes,
        applySystemBarsAppearance = applySystemBarsAppearance,
        content = content,
    )
}

/**
 * [MaterialTheme] for the given [settings]. Use this overload when the settings come from your own
 * UI state, e.g. a ViewModel.
 *
 * Dynamic color replaces [lightColorScheme] and [darkColorScheme] when [ThemeSettings.useDynamicColor] is set
 * and [isDynamicColorAvailable].
 *
 * @param settings the theme settings to apply.
 * @param lightColorScheme the app's light color scheme.
 * @param darkColorScheme the app's dark color scheme.
 * @param typography the typography to apply.
 * @param shapes the shapes to apply.
 * @param applySystemBarsAppearance whether to switch the status and navigation bar icons between light and dark
 * to stay readable over the theme's background. Disable it if the app manages the system bars itself.
 * @param content the themed content.
 */
@Composable
public fun ManagedTheme(
    settings: ThemeSettings,
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    applySystemBarsAppearance: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkTheme = settings.mode.isDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        settings.useDynamicColor && isDynamicColorAvailable ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    if (applySystemBarsAppearance) {
        SystemBarsAppearanceEffect(darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}

/** Dark icons over a light theme and vice versa, so the bars stay readable when the theme differs from the device's. */
@Composable
private fun SystemBarsAppearanceEffect(darkTheme: Boolean) {
    val window = LocalActivity.current?.window ?: return
    val view = LocalView.current
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
