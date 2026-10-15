package com.mustafakoceerr.thememanager.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.core.view.WindowCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import com.mustafakoceerr.thememanager.testing.FakeThemeManager
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ManagedThemeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val light = lightColorScheme(primary = Color.Red)
    private val dark = darkColorScheme(primary = Color.Blue)

    private fun applied(settings: ThemeSettings): ColorScheme {
        lateinit var colorScheme: ColorScheme
        composeRule.setContent {
            ManagedTheme(settings, light, dark) { colorScheme = MaterialTheme.colorScheme }
        }
        return colorScheme
    }

    @Test
    fun `light mode applies the light scheme`() {
        assertThat(applied(ThemeSettings(mode = ThemeMode.LIGHT)).primary).isEqualTo(light.primary)
    }

    @Test
    fun `dark mode applies the dark scheme`() {
        assertThat(applied(ThemeSettings(mode = ThemeMode.DARK)).primary).isEqualTo(dark.primary)
    }

    @Test
    @Config(qualifiers = "night")
    fun `system mode on a dark device applies the dark scheme`() {
        assertThat(applied(ThemeSettings(mode = ThemeMode.SYSTEM)).primary).isEqualTo(dark.primary)
    }

    @Test
    @Config(sdk = [31])
    fun `dynamic color replaces the schemes where available`() {
        val primary = applied(ThemeSettings(mode = ThemeMode.LIGHT, useDynamicColor = true)).primary

        assertThat(primary).isNotEqualTo(light.primary)
    }

    @Test
    @Config(sdk = [30])
    fun `dynamic color is ignored where unavailable`() {
        val primary = applied(ThemeSettings(mode = ThemeMode.LIGHT, useDynamicColor = true)).primary

        assertThat(primary).isEqualTo(light.primary)
    }

    @Test
    fun `manager overload applies the managed settings`() {
        val fake = FakeThemeManager(ThemeState.Loading)
        lateinit var colorScheme: ColorScheme
        composeRule.setContent {
            ManagedTheme(light, dark, themeManager = fake) { colorScheme = MaterialTheme.colorScheme }
        }
        // Loading: defaults (follow the light device).
        assertThat(colorScheme.primary).isEqualTo(light.primary)

        fake.emit(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
        composeRule.waitForIdle()

        assertThat(colorScheme.primary).isEqualTo(dark.primary)
    }

    @Test
    fun `system bar icons follow the theme`() {
        val fake = FakeThemeManager(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
        composeRule.setContent { ManagedTheme(light, dark, themeManager = fake) {} }
        val window = composeRule.activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        assertThat(controller.isAppearanceLightStatusBars).isFalse()
        assertThat(controller.isAppearanceLightNavigationBars).isFalse()

        fake.emit(ThemeState.Ready(ThemeSettings(mode = ThemeMode.LIGHT)))
        composeRule.waitForIdle()

        assertThat(controller.isAppearanceLightStatusBars).isTrue()
        assertThat(controller.isAppearanceLightNavigationBars).isTrue()
    }

    @Test
    fun `system bars are left alone when disabled`() {
        lateinit var before: Pair<Boolean, Boolean>
        composeRule.runOnUiThread {
            val window = composeRule.activity.window
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            before = controller.isAppearanceLightStatusBars to controller.isAppearanceLightNavigationBars
        }

        composeRule.setContent {
            ManagedTheme(ThemeSettings(mode = ThemeMode.DARK), light, dark, applySystemBarsAppearance = false) {}
        }
        val window = composeRule.activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        assertThat(controller.isAppearanceLightStatusBars to controller.isAppearanceLightNavigationBars)
            .isEqualTo(before)
    }
}
