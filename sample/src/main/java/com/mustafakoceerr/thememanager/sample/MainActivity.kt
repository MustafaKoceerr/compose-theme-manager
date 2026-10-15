package com.mustafakoceerr.thememanager.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakoceerr.thememanager.ThemeState
import com.mustafakoceerr.thememanager.compose.ManagedTheme
import com.mustafakoceerr.thememanager.settingsOrDefault
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Keep the splash screen until the stored theme is read, so the first frame already uses it.
        splashScreen.setKeepOnScreenCondition { viewModel.themeState.value is ThemeState.Loading }

        setContent {
            val themeState by viewModel.themeState.collectAsStateWithLifecycle()
            val settings = themeState.settingsOrDefault
            ManagedTheme(
                settings = settings,
                lightColorScheme = SampleLightColors,
                darkColorScheme = SampleDarkColors,
            ) {
                ThemeScreen(
                    settings = settings,
                    onThemeModeSelected = viewModel::onThemeModeSelected,
                    onDynamicColorChanged = viewModel::onDynamicColorChanged,
                )
            }
        }
    }
}
