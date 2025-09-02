package com.runiq.di

import android.content.Context
import com.runiq.core.error.GlobalErrorHandler
import com.runiq.data.local.preferences.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides PreferencesManager for DataStore operations
     */
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    /**
     * Provides GlobalErrorHandler for centralized error management
     */
    @Provides
    @Singleton
    fun provideGlobalErrorHandler(
        @ApplicationContext context: Context
    ): GlobalErrorHandler {
        return GlobalErrorHandler(context)
    }
}