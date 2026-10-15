package com.mustafakoceerr.thememanager.internal

import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import org.junit.Test

class ThemeModeMapperTest {

    @Test
    fun `every mode round-trips through its stored value`() {
        ThemeMode.entries.forEach { mode ->
            assertThat(mode.toStoredValue().toThemeMode()).isEqualTo(mode)
        }
    }

    @Test
    fun `stored values match 1_x`() {
        assertThat(ThemeMode.entries.map { it.toStoredValue() }).containsExactly("SYSTEM", "LIGHT", "DARK")
    }

    @Test
    fun `missing or unknown values map to SYSTEM`() {
        assertThat(null.toThemeMode()).isEqualTo(ThemeMode.SYSTEM)
        assertThat("".toThemeMode()).isEqualTo(ThemeMode.SYSTEM)
        assertThat("dark".toThemeMode()).isEqualTo(ThemeMode.SYSTEM)
    }
}
