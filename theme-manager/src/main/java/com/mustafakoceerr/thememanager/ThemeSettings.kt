package com.mustafakoceerr.thememanager

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * The user's theme preferences.
 *
 * @property mode light, dark or follow the device.
 * @property useDynamicColor use colors derived from the wallpaper (Material You) instead of the app's own color
 * schemes. Only takes effect where [isDynamicColorAvailable] is `true`.
 */
public data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false,
)

/**
 * Whether the device supports dynamic color (API 31+). Use it to hide or disable a dynamic color toggle.
 */
@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
public val isDynamicColorAvailable: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
