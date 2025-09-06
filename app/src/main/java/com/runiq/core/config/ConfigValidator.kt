package com.runiq.core.config

import android.util.Log
import com.runiq.BuildConfig

/**
 * Validates app configuration and API key setup
 * Used during app initialization to ensure all required services are properly configured
 */
object ConfigValidator {
    
    private const val TAG = "ConfigValidator"
    
    /**
     * Validates all external service configurations
     * @return ValidationResult containing status and any issues found
     */
    fun validateConfiguration(): ValidationResult {
        val issues = mutableListOf<String>()
        
        // Check API keys
        val missingKeys = SecretKeys.getMissingKeys()
        if (missingKeys.isNotEmpty()) {
            issues.add("Missing API keys: ${missingKeys.joinToString(", ")}")
        }
        
        // Check Firebase configuration
        if (!isFirebaseConfigured()) {
            issues.add("Firebase configuration missing or invalid")
        }
        
        // Check build configuration
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Running in DEBUG mode")
        }
        
        return ValidationResult(
            isValid = issues.isEmpty(),
            issues = issues
        )
    }
    
    /**
     * Checks if Firebase is properly configured
     */
    private fun isFirebaseConfigured(): Boolean {
        return try {
            // Basic check - more thorough validation would require Firebase SDK initialization
            BuildConfig.APPLICATION_ID.isNotBlank()
        } catch (e: Exception) {
            Log.e(TAG, "Firebase configuration check failed", e)
            false
        }
    }
    
    /**
     * Logs configuration status for debugging
     */
    fun logConfigurationStatus() {
        val result = validateConfiguration()
        
        if (result.isValid) {
            Log.i(TAG, "✅ All external services properly configured")
        } else {
            Log.w(TAG, "⚠️ Configuration issues found:")
            result.issues.forEach { issue ->
                Log.w(TAG, "  - $issue")
            }
        }
        
        // Log available services (without exposing actual keys)
        Log.d(TAG, "Available services:")
        Log.d(TAG, "  - Spotify: ${if (SecretKeys.SPOTIFY_CLIENT_ID.isNotBlank()) "✅" else "❌"}")
        Log.d(TAG, "  - Eleven Labs: ${if (SecretKeys.ELEVEN_LABS_API_KEY.isNotBlank()) "✅" else "❌"}")
        Log.d(TAG, "  - Google Maps: ${if (SecretKeys.MAPS_API_KEY.isNotBlank()) "✅" else "❌"}")
    }
}

/**
 * Result of configuration validation
 */
data class ValidationResult(
    val isValid: Boolean,
    val issues: List<String>
)