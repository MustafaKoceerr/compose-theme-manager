package com.mustafakoceerr.core.theme.domain

import com.mustafakoceerr.core.theme.model.AppThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe the current theme mode.
 */
class GetThemeModeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<AppThemeMode> {
        return themeRepository.themeMode
    }
}