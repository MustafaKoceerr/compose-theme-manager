package com.mustafakoceerr.thememanager.compose

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeManager
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import com.mustafakoceerr.thememanager.testing.FakeThemeManager
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ThemeManagerComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `provided manager is used`() {
        val fake = FakeThemeManager()
        lateinit var resolved: ThemeManager

        composeRule.setContent {
            CompositionLocalProvider(LocalThemeManager provides fake) {
                resolved = rememberThemeManager()
            }
        }

        assertThat(resolved).isSameInstanceAs(fake)
    }

    @Test
    fun `process-wide manager is used by default`() {
        lateinit var resolved: ThemeManager
        lateinit var expected: ThemeManager

        composeRule.setContent {
            resolved = rememberThemeManager()
            expected = ThemeManager.create(LocalContext.current)
        }

        assertThat(resolved).isSameInstanceAs(expected)
    }

    @Test
    fun `inspection mode uses an in-memory manager`() = runTest {
        lateinit var resolved: ThemeManager

        composeRule.setContent {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                resolved = rememberThemeManager()
            }
        }

        assertThat(resolved.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings()))
        resolved.setThemeMode(ThemeMode.DARK)
        assertThat(resolved.themeState.value).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
    }

    @Test
    fun `theme state recomposes on change`() {
        val fake = FakeThemeManager(ThemeState.Loading)
        val ready = ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK))

        composeRule.setContent {
            CompositionLocalProvider(LocalThemeManager provides fake) {
                val state by rememberThemeState()
                BasicText(state.toString())
            }
        }
        composeRule.onNodeWithText(ThemeState.Loading.toString()).assertExists()

        fake.emit(ready)

        composeRule.onNodeWithText(ready.toString()).assertExists()
    }

    @Test
    fun `explicit modes ignore the device setting`() {
        val resolved = mutableMapOf<ThemeMode, Boolean>()

        composeRule.setContent {
            ThemeMode.entries.forEach { resolved[it] = it.isDarkTheme() }
        }

        assertThat(resolved).containsExactly(ThemeMode.SYSTEM, false, ThemeMode.LIGHT, false, ThemeMode.DARK, true)
    }

    @Test
    @Config(qualifiers = "night")
    fun `system mode follows a dark device`() {
        val resolved = mutableMapOf<ThemeMode, Boolean>()

        composeRule.setContent {
            ThemeMode.entries.forEach { resolved[it] = it.isDarkTheme() }
        }

        assertThat(resolved).containsExactly(ThemeMode.SYSTEM, true, ThemeMode.LIGHT, false, ThemeMode.DARK, true)
    }
}
