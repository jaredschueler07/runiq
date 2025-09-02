// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    
    // Compose Compiler plugin (required for Kotlin 2.0+)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.3" apply false
    
    // Add Firebase Crashlytics plugin
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    
    // Hilt dependency injection
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}