package com.runiq.core.config

import com.runiq.BuildConfig

/**
 * Centralized access point for all API keys and secrets.
 * Keys are injected from BuildConfig which reads from local.properties
 * 
 * Security Note: All keys are stored in local.properties and injected at build time
 * to avoid hardcoding sensitive information in source code.
 */
object SecretKeys {
    
    /**
     * Spotify Client ID for music integration
     * Used for: Spotify SDK authentication and playlist access
     */
    const val SPOTIFY_CLIENT_ID: String = BuildConfig.SPOTIFY_CLIENT_ID
    
    /**
     * Spotify Client Secret for OAuth flow
     * Used for: Spotify API token exchange and refresh
     */
    const val SPOTIFY_CLIENT_SECRET: String = BuildConfig.SPOTIFY_CLIENT_SECRET
    
    /**
     * Eleven Labs API key for AI voice synthesis
     * Used for: Converting coaching text to natural speech
     */
    const val ELEVEN_LABS_API_KEY: String = BuildConfig.ELEVEN_LABS_API_KEY
    
    /**
     * Google Maps API key for location services
     * Used for: Route mapping and location-based features
     */
    const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY
    
    /**
     * Google Gemini API key for AI coaching
     * Used for: Generating personalized coaching messages
     */
    const val GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY
    
    /**
     * Validates that all required API keys are present
     * @return true if all keys are configured, false otherwise
     */
    fun areAllKeysConfigured(): Boolean {
        return SPOTIFY_CLIENT_ID.isNotBlank() &&
               SPOTIFY_CLIENT_SECRET.isNotBlank() &&
               ELEVEN_LABS_API_KEY.isNotBlank() &&
               MAPS_API_KEY.isNotBlank() &&
               GEMINI_API_KEY.isNotBlank()
    }
    
    /**
     * Returns a list of missing API keys for debugging purposes
     * @return List of missing key names
     */
    fun getMissingKeys(): List<String> {
        val missingKeys = mutableListOf<String>()
        
        if (SPOTIFY_CLIENT_ID.isBlank()) missingKeys.add("SPOTIFY_CLIENT_ID")
        if (SPOTIFY_CLIENT_SECRET.isBlank()) missingKeys.add("SPOTIFY_CLIENT_SECRET")
        if (ELEVEN_LABS_API_KEY.isBlank()) missingKeys.add("ELEVEN_LABS_API_KEY") 
        if (MAPS_API_KEY.isBlank()) missingKeys.add("MAPS_API_KEY")
        if (GEMINI_API_KEY.isBlank()) missingKeys.add("GEMINI_API_KEY")
        
        return missingKeys
    }
}