package com.mustafakoceerr.thememanager.hilt

import android.content.Context
import com.mustafakoceerr.thememanager.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the process-wide [ThemeManager] to the Hilt [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
public object ThemeManagerModule {

    @Provides
    @Singleton
    public fun provideThemeManager(@ApplicationContext context: Context): ThemeManager =
        ThemeManager.create(context)
}
