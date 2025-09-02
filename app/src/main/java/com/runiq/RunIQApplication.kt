package com.runiq

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RunIQApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging for debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("RunIQ Application initialized")
    }
}