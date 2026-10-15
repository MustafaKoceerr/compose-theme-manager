package com.mustafakoceerr.thememanager.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException

class ThemePreferencesDataSourceTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun TestScope.createDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(tmp.root, "${ThemePreferencesDataSource.FILE_NAME}.preferences_pb")
        }

    @Test
    fun `empty store yields default settings`() = runTest {
        val dataSource = ThemePreferencesDataSource(createDataStore())

        assertThat(dataSource.settings.first()).isEqualTo(ThemeSettings())
    }

    @Test
    fun `written values are read back`() = runTest {
        val dataSource = ThemePreferencesDataSource(createDataStore())

        dataSource.setThemeMode(ThemeMode.DARK)
        dataSource.setUseDynamicColor(true)

        assertThat(dataSource.settings.first())
            .isEqualTo(ThemeSettings(mode = ThemeMode.DARK, useDynamicColor = true))
    }

    @Test
    fun `selection stored by 1_x is read`() = runTest {
        val dataStore = createDataStore()
        dataStore.edit { it[stringPreferencesKey("app_theme_mode")] = "LIGHT" }

        assertThat(ThemePreferencesDataSource(dataStore).settings.first().mode).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `unknown stored mode falls back to SYSTEM`() = runTest {
        val dataStore = createDataStore()
        dataStore.edit { it[stringPreferencesKey("app_theme_mode")] = "SEPIA" }

        assertThat(ThemePreferencesDataSource(dataStore).settings.first().mode).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `read failure yields default settings`() = runTest {
        val dataSource = ThemePreferencesDataSource(FailingDataStore(IOException("disk")))

        assertThat(dataSource.settings.first()).isEqualTo(ThemeSettings())
    }

    @Test
    fun `non-IO failures are not swallowed`() = runTest {
        val dataSource = ThemePreferencesDataSource(FailingDataStore(IllegalStateException("bug")))

        val error = runCatching { dataSource.settings.first() }.exceptionOrNull()

        assertThat(error).isInstanceOf(IllegalStateException::class.java)
    }

    private class FailingDataStore(private val error: Throwable) : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow { throw error }
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences = throw error
    }
}
