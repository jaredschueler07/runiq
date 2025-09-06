package com.runiq.presentation.screens.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Onboarding screen - First-time user welcome and introduction
 * Introduces the app's key features and value proposition
 */
@Composable
fun OnboardingScreen(
    onNavigateToPermissions: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(RunIQSpacing.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App icon/logo
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = "RunIQ Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(RunIQSpacing.xl))
            
            // Welcome text
            Text(
                text = "Welcome to RunIQ",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(RunIQSpacing.md))
            
            Text(
                text = "Your AI-powered running companion",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(RunIQSpacing.sectionSpacing))
            
            // Feature highlights
            Column(
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.lg),
                horizontalAlignment = Alignment.Start
            ) {
                FeatureItem(
                    emoji = "🤖",
                    title = "AI Coaching",
                    description = "Get personalized coaching with different personality styles"
                )
                
                FeatureItem(
                    emoji = "🎵",
                    title = "Music Sync",
                    description = "BPM-matched playlists that sync with your running cadence"
                )
                
                FeatureItem(
                    emoji = "📊",
                    title = "Health Connect",
                    description = "Seamlessly integrates with your fitness ecosystem"
                )
                
                FeatureItem(
                    emoji = "🏃‍♂️",
                    title = "Smart Tracking",
                    description = "Advanced GPS tracking with real-time voice coaching"
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
            ) {
                Button(
                    onClick = onNavigateToPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Skip for Now",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(RunIQSpacing.md))
        }
    }
}

/**
 * Feature item component for onboarding highlights
 */
@Composable
private fun FeatureItem(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.size(32.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Onboarding Screen")
@Composable
private fun OnboardingScreenPreview() {
    RunIQTheme {
        OnboardingScreen(
            onNavigateToPermissions = { },
            onSkip = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Onboarding Dark")
@Composable
private fun OnboardingScreenDarkPreview() {
    RunIQTheme {
        OnboardingScreen(
            onNavigateToPermissions = { },
            onSkip = { }
        )
    }
}