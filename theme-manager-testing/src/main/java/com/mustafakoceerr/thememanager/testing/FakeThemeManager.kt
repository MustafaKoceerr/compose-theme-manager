package com.mustafakoceerr.thememanager.testing

import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory [ThemeManager] for unit tests and Compose previews. Doesn't touch disk or any platform API.
 *
 * ```
 * val themeManager = FakeThemeManager(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
 * val viewModel = ThemeViewModel(themeManager)
 * viewModel.onThemeModeSelected(ThemeMode.LIGHT)
 * assertThat(themeManager.setThemeModeCalls).containsExactly(ThemeMode.LIGHT)
 * ```
 */
public class FakeThemeManager(
    initialState: ThemeState = ThemeState.Ready(ThemeSettings()),
) : ThemeManager {

    private val _themeState = MutableStateFlow(initialState)
    override val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    private val _setThemeModeCalls = mutableListOf<ThemeMode>()
    private val _setUseDynamicColorCalls = mutableListOf<Boolean>()

    /** Every mode passed to [setThemeMode], in call order. */
    public val setThemeModeCalls: List<ThemeMode> get() = _setThemeModeCalls.toList()

    /** Every value passed to [setUseDynamicColor], in call order. */
    public val setUseDynamicColorCalls: List<Boolean> get() = _setUseDynamicColorCalls.toList()

    /** Records [mode] and applies it, starting from the default settings while [ThemeState.Loading]. */
    override suspend fun setThemeMode(mode: ThemeMode) {
        _setThemeModeCalls += mode
        updateSettings { it.copy(mode = mode) }
    }

    /** Records [enabled] and applies it, starting from the default settings while [ThemeState.Loading]. */
    override suspend fun setUseDynamicColor(enabled: Boolean) {
        _setUseDynamicColorCalls += enabled
        updateSettings { it.copy(useDynamicColor = enabled) }
    }

    /**
     * Replaces the state without recording a call, e.g. to finish loading:
     * `emit(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))`.
     */
    public fun emit(state: ThemeState) {
        _themeState.value = state
    }

    private inline fun updateSettings(transform: (ThemeSettings) -> ThemeSettings) {
        _themeState.update { state ->
            val current = (state as? ThemeState.Ready)?.settings ?: ThemeSettings()
            ThemeState.Ready(transform(current))
        }
    }
}
