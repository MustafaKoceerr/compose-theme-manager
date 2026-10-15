package com.mustafakoceerr.thememanager.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeManager: ThemeManager,
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = themeManager.themeState

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { themeManager.setThemeMode(mode) }
    }

    fun onDynamicColorChanged(enabled: Boolean) {
        viewModelScope.launch { themeManager.setUseDynamicColor(enabled) }
    }
}
