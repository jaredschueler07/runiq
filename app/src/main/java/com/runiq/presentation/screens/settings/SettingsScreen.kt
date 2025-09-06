package com.runiq.presentation.screens.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Settings screen - App preferences and configuration
 * Central hub for all app settings and preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMusicSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock settings state - in real app these would come from ViewModel
    var notificationsEnabled by remember { mutableStateOf(true) }
    var voiceCoachingEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = RunIQSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sectionSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.sm))
            }
            
            // App Preferences
            item {
                SettingsSection(title = "App Preferences") {
                    SettingsToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Run reminders and achievements",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    SettingsToggleItem(
                        icon = Icons.Default.MusicNote,
                        title = "Voice Coaching",
                        subtitle = "AI coach audio during runs",
                        checked = voiceCoachingEnabled,
                        onCheckedChange = { voiceCoachingEnabled = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    SettingsClickableItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = "Auto (System default)",
                        onClick = { /* TODO: Open theme selector */ }
                    )
                }
            }
            
            // Data & Sync
            item {
                SettingsSection(title = "Data & Sync") {
                    SettingsToggleItem(
                        icon = Icons.Default.Storage,
                        title = "Auto Sync",
                        subtitle = "Sync runs to cloud automatically",
                        checked = autoSyncEnabled,
                        onCheckedChange = { autoSyncEnabled = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    SettingsClickableItem(
                        icon = Icons.Default.MusicNote,
                        title = "Music Settings",
                        subtitle = "Spotify and audio preferences",
                        onClick = onNavigateToMusicSettings
                    )
                }
            }
            
            // Privacy & Security
            item {
                SettingsSection(title = "Privacy & Security") {
                    SettingsClickableItem(
                        icon = Icons.Default.Lock,
                        title = "Privacy Policy",
                        subtitle = "How we handle your data",
                        onClick = { /* TODO: Open privacy policy */ }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    SettingsClickableItem(
                        icon = Icons.Default.Security,
                        title = "Data Export",
                        subtitle = "Download your running data",
                        onClick = { /* TODO: Open data export */ }
                    )
                }
            }
            
            // About
            item {
                SettingsSection(title = "About") {
                    SettingsClickableItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0 (Beta)",
                        onClick = { /* TODO: Show version info */ }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    SettingsClickableItem(
                        icon = Icons.Default.Info,
                        title = "Terms of Service",
                        subtitle = "Legal information",
                        onClick = { /* TODO: Open terms */ }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.xl))
            }
        }
    }
}

/**
 * Settings section wrapper
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small)
        ) {
            content()
        }
    }
}

/**
 * Settings item with toggle switch
 */
@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(RunIQSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Settings item with click action
 */
@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(RunIQSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen")
@Composable
private fun SettingsScreenPreview() {
    RunIQTheme {
        SettingsScreen(
            onNavigateBack = { },
            onNavigateToMusicSettings = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Settings Dark")
@Composable
private fun SettingsScreenDarkPreview() {
    RunIQTheme {
        SettingsScreen(
            onNavigateBack = { },
            onNavigateToMusicSettings = { }
        )
    }
}