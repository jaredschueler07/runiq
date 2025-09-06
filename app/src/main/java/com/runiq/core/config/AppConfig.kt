package com.runiq.core.config

/**
 * Application-wide configuration constants
 * Contains non-sensitive configuration values and feature flags
 */
object AppConfig {
    
    // App Information
    const val APP_NAME = "RunIQ"
    const val APP_VERSION = "1.0.0"
    
    // Health Connect Configuration
    const val HEALTH_CONNECT_PACKAGE_NAME = "com.google.android.apps.healthdata"
    
    // Location Tracking
    const val LOCATION_UPDATE_INTERVAL_MS = 1000L // 1 second
    const val MIN_LOCATION_DISTANCE_METERS = 5f
    const val GPS_ACCURACY_THRESHOLD_METERS = 10f
    
    // Audio Configuration
    const val AUDIO_DUCKING_FACTOR = 0.3f
    const val TTS_SPEECH_RATE = 1.0f
    const val TTS_PITCH = 1.0f
    
    // Coaching Configuration
    const val DEFAULT_COACHING_INTERVAL_MS = 30000L // 30 seconds
    const val MIN_COACHING_INTERVAL_MS = 15000L // 15 seconds
    const val MAX_COACHING_INTERVAL_MS = 120000L // 2 minutes
    
    // Network Configuration
    const val API_TIMEOUT_SECONDS = 30L
    const val CONNECT_TIMEOUT_SECONDS = 10L
    const val READ_TIMEOUT_SECONDS = 30L
    
    // Cache Configuration
    const val MAX_CACHE_SIZE_MB = 50L
    const val CACHE_EXPIRY_HOURS = 24L
    
    // Workout Defaults
    const val DEFAULT_WORKOUT_DURATION_MS = 1800000L // 30 minutes
    const val MIN_WORKOUT_DURATION_MS = 300000L // 5 minutes
    const val MAX_WORKOUT_DURATION_MS = 14400000L // 4 hours
    
    // Feature Flags
    const val ENABLE_AI_COACHING = true
    const val ENABLE_SPOTIFY_INTEGRATION = true
    const val ENABLE_VOICE_SYNTHESIS = true
    const val ENABLE_OFFLINE_MODE = true
    
    // Debug Configuration
    const val ENABLE_DEBUG_LOGGING = true
    const val ENABLE_PERFORMANCE_MONITORING = true
}