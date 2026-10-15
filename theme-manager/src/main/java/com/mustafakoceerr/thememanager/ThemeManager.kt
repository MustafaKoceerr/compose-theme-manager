package com.mustafakoceerr.thememanager

import android.content.Context
import com.mustafakoceerr.thememanager.internal.DefaultThemeManager
import kotlinx.coroutines.flow.StateFlow

/**
 * Observes and changes the app's [ThemeSettings], persisted with Jetpack DataStore.
 *
 * Obtain the process-wide instance with [ThemeManager.create]. The library has no dependency injection
 * framework: provide the instance through your own DI graph, or use the `theme-manager-hilt` module.
 */
public interface ThemeManager {

    /**
     * The current settings. Starts as [ThemeState.Loading] and becomes [ThemeState.Ready] once the
     * stored settings are read; never returns to [ThemeState.Loading].
     */
    public val themeState: StateFlow<ThemeState>

    /** Persists [mode]. [themeState] is updated once the write completes. */
    public suspend fun setThemeMode(mode: ThemeMode)

    /** Persists whether dynamic color is used. [themeState] is updated once the write completes. */
    public suspend fun setUseDynamicColor(enabled: Boolean)

    public companion object {
        @Volatile
        private var instance: ThemeManager? = null

        /**
         * Returns the process-wide [ThemeManager]. Safe to call repeatedly; starts reading the stored
         * settings on first call.
         */
        public fun create(context: Context): ThemeManager =
            instance ?: synchronized(this) {
                instance ?: DefaultThemeManager.create(context.applicationContext).also { instance = it }
            }
    }
}
