package com.mustafakoceerr.thememanager.testing

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakeThemeManagerTest {

    @Test
    fun `starts ready with default settings by default`() {
        assertThat(FakeThemeManager().themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings()))
    }

    @Test
    fun `starts with the given state`() {
        assertThat(FakeThemeManager(ThemeState.Loading).themeState.value).isEqualTo(ThemeState.Loading)
    }

    @Test
    fun `setters update state and record calls`() = runTest {
        val manager = FakeThemeManager()

        manager.themeState.test {
            assertThat(awaitItem()).isEqualTo(ThemeState.Ready(ThemeSettings()))

            manager.setThemeMode(ThemeMode.DARK)
            assertThat(awaitItem()).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))

            manager.setUseDynamicColor(true)
            assertThat(awaitItem())
                .isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK, useDynamicColor = true)))
        }
        assertThat(manager.setThemeModeCalls).containsExactly(ThemeMode.DARK)
        assertThat(manager.setUseDynamicColorCalls).containsExactly(true)
    }

    @Test
    fun `setter while loading starts from default settings`() = runTest {
        val manager = FakeThemeManager(ThemeState.Loading)

        manager.setThemeMode(ThemeMode.LIGHT)

        assertThat(manager.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.LIGHT)))
    }

    @Test
    fun `emit replaces state without recording a call`() {
        val manager = FakeThemeManager(ThemeState.Loading)
        val settings = ThemeSettings(mode = ThemeMode.DARK, useDynamicColor = true)

        manager.emit(ThemeState.Ready(settings))

        assertThat(manager.themeState.value).isEqualTo(ThemeState.Ready(settings))
        assertThat(manager.setThemeModeCalls).isEmpty()
        assertThat(manager.setUseDynamicColorCalls).isEmpty()
    }
}
