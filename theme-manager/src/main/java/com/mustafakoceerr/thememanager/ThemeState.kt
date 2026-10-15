package com.mustafakoceerr.thememanager

/**
 * The stored [ThemeSettings], or [Loading] while they are read from disk.
 *
 * Reading takes a few milliseconds on cold start. Keep the splash screen visible while the state is [Loading]
 * so the first frame is drawn with the user's theme instead of flashing the default one.
 */
public sealed interface ThemeState {

    /** The settings have not been read yet. */
    public data object Loading : ThemeState

    /** The stored settings. */
    public data class Ready(val settings: ThemeSettings) : ThemeState
}
