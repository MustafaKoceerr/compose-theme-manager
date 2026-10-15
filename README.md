# Compose Theme Manager

[![CI](https://github.com/MustafaKoceerr/compose-theme-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/MustafaKoceerr/compose-theme-manager/actions/workflows/ci.yml)
[![JitPack](https://jitpack.io/v/MustafaKoceerr/compose-theme-manager.svg)](https://jitpack.io/#MustafaKoceerr/compose-theme-manager)

A lightweight Android library for **user-selectable themes in Jetpack Compose Material 3**: light / dark / follow
system, plus optional dynamic color (Material You). The selection is persisted with Jetpack DataStore and exposed as a
`StateFlow<ThemeState>`. It doesn't depend on any DI framework. Hilt support comes in an optional module.

## How it works

The library follows the approach of Google's [Now in Android](https://github.com/android/nowinandroid) reference app:

- **Persistence:** `ThemeSettings` (mode + dynamic color) are stored in a Preferences DataStore, the single source of
  truth.
- **No theme flash on cold start:** reading DataStore is asynchronous, so `themeState` starts as `ThemeState.Loading`
  and becomes `ThemeState.Ready` a few milliseconds later. Keep the splash screen up while loading and the first frame
  is drawn with the user's theme.
- **Readable system bars:** when the chosen theme differs from the device's (e.g. dark app on a light device),
  `ManagedTheme` switches the status and navigation bar icons so they stay visible.
- **Dynamic color** is applied on API 31+ and ignored below, where your own color schemes are used.

## Requirements

- **minSdk 24**
- Jetpack Compose with Material 3 (for `theme-manager-compose`)

## Installation (JitPack)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.github.MustafaKoceerr.compose-theme-manager:theme-manager-compose:2.0.0")

    // Optional (each includes theme-manager)
    implementation("com.github.MustafaKoceerr.compose-theme-manager:theme-manager-hilt:2.0.0")
    testImplementation("com.github.MustafaKoceerr.compose-theme-manager:theme-manager-testing:2.0.0")
}
```

To try an unreleased branch, use `<branch>-SNAPSHOT` as the version.

## Modules

| Module | Purpose |
|---|---|
| `:theme-manager` | Core API and DataStore persistence, no DI, no Compose |
| `:theme-manager-compose` | `ManagedTheme`, `rememberThemeState()`, `ThemeMode.isDarkTheme()`, preview support |
| `:theme-manager-hilt` | Optional Hilt module that provides `ThemeManager` as a singleton |
| `:theme-manager-testing` | `FakeThemeManager` for unit tests and previews |
| `:sample` | Compose demo app |

## API

```kotlin
public enum class ThemeMode { SYSTEM, LIGHT, DARK }

public data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false,
)

public sealed interface ThemeState {
    data object Loading : ThemeState
    data class Ready(val settings: ThemeSettings) : ThemeState
}

public interface ThemeManager {
    val themeState: StateFlow<ThemeState>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setUseDynamicColor(enabled: Boolean)

    companion object {
        fun create(context: Context): ThemeManager // process-wide instance
    }
}

public val ThemeState.settingsOrDefault: ThemeSettings // defaults while Loading
public val isDynamicColorAvailable: Boolean            // API 31+
```

### `ManagedTheme` (`theme-manager-compose`)

Replaces `MaterialTheme` at the root of your UI. Two overloads:

```kotlin
// Stateless: settings come from your UI state (e.g. a ViewModel)
ManagedTheme(
    settings = settings,
    lightColorScheme = AppLightColors,
    darkColorScheme = AppDarkColors,
    typography = AppTypography,            // optional
    shapes = AppShapes,                    // optional
    applySystemBarsAppearance = true,      // optional
) { /* content */ }

// Collects the settings itself from rememberThemeManager()
ManagedTheme(lightColorScheme = AppLightColors, darkColorScheme = AppDarkColors) { /* content */ }
```

## Usage

### With Hilt

Add `:theme-manager-hilt` and inject `ThemeManager`:

```kotlin
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
```

Keep the splash screen until the theme is read ([`androidx.core:core-splashscreen`](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate)),
then apply it:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.themeState.value is ThemeState.Loading }

        setContent {
            val themeState by viewModel.themeState.collectAsStateWithLifecycle()
            val settings = themeState.settingsOrDefault
            ManagedTheme(settings, AppLightColors, AppDarkColors) {
                SettingsScreen(
                    settings = settings,
                    onThemeModeSelected = viewModel::onThemeModeSelected,
                    onDynamicColorChanged = viewModel::onDynamicColorChanged,
                )
            }
        }
    }
}
```

See [`sample`](sample/src/main/java/com/mustafakoceerr/thememanager/sample) for the complete app, including the
settings screen.

### With another DI framework or none

`ThemeManager.create(context)` returns the process-wide instance; register it wherever your graph lives. With Koin:

```kotlin
val themeModule = module {
    single { ThemeManager.create(androidContext()) }
    viewModelOf(::ThemeViewModel)
}
```

### Compose without a ViewModel

```kotlin
@Composable
fun App() {
    ManagedTheme(lightColorScheme = AppLightColors, darkColorScheme = AppDarkColors) {
        val themeManager = rememberThemeManager()
        val themeState by rememberThemeState(themeManager)
        val scope = rememberCoroutineScope()
        // render options, call scope.launch { themeManager.setThemeMode(...) }
    }
}
```

- `rememberThemeManager()` returns the process-wide manager. In `@Preview` it returns an in-memory manager, so
  previews don't touch disk.
- Use `LocalThemeManager` to provide a different manager, e.g. a fake in UI tests:
  `CompositionLocalProvider(LocalThemeManager provides FakeThemeManager()) { ... }`
- `ThemeMode.isDarkTheme()` resolves `SYSTEM` with `isSystemInDarkTheme()`.

### Testing (`theme-manager-testing`)

```kotlin
val themeManager = FakeThemeManager(ThemeState.Ready(ThemeSettings(mode = ThemeMode.DARK)))
val viewModel = ThemeViewModel(themeManager)

viewModel.onThemeModeSelected(ThemeMode.LIGHT)
assertThat(themeManager.setThemeModeCalls).containsExactly(ThemeMode.LIGHT)

// Start in Loading and finish it later
val loading = FakeThemeManager(ThemeState.Loading)
loading.emit(ThemeState.Ready(ThemeSettings()))
```

## Migrating from 1.x

| 1.x | 2.0 |
|---|---|
| `com.github.MustafaKoceerr:compose-theme-manager:1.0.4` | `com.github.MustafaKoceerr.compose-theme-manager:theme-manager-compose:2.0.0` (+ `theme-manager-hilt` if you use Hilt) |
| `AppThemeMode` | `ThemeMode` |
| `GetThemeModeUseCase()` | `themeManager.themeState` |
| `SetThemeModeUseCase(mode)` | `themeManager.setThemeMode(mode)` |
| `CoreTheme(themeMode, light, dark, typography, shapes, dynamicColor)` | `ManagedTheme(ThemeSettings(mode, useDynamicColor), light, dark, typography, shapes)` |
| Hilt required (2.54+) | Any DI or none |

The selection saved by 1.x is read by 2.0 as is; users keep their theme after the update.
If your app relied on use cases, define them in your app's domain layer on top of `ThemeManager`.

## FAQ

**Q: The system splash screen is still light when the user chose Dark. Why?**
A: The system draws the splash screen before any app code runs, so it follows the device theme. Everything the app
draws, from the first frame on, uses the stored theme.

**Q: Dynamic color is enabled but my color schemes are not used.**
A: On API 31+ dynamic color replaces your schemes. Below API 31 it's ignored and your schemes are used. Use
`isDynamicColorAvailable` to hide or disable the toggle on older devices.

**Q: What is the default on first launch?**
A: `ThemeSettings()`: follow the system, dynamic color off.

## License

This project is licensed under the MIT License. See [LICENSE.txt](LICENSE.txt) for details.
