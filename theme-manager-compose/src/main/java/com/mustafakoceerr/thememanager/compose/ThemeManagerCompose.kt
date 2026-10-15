package com.mustafakoceerr.thememanager.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Overrides the [ThemeManager] returned by [rememberThemeManager] for a subtree,
 * e.g. to supply a fake in UI tests or previews:
 *
 * ```
 * CompositionLocalProvider(LocalThemeManager provides FakeThemeManager()) {
 *     ThemeSettingsScreen()
 * }
 * ```
 */
public val LocalThemeManager: ProvidableCompositionLocal<ThemeManager?> =
    staticCompositionLocalOf { null }

/**
 * Returns the [ThemeManager] for this composition: the one provided through [LocalThemeManager] if any,
 * an in-memory manager in previews (`LocalInspectionMode`), otherwise the process-wide [ThemeManager.create].
 */
@Composable
public fun rememberThemeManager(): ThemeManager {
    val provided = LocalThemeManager.current
    val inInspectionMode = LocalInspectionMode.current
    val context = LocalContext.current
    return remember(provided, inInspectionMode, context) {
        when {
            provided != null -> provided
            inInspectionMode -> PreviewThemeManager()
            else -> ThemeManager.create(context)
        }
    }
}

/**
 * Collects [ThemeManager.themeState] as [State], lifecycle-aware.
 */
@Composable
public fun rememberThemeState(
    themeManager: ThemeManager = rememberThemeManager(),
): State<ThemeState> = themeManager.themeState.collectAsStateWithLifecycle()

/**
 * Whether this mode resolves to a dark theme; [ThemeMode.SYSTEM] follows [isSystemInDarkTheme].
 */
@Composable
public fun ThemeMode.isDarkTheme(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

/** Preview-only manager: keeps the settings in memory so previews stay interactive without disk access. */
private class PreviewThemeManager : ThemeManager {
    override val themeState = MutableStateFlow<ThemeState>(ThemeState.Ready(ThemeSettings()))

    override suspend fun setThemeMode(mode: ThemeMode) = update { it.copy(mode = mode) }

    override suspend fun setUseDynamicColor(enabled: Boolean) = update { it.copy(useDynamicColor = enabled) }

    private fun update(transform: (ThemeSettings) -> ThemeSettings) {
        themeState.update { ThemeState.Ready(transform((it as ThemeState.Ready).settings)) }
    }
}
