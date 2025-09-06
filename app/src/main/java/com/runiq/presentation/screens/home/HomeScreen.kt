package com.runiq.presentation.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.presentation.components.buttons.RunButtonState
import com.runiq.presentation.components.buttons.StartRunButton
import com.runiq.presentation.components.cards.CoachPersonality
import com.runiq.presentation.components.cards.CoachPersonalityCard
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.cards.RunMetricsCard
import com.runiq.presentation.components.charts.StatsRing
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.charts.StatsRingRow
import com.runiq.presentation.components.lists.RunHistoryEntry
import com.runiq.presentation.components.lists.RunHistoryItem
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import java.time.LocalDateTime

/**
 * Home screen - Main dashboard of the app
 * Shows quick stats, start run button, and recent activity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToActiveRun: () -> Unit,
    onNavigateToCoachSelection: () -> Unit,
    onNavigateToRunDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Good morning!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ready to run?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to settings */ }) {
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
            
            // Start Run Button Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    StartRunButton(
                        state = RunButtonState.READY,
                        onStartRun = onNavigateToActiveRun,
                        onPauseRun = { },
                        onResumeRun = { },
                        onStopRun = { }
                    )
                    
                    Text(
                        text = "Tap to start your run",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Weekly Goals Section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    StatsRingRow(
                        rings = listOf(
                            StatsRingData(
                                value = 15.2f,
                                maxValue = 20f,
                                label = "Distance",
                                color = MaterialTheme.colorScheme.primary,
                                unit = "km"
                            ),
                            StatsRingData(
                                value = 3f,
                                maxValue = 4f,
                                label = "Runs",
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            StatsRingData(
                                value = 180f,
                                maxValue = 240f,
                                label = "Minutes",
                                color = MaterialTheme.colorScheme.tertiary,
                                unit = "min"
                            )
                        )
                    )
                }
            }
            
            // Current Coach Section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Coach",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        IconButton(onClick = onNavigateToCoachSelection) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Change coach",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    CoachPersonalityCard(
                        coach = CoachPersonality(
                            id = "current",
                            name = "Alex",
                            description = "Your motivational running coach ready to push you to new limits!",
                            personality = "Motivational",
                            color = MaterialTheme.colorScheme.primary
                        ),
                        isSelected = true,
                        onSelect = { onNavigateToCoachSelection() }
                    )
                }
            }
            
            // Recent Activity Section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    // Mock recent runs
                    val recentRuns = listOf(
                        RunHistoryEntry(
                            id = "1",
                            date = LocalDateTime.now().minusDays(1),
                            distance = 5.2,
                            duration = "26:45",
                            pace = "5:08",
                            calories = 342,
                            workoutType = "Easy Run"
                        ),
                        RunHistoryEntry(
                            id = "2",
                            date = LocalDateTime.now().minusDays(3),
                            distance = 8.1,
                            duration = "41:23",
                            pace = "5:06",
                            calories = 567,
                            workoutType = "Long Run"
                        ),
                        RunHistoryEntry(
                            id = "3",
                            date = LocalDateTime.now().minusDays(5),
                            distance = 3.5,
                            duration = "18:12",
                            pace = "5:12",
                            workoutType = "Recovery Run"
                        )
                    )
                    
                    recentRuns.take(3).forEachIndexed { index, run ->
                        RunHistoryItem(
                            run = run,
                            onClick = onNavigateToRunDetail,
                            showDivider = index < 2
                        )
                    }
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.xl))
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen")
@Composable
private fun HomeScreenPreview() {
    RunIQTheme {
        HomeScreen(
            onNavigateToActiveRun = { },
            onNavigateToCoachSelection = { },
            onNavigateToRunDetail = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Home Screen Dark")
@Composable
private fun HomeScreenDarkPreview() {
    RunIQTheme {
        HomeScreen(
            onNavigateToActiveRun = { },
            onNavigateToCoachSelection = { },
            onNavigateToRunDetail = { }
        )
    }
}