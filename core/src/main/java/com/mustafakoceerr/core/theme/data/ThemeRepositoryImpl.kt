package com.mustafakoceerr.core.theme.data

import com.mustafakoceerr.core.theme.domain.ThemeRepository
import com.mustafakoceerr.core.theme.model.AppThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Concrete implementation of [ThemeRepository] that delegates to [ThemeDataSource].
 */
internal class ThemeRepositoryImpl @Inject constructor(
    private val dataSource: ThemeDataSource
) : ThemeRepository {

    override val themeMode: Flow<AppThemeMode> = dataSource.themeMode

    override suspend fun updateTheme(mode: AppThemeMode) {
        dataSource.setThemeMode(mode)
    }
}