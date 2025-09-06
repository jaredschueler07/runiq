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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Permissions screen - Request necessary app permissions
 * Explains why each permission is needed and requests them
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onPermissionsGranted: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock permission states - in real app these would come from system
    var locationGranted by remember { mutableStateOf(false) }
    var healthConnectGranted by remember { mutableStateOf(false) }
    var notificationsGranted by remember { mutableStateOf(false) }
    
    val allPermissionsGranted = locationGranted && healthConnectGranted && notificationsGranted
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "App Permissions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(RunIQSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sectionSpacing)
        ) {
            // Header description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
            ) {
                Text(
                    text = "Enable Key Features",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "RunIQ needs these permissions to provide the best running experience. You can manage these in settings later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            // Permission cards
            Column(
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
            ) {
                PermissionCard(
                    icon = Icons.Default.LocationOn,
                    title = "Location Access",
                    description = "Track your runs with GPS and provide accurate distance, pace, and route mapping.",
                    isGranted = locationGranted,
                    onToggle = { locationGranted = !locationGranted },
                    isRequired = true
                )
                
                PermissionCard(
                    icon = Icons.Default.MonitorHeart,
                    title = "Health Connect",
                    description = "Sync with your fitness ecosystem and access heart rate data for comprehensive tracking.",
                    isGranted = healthConnectGranted,
                    onToggle = { healthConnectGranted = !healthConnectGranted },
                    isRequired = true
                )
                
                PermissionCard(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    description = "Receive coaching tips, run reminders, and achievement notifications.",
                    isGranted = notificationsGranted,
                    onToggle = { notificationsGranted = !notificationsGranted },
                    isRequired = false
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
            ) {
                Button(
                    onClick = onPermissionsGranted,
                    enabled = allPermissionsGranted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (allPermissionsGranted) "Continue to RunIQ" else "Grant All Permissions",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                OutlinedButton(
                    onClick = onPermissionsGranted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue Without Some Permissions",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Text(
                text = "You can enable these permissions later in your device settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(RunIQSpacing.sm))
        }
    }
}

/**
 * Permission card component
 */
@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onToggle: () -> Unit,
    isRequired: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isGranted) RunIQElevation.medium else RunIQElevation.small
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RunIQSpacing.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (isRequired) {
                        Text(
                            text = "Required",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Status indicator
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Granted",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Permissions Screen")
@Composable
private fun PermissionsScreenPreview() {
    RunIQTheme {
        PermissionsScreen(
            onPermissionsGranted = { },
            onNavigateBack = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Permissions Dark")
@Composable
private fun PermissionsScreenDarkPreview() {
    RunIQTheme {
        PermissionsScreen(
            onPermissionsGranted = { },
            onNavigateBack = { }
        )
    }
}