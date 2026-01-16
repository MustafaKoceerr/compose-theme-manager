# Compose Theme Manager (DataStore + Hilt)

A small, production-ready theme manager for **Jetpack Compose Material 3**.  
It persists the user’s theme choice via **DataStore Preferences** and exposes a clean, testable API with **use-cases** and a single **CoreTheme** entry point.

- ✅ Theme modes: **System / Light / Dark**
- ✅ Persistence: **DataStore Preferences**
- ✅ DI: **Hilt**
- ✅ UI: **Material 3** + optional **Dynamic Color (Android 12+)**

---

## Requirements

- **Hilt: 2.54+ is required (minimum).**  
  This library relies on the newer Hilt/KSP2-related setup introduced after 2.54; **Hilt versions below 2.54 are not supported and will not work.**

---

## Installation (JitPack)

**Step 1)** Add JitPack to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2)** Add the dependency:

```kotlin
dependencies {
    implementation("com.github.MustafaKoceerr:compose-theme-manager:1.0.3")
}
```

---

## Public API

### `AppThemeMode`

```kotlin
enum class AppThemeMode { SYSTEM, LIGHT, DARK }
```

### `CoreTheme(...)`

Main entry point for applying theme logic and Material 3 styling.

```kotlin
@Composable
fun CoreTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,   // optional
    lightColorScheme: ColorScheme,
    darkColorScheme: ColorScheme,
    typography: Typography = MaterialTheme.typography, // optional
    shapes: Shapes = MaterialTheme.shapes,             // optional
    dynamicColor: Boolean = false,                     // optional (Android 12+)
    content: @Composable () -> Unit
)
```

### Use cases

- `GetThemeModeUseCase(): Flow<AppThemeMode>`
- `SetThemeModeUseCase(mode: AppThemeMode)`

---

## Usage Example (recommended)

### 1) ViewModel

Expose a `StateFlow<AppThemeMode>` and update it via use cases.

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = getThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppThemeMode.SYSTEM
        )

    fun updateTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode)
        }
    }
}
```

### 2) Apply `CoreTheme` in Activity / Root Composable

This example uses **all CoreTheme parameters**, with optional ones clearly shown.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            // Provide your own Material 3 color schemes
            val lightScheme: ColorScheme = TestLightColors
            val darkScheme: ColorScheme = TestDarkColors

            CoreTheme(
                themeMode = themeMode,
                lightColorScheme = lightScheme,
                darkColorScheme = darkScheme,

                // Optional:
                typography = MaterialTheme.typography,
                shapes = MaterialTheme.shapes,

                // Optional (Android 12+). If true, dynamic colors override your schemes on supported devices.
                dynamicColor = true
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ThemeSwitchingScreen(
                        currentMode = themeMode,
                        onThemeChange = { newMode -> viewModel.updateTheme(newMode) }
                    )
                }
            }
        }
    }
}
```

---

## Notes

- If `dynamicColor = true` and the device supports it (**Android 12+**), the system dynamic color scheme is used.
- If `dynamicColor = false`, your `lightColorScheme` / `darkColorScheme` are applied based on `themeMode`.

---

## FAQ

**Q: I set `dynamicColor = true`, but my `lightColorScheme` / `darkColorScheme` colors are not applied. Why?**  
A: On Android 12+ devices, when `dynamicColor = true`, the system **Material You (dynamic)** colors take priority and will override the color schemes you pass in.  
- If you always want to use your own colors: set `dynamicColor = false`.  
- On unsupported devices (Android 11 and below): even if `dynamicColor = true`, your provided schemes are used automatically.

**Q: Which theme is selected when `themeMode = SYSTEM`?**  
A: The app follows the device’s system theme. If the system is dark, Dark is used; otherwise Light is used.

**Q: What is the default theme on first launch?**  
A: If there is no stored value (or it is invalid), the library falls back to `AppThemeMode.SYSTEM`.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
