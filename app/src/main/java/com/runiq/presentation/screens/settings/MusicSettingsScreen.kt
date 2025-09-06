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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import kotlin.math.roundToInt

/**
 * Music settings screen - Configure music and audio preferences
 * Manage Spotify integration and audio coaching settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock settings state - in real app these would come from ViewModel
    var spotifyConnected by remember { mutableStateOf(true) }
    var bpmMatchingEnabled by remember { mutableStateOf(true) }
    var audioCoachingEnabled by remember { mutableStateOf(true) }
    var musicVolume by remember { mutableFloatStateOf(0.8f) }
    var coachVolume by remember { mutableFloatStateOf(0.7f) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Music Settings",
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
            
            // Spotify Integration
            item {
                MusicSettingsSection(title = "Music Service") {
                    SpotifyConnectionCard(
                        isConnected = spotifyConnected,
                        onConnect = { spotifyConnected = true },
                        onDisconnect = { spotifyConnected = false }
                    )
                }
            }
            
            // BPM Matching
            item {
                MusicSettingsSection(title = "Smart Features") {
                    MusicToggleItem(
                        icon = Icons.Default.Speed,
                        title = "BPM Matching",
                        subtitle = "Match music tempo to your running cadence",
                        checked = bpmMatchingEnabled,
                        onCheckedChange = { bpmMatchingEnabled = it },
                        enabled = spotifyConnected
                    )
                    
                    if (bpmMatchingEnabled && spotifyConnected) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                        
                        Column(
                            modifier = Modifier.padding(RunIQSpacing.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                        ) {
                            Text(
                                text = "Target Cadence: 174 steps/min",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Music will be selected to match your running rhythm for optimal performance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Audio Levels
            item {
                MusicSettingsSection(title = "Audio Levels") {
                    VolumeSliderItem(
                        icon = Icons.Default.MusicNote,
                        title = "Music Volume",
                        value = musicVolume,
                        onValueChange = { musicVolume = it }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                    
                    VolumeSliderItem(
                        icon = Icons.Default.VolumeUp,
                        title = "Coach Voice Volume",
                        value = coachVolume,
                        onValueChange = { coachVolume = it }
                    )
                }
            }
            
            // Audio Coaching
            item {
                MusicSettingsSection(title = "Voice Coaching") {
                    MusicToggleItem(
                        icon = Icons.Default.PlayArrow,
                        title = "Audio Coaching",
                        subtitle = "Receive voice coaching during runs",
                        checked = audioCoachingEnabled,
                        onCheckedChange = { audioCoachingEnabled = it }
                    )
                    
                    if (audioCoachingEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                        
                        Column(
                            modifier = Modifier.padding(RunIQSpacing.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                        ) {
                            Text(
                                text = "Coaching Frequency",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CoachingFrequencyOption("Minimal", isSelected = false) { }
                                CoachingFrequencyOption("Balanced", isSelected = true) { }
                                CoachingFrequencyOption("Frequent", isSelected = false) { }
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.xl))
            }
        }
    }
}

/**
 * Music settings section wrapper
 */
@Composable
private fun MusicSettingsSection(
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
 * Spotify connection card
 */
@Composable
private fun SpotifyConnectionCard(
    isConnected: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(RunIQSpacing.cardPadding),
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Default.QueueMusic,
                contentDescription = "Spotify",
                tint = if (isConnected) Color(0xFF1DB954) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Spotify",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = if (isConnected) "Connected" else "Not connected",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isConnected) Color(0xFF1DB954) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isConnected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Connected",
                    tint = Color(0xFF1DB954),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        if (isConnected) {
            Text(
                text = "Access to your playlists and BPM-matched music recommendations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedButton(
                onClick = onDisconnect,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Disconnect Spotify")
            }
        } else {
            Text(
                text = "Connect Spotify to access your music and enable BPM matching for optimal running performance.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect to Spotify")
            }
        }
    }
}

/**
 * Music toggle item component
 */
@Composable
private fun MusicToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
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
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

/**
 * Volume slider item component
 */
@Composable
private fun VolumeSliderItem(
    icon: ImageVector,
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(RunIQSpacing.cardPadding),
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "${(value * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}

/**
 * Coaching frequency option button
 */
@Composable
private fun CoachingFrequencyOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Button(onClick = onClick) {
            Text(text)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(text)
        }
    }
}

@Preview(showBackground = true, name = "Music Settings Screen")
@Composable
private fun MusicSettingsScreenPreview() {
    RunIQTheme {
        MusicSettingsScreen(
            onNavigateBack = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Music Settings Dark")
@Composable
private fun MusicSettingsScreenDarkPreview() {
    RunIQTheme {
        MusicSettingsScreen(
            onNavigateBack = { }
        )
    }
}