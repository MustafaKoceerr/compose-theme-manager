package com.mustafakoceerr.thememanager.sample

import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import com.mustafakoceerr.thememanager.testing.FakeThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    private val themeManager = FakeThemeManager(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ThemeViewModel(themeManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `exposes the current state`() {
        assertThat(viewModel.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
    }

    @Test
    fun `selecting a mode forwards it to the manager`() {
        viewModel.onThemeModeSelected(ThemeMode.LIGHT)

        assertThat(themeManager.setThemeModeCalls).containsExactly(ThemeMode.LIGHT)
        assertThat(viewModel.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.LIGHT)))
    }

    @Test
    fun `toggling dynamic color forwards it to the manager`() {
        viewModel.onDynamicColorChanged(true)

        assertThat(themeManager.setUseDynamicColorCalls).containsExactly(true)
        assertThat(viewModel.themeState.value)
            .isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK, useDynamicColor = true)))
    }

    @Test
    fun `stays loading until the manager is ready`() {
        val loading = FakeThemeManager(ThemeState.Loading)
        val viewModel = ThemeViewModel(loading)
        assertThat(viewModel.themeState.value).isEqualTo(ThemeState.Loading)

        loading.emit(ThemeState.Ready(ThemeSettings()))

        assertThat(viewModel.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings()))
    }
}
