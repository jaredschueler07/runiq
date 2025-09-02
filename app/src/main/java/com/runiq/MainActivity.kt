package com.runiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.runiq.presentation.theme.RunIQTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for RunIQ app
 * Entry point that sets up the Compose UI and navigation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            RunIQTheme {
                RunIQApp()
            }
        }
    }
}

/**
 * Main app composable that sets up the overall app structure
 */
@Composable
fun RunIQApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            // TODO: Add navigation and main app content
            // For now, just a placeholder
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                androidx.compose.material3.Text(
                    text = "RunIQ - Coming Soon",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(androidx.compose.foundation.layout.Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunIQAppPreview() {
    RunIQTheme {
        RunIQApp()
    }
}