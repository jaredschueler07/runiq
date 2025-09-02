package com.runiq

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for RunIQ - AI-powered running coach app
 * Initializes Hilt dependency injection and core services
 */
@HiltAndroidApp
class RunIQApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO: Plant production tree (e.g., Crashlytics tree)
            Timber.plant(ReleaseTree())
        }
        
        Timber.d("RunIQ Application initialized")
    }
}

/**
 * Production logging tree that filters sensitive information
 */
private class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // In production, only log warnings and errors
        if (priority >= android.util.Log.WARN) {
            // TODO: Send to crash reporting service
            // FirebaseCrashlytics.getInstance().log("$tag: $message")
            // if (t != null) {
            //     FirebaseCrashlytics.getInstance().recordException(t)
            // }
        }
    }
}