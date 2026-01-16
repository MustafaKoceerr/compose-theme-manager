package com.mustafakoceerr.core.theme.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mustafakoceerr.core.theme.model.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Manages the storage and retrieval of theme preferences using DataStore.
 */
internal class ThemeDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme_mode")
    }

    /**
     * Observes the current theme mode.
     * Defaults to [AppThemeMode.SYSTEM] if no value is set or if an error occurs.
     */
    val themeMode: Flow<AppThemeMode> = dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY]
            if (themeName == null) {
                AppThemeMode.SYSTEM
            } else {
                try {
                    AppThemeMode.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    AppThemeMode.SYSTEM
                }
            }
        }

    /**
     * Updates the stored theme mode.
     * @param mode The new theme mode to save.
     */
    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }
}