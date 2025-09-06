package com.runiq.presentation.screens.profile

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.charts.StatsRingRow
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Profile screen - User profile, settings, and statistics
 * Central hub for user preferences and account management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToCoachSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
            
            // User info section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(RunIQSpacing.cardPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                    ) {
                        // Profile picture placeholder
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile picture",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        // User info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
                        ) {
                            Text(
                                text = "Runner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = "Member since January 2024",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "🏃‍♂️ 47 runs completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Monthly stats
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "This Month",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    StatsRingRow(
                        rings = listOf(
                            StatsRingData(
                                value = 12f,
                                maxValue = 15f,
                                label = "Runs",
                                color = MaterialTheme.colorScheme.primary
                            ),
                            StatsRingData(
                                value = 67.3f,
                                maxValue = 80f,
                                label = "Distance",
                                color = MaterialTheme.colorScheme.secondary,
                                unit = "km"
                            ),
                            StatsRingData(
                                value = 342f,
                                maxValue = 400f,
                                label = "Minutes",
                                color = MaterialTheme.colorScheme.tertiary,
                                unit = "min"
                            )
                        )
                    )
                }
            }
            
            // Personal Records
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "Personal Records",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small)
                    ) {
                        Column(
                            modifier = Modifier.padding(RunIQSpacing.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                        ) {
                            PersonalRecordItem(
                                label = "Longest Run",
                                value = "21.1 km",
                                subtitle = "Half Marathon • March 15"
                            )
                            
                            HorizontalDivider()
                            
                            PersonalRecordItem(
                                label = "Fastest 5K",
                                value = "22:45",
                                subtitle = "4:33/km • February 28"
                            )
                            
                            HorizontalDivider()
                            
                            PersonalRecordItem(
                                label = "Most Active Week",
                                value = "45.2 km",
                                subtitle = "6 runs • January 8-14"
                            )
                        }
                    }
                }
            }
            
            // Settings and preferences
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small)
                    ) {
                        Column {
                            SettingsItem(
                                icon = Icons.Default.Person,
                                title = "AI Coach",
                                subtitle = "Alex • Motivational style",
                                onClick = onNavigateToCoachSelection
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                            
                            SettingsItem(
                                icon = Icons.Default.MusicNote,
                                title = "Music Settings",
                                subtitle = "Spotify connected",
                                onClick = { /* TODO: Navigate to music settings */ }
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = RunIQSpacing.cardPadding))
                            
                            SettingsItem(
                                icon = Icons.Default.Settings,
                                title = "App Settings",
                                subtitle = "Notifications, privacy, data",
                                onClick = onNavigateToSettings
                            )
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
 * Personal record item component
 */
@Composable
private fun PersonalRecordItem(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
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
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Settings item component
 */
@Composable
private fun SettingsItem(
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

@Preview(showBackground = true, name = "Profile Screen")
@Composable
private fun ProfileScreenPreview() {
    RunIQTheme {
        ProfileScreen(
            onNavigateToSettings = { },
            onNavigateToCoachSelection = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Profile Screen Dark")
@Composable
private fun ProfileScreenDarkPreview() {
    RunIQTheme {
        ProfileScreen(
            onNavigateToSettings = { },
            onNavigateToCoachSelection = { }
        )
    }
}