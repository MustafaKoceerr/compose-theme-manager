package com.mustafakoceerr.core.theme.domain

import com.mustafakoceerr.core.theme.model.AppThemeMode
import javax.inject.Inject

/**
 * Use case to update the application's theme mode.
 */
class SetThemeModeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(mode: AppThemeMode) {
        themeRepository.updateTheme(mode)
    }
}