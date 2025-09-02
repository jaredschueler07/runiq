package com.runiq

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.runiq.core.config.ConfigValidator
import com.runiq.core.config.SecretKeys
import com.runiq.ui.theme.RunIQTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Log configuration status for debugging
        logConfigurationStatus()
        
        setContent {
            RunIQTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        WelcomeScreen()
                    }
                }
            }
        }
    }
    
    private fun logConfigurationStatus() {
        Log.d(TAG, "=== RunIQ Configuration Status ===")
        
        val validationResult = ConfigValidator.validateConfiguration()
        if (validationResult.isValid) {
            Log.i(TAG, "✅ All external services configured properly")
        } else {
            Log.w(TAG, "⚠️ Configuration issues detected:")
            validationResult.issues.forEach { issue ->
                Log.w(TAG, "  - $issue")
            }
        }
        
        // Log service availability (without exposing keys)
        Log.d(TAG, "Service availability:")
        Log.d(TAG, "  - Spotify: ${if (SecretKeys.SPOTIFY_CLIENT_ID.isNotBlank()) "Available" else "Not configured"}")
        Log.d(TAG, "  - Eleven Labs: ${if (SecretKeys.ELEVEN_LABS_API_KEY.isNotBlank()) "Available" else "Not configured"}")
        Log.d(TAG, "  - Google Maps: ${if (SecretKeys.MAPS_API_KEY.isNotBlank()) "Available" else "Not configured"}")
        
        Log.d(TAG, "=== End Configuration Status ===")
    }
}

@Composable
fun WelcomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to RunIQ!\n🏃‍♂️ AI-Powered Running Coach",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    RunIQTheme {
        WelcomeScreen()
    }
}