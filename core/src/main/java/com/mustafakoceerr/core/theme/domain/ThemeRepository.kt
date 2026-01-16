package com.mustafakoceerr.core.theme.domain

import com.mustafakoceerr.core.theme.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing theme settings.
 * Abstraction allows for easier testing and future data source changes.
 */
interface ThemeRepository {
    val themeMode: Flow<AppThemeMode>
    suspend fun updateTheme(mode: AppThemeMode)
}