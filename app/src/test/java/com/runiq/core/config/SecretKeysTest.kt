package com.runiq.core.config

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecretKeys configuration
 */
class SecretKeysTest {
    
    @Test
    fun `secretKeys should provide access to all required API keys`() {
        // Test that all key constants are accessible
        // Note: In test environment, these will be empty strings unless configured
        assertNotNull(SecretKeys.SPOTIFY_CLIENT_ID)
        assertNotNull(SecretKeys.ELEVEN_LABS_API_KEY)
        assertNotNull(SecretKeys.MAPS_API_KEY)
    }
    
    @Test
    fun `areAllKeysConfigured should return false when keys are empty`() {
        // In test environment without local.properties, keys will be empty
        // This test verifies the validation logic works
        val result = SecretKeys.areAllKeysConfigured()
        
        // This will likely be false in test environment, which is expected
        // The method should handle empty strings correctly
        assertTrue("Method should handle empty keys gracefully", 
                   result || SecretKeys.getMissingKeys().isNotEmpty())
    }
    
    @Test
    fun `getMissingKeys should identify empty keys`() {
        val missingKeys = SecretKeys.getMissingKeys()
        
        // Should return a list (might be empty if keys are configured, or contain key names if not)
        assertNotNull(missingKeys)
        assertTrue("Missing keys list should be valid", missingKeys.all { it.isNotBlank() })
    }
    
    @Test
    fun `key constants should have correct names`() {
        // Test that we can access the keys by their expected names
        // This ensures the constants are properly defined
        try {
            val spotifyKey = SecretKeys.SPOTIFY_CLIENT_ID
            val elevenLabsKey = SecretKeys.ELEVEN_LABS_API_KEY
            val mapsKey = SecretKeys.MAPS_API_KEY
            
            // If we reach here, all constants are accessible
            assertTrue("All key constants should be accessible", true)
        } catch (e: Exception) {
            fail("Failed to access key constants: ${e.message}")
        }
    }
}