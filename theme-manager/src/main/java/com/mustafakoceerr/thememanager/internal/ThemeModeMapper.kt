package com.mustafakoceerr.thememanager.internal

import com.mustafakoceerr.thememanager.ThemeMode

// Stored values are fixed strings rather than enum names, so they survive renames and R8.
// They match what 1.x stored, so existing selections are kept.
internal fun ThemeMode.toStoredValue(): String = when (this) {
    ThemeMode.SYSTEM -> "SYSTEM"
    ThemeMode.LIGHT -> "LIGHT"
    ThemeMode.DARK -> "DARK"
}

/** Missing or unknown values fall back to [ThemeMode.SYSTEM]. */
internal fun String?.toThemeMode(): ThemeMode = when (this) {
    "LIGHT" -> ThemeMode.LIGHT
    "DARK" -> ThemeMode.DARK
    else -> ThemeMode.SYSTEM
}
