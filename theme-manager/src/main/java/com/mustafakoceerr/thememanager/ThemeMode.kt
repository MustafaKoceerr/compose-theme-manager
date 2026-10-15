package com.mustafakoceerr.thememanager

/**
 * Whether the app uses a light or dark theme.
 */
public enum class ThemeMode {
    /** Follows the device's dark theme setting. */
    SYSTEM,

    /** Always light, regardless of the device setting. */
    LIGHT,

    /** Always dark, regardless of the device setting. */
    DARK,
}
