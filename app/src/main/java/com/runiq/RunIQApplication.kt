package com.runiq

import android.app.Application
import com.runiq.core.config.ServiceInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * RunIQ Application class
 * Handles app-wide initialization and dependency injection setup
 */
@HiltAndroidApp
class RunIQApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize external services
        ServiceInitializer.initialize(this)
    }
}