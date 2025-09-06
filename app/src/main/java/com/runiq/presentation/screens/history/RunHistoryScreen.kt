package com.runiq.presentation.screens.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.cards.RunMetricsCard
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.charts.StatsRingRow
import com.runiq.presentation.components.lists.RunHistoryEntry
import com.runiq.presentation.components.lists.RunHistoryItem
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import java.time.LocalDateTime

/**
 * Run history screen - Displays all past runs with filtering and stats
 * Shows weekly/monthly summaries and detailed run list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunHistoryScreen(
    onNavigateToRunDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock data for preview - in real app this would come from ViewModel
    val mockRuns = generateMockRunHistory()
    val weeklyStats = calculateWeeklyStats(mockRuns)
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Run History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Open search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search runs"
                        )
                    }
                    IconButton(onClick = { /* TODO: Open filters */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter runs"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (mockRuns.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "No runs yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Start your first run to see your progress here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
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
                
                // Weekly summary section
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                    ) {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        RunMetricsCard(
                            metrics = weeklyStats,
                            isCompact = true
                        )
                        
                        StatsRingRow(
                            rings = listOf(
                                StatsRingData(
                                    value = 4f,
                                    maxValue = 5f,
                                    label = "Runs",
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                StatsRingData(
                                    value = 18.7f,
                                    maxValue = 25f,
                                    label = "Distance",
                                    color = MaterialTheme.colorScheme.secondary,
                                    unit = "km"
                                ),
                                StatsRingData(
                                    value = 95f,
                                    maxValue = 120f,
                                    label = "Time",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    unit = "min"
                                )
                            ),
                            ringSize = 80.dp
                        )
                    }
                }
                
                // Filter/Sort row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "All Runs (${mockRuns.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            text = "Recent first",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Run history list
                items(mockRuns) { run ->
                    RunHistoryItem(
                        run = run,
                        onClick = onNavigateToRunDetail,
                        showDivider = run != mockRuns.last()
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(RunIQSpacing.xl))
                }
            }
        }
    }
}

/**
 * Generate mock run history data for preview
 */
private fun generateMockRunHistory(): List<RunHistoryEntry> {
    return listOf(
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
            date = LocalDateTime.now().minusDays(2),
            distance = 3.1,
            duration = "16:23",
            pace = "5:17",
            calories = 198,
            workoutType = "Recovery Run"
        ),
        RunHistoryEntry(
            id = "3",
            date = LocalDateTime.now().minusDays(4),
            distance = 8.1,
            duration = "41:23",
            pace = "5:06",
            calories = 567,
            workoutType = "Long Run"
        ),
        RunHistoryEntry(
            id = "4",
            date = LocalDateTime.now().minusDays(6),
            distance = 2.3,
            duration = "12:45",
            pace = "5:32",
            calories = 156,
            workoutType = "Easy Run"
        ),
        RunHistoryEntry(
            id = "5",
            date = LocalDateTime.now().minusDays(8),
            distance = 6.4,
            duration = "32:18",
            pace = "5:03",
            calories = 423,
            workoutType = "Tempo Run"
        ),
        RunHistoryEntry(
            id = "6",
            date = LocalDateTime.now().minusDays(10),
            distance = 4.7,
            duration = "24:12",
            pace = "5:09",
            calories = 287,
            workoutType = "Easy Run"
        )
    )
}

/**
 * Calculate weekly statistics from run history
 */
private fun calculateWeeklyStats(runs: List<RunHistoryEntry>): RunMetrics {
    val weeklyRuns = runs.filter { 
        it.date.isAfter(LocalDateTime.now().minusDays(7)) 
    }
    
    if (weeklyRuns.isEmpty()) {
        return RunMetrics()
    }
    
    val totalDistance = weeklyRuns.sumOf { it.distance }
    val totalDurationMinutes = weeklyRuns.sumOf { 
        val parts = it.duration.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    }
    val averagePace = weeklyRuns.map { 
        val parts = it.pace.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    }.average() / 60.0
    val totalCalories = weeklyRuns.sumOf { it.calories ?: 0 }
    
    return RunMetrics(
        pace = String.format("%d:%02d", averagePace.toInt(), ((averagePace % 1) * 60).toInt()),
        distance = String.format("%.1f", totalDistance),
        duration = String.format("%d:%02d", totalDurationMinutes / 60, totalDurationMinutes % 60),
        calories = totalCalories
    )
}

@Preview(showBackground = true, name = "Run History Screen")
@Composable
private fun RunHistoryScreenPreview() {
    RunIQTheme {
        RunHistoryScreen(
            onNavigateToRunDetail = { }
        )
    }
}

@Preview(showBackground = true, name = "Empty Run History")
@Composable
private fun RunHistoryScreenEmptyPreview() {
    RunIQTheme {
        // Override the mock data to be empty
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = {
                        Text(
                            text = "Run History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "No runs yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Start your first run to see your progress here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Run History Dark")
@Composable
private fun RunHistoryScreenDarkPreview() {
    RunIQTheme {
        RunHistoryScreen(
            onNavigateToRunDetail = { }
        )
    }
}