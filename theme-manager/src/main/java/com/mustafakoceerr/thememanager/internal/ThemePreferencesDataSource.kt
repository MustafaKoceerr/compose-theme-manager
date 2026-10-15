package com.mustafakoceerr.thememanager.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Reads and writes [ThemeSettings] in a Preferences DataStore. The file name and theme mode key are the ones
 * 1.x used, so a selection made with 1.x is read without migration.
 */
internal class ThemePreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {

    /** The stored settings. An unreadable file yields the defaults rather than an error. */
    val settings: Flow<ThemeSettings> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { preferences ->
            ThemeSettings(
                mode = preferences[THEME_MODE].toThemeMode(),
                useDynamicColor = preferences[USE_DYNAMIC_COLOR] ?: false,
            )
        }
        .distinctUntilChanged()

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.toStoredValue() }
    }

    suspend fun setUseDynamicColor(enabled: Boolean) {
        dataStore.edit { it[USE_DYNAMIC_COLOR] = enabled }
    }

    companion object {
        const val FILE_NAME = "theme_preferences"
        private val THEME_MODE = stringPreferencesKey("app_theme_mode")
        private val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    }
}
