package com.mustafakoceerr.thememanager.internal

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.ThemeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DefaultThemeManagerTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val file get() = File(tmp.root, "theme.preferences_pb")

    private fun TestScope.createManager(scope: CoroutineScope = backgroundScope): DefaultThemeManager {
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) { file }
        return DefaultThemeManager(ThemePreferencesDataSource(dataStore), scope)
    }

    @Test
    fun `starts Loading then becomes Ready with defaults`() = runTest {
        val manager = createManager()

        manager.themeState.test {
            assertThat(awaitItem()).isEqualTo(ThemeState.Loading)
            assertThat(awaitItem()).isEqualTo(ThemeState.Ready(ThemeSettings()))
        }
    }

    @Test
    fun `setters update the state`() = runTest {
        val manager = createManager()

        manager.themeState.test {
            skipItems(2) // Loading, Ready(defaults)

            manager.setThemeMode(ThemeMode.DARK)
            assertThat(awaitItem()).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))

            manager.setUseDynamicColor(true)
            assertThat(awaitItem())
                .isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK, useDynamicColor = true)))
        }
    }

    @Test
    fun `settings persist across instances`() = runTest {
        // DataStore allows one active instance per file: close the first one before opening the second.
        val firstScope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        createManager(firstScope).setThemeMode(ThemeMode.LIGHT)
        firstScope.cancel()
        advanceUntilIdle()

        createManager().themeState.test {
            assertThat(awaitItem()).isEqualTo(ThemeState.Loading)
            assertThat(awaitItem()).isEqualTo(ThemeState.Ready(ThemeSettings(mode = ThemeMode.LIGHT)))
        }
    }
}
