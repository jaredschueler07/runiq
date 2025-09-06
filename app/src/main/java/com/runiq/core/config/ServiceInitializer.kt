package com.runiq.core.config

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Handles initialization of external services
 * Call from Application.onCreate() to set up all required services
 */
object ServiceInitializer {
    
    private const val TAG = "ServiceInitializer"
    
    /**
     * Initializes all external services
     * @param context Application context
     */
    fun initialize(context: Context) {
        Log.d(TAG, "Initializing external services...")
        
        // Validate configuration first
        val validationResult = ConfigValidator.validateConfiguration()
        ConfigValidator.logConfigurationStatus()
        
        if (!validationResult.isValid) {
            Log.w(TAG, "⚠️ Some services may not work properly due to configuration issues")
        }
        
        // Initialize Firebase
        initializeFirebase(context)
        
        // Initialize other services
        initializeSpotify()
        initializeElevenLabs()
        initializeMaps()
        
        Log.i(TAG, "✅ External services initialization completed")
    }
    
    /**
     * Initialize Firebase services
     */
    private fun initializeFirebase(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
            Log.d(TAG, "✅ Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase initialization failed", e)
        }
    }
    
    /**
     * Validate Spotify configuration
     */
    private fun initializeSpotify() {
        if (SecretKeys.SPOTIFY_CLIENT_ID.isNotBlank()) {
            Log.d(TAG, "✅ Spotify configuration validated")
        } else {
            Log.w(TAG, "⚠️ Spotify Client ID not configured")
        }
    }
    
    /**
     * Validate Eleven Labs configuration
     */
    private fun initializeElevenLabs() {
        if (SecretKeys.ELEVEN_LABS_API_KEY.isNotBlank()) {
            Log.d(TAG, "✅ Eleven Labs configuration validated")
        } else {
            Log.w(TAG, "⚠️ Eleven Labs API key not configured")
        }
    }
    
    /**
     * Validate Google Maps configuration
     */
    private fun initializeMaps() {
        if (SecretKeys.MAPS_API_KEY.isNotBlank()) {
            Log.d(TAG, "✅ Google Maps configuration validated")
        } else {
            Log.w(TAG, "⚠️ Google Maps API key not configured")
        }
    }
}