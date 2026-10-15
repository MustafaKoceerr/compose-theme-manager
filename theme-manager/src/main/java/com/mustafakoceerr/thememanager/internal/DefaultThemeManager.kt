package com.mustafakoceerr.thememanager.internal

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * [ThemeManager] backed by a [ThemePreferencesDataSource], the single source of truth. Collection starts eagerly
 * so the settings are usually [ThemeState.Ready] by the time the first activity draws.
 */
internal class DefaultThemeManager(
    private val dataSource: ThemePreferencesDataSource,
    scope: CoroutineScope,
) : ThemeManager {

    override val themeState: StateFlow<ThemeState> = dataSource.settings
        .map(ThemeState::Ready)
        .stateIn(scope, SharingStarted.Eagerly, ThemeState.Loading)

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataSource.setThemeMode(mode)
    }

    override suspend fun setUseDynamicColor(enabled: Boolean) {
        dataSource.setUseDynamicColor(enabled)
    }

    companion object {
        fun create(context: Context): DefaultThemeManager {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
                context.preferencesDataStoreFile(ThemePreferencesDataSource.FILE_NAME)
            }
            return DefaultThemeManager(ThemePreferencesDataSource(dataStore), scope)
        }
    }
}
