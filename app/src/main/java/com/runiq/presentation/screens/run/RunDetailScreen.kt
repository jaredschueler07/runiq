package com.runiq.presentation.screens.run

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.runiq.presentation.components.charts.PaceChart
import com.runiq.presentation.components.charts.PaceDataPoint
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.charts.StatsRingRow
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Run detail screen - Detailed view of a completed run
 * Shows comprehensive metrics, charts, and run analysis
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunDetailScreen(
    runId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock data for preview - in real app this would come from ViewModel
    val runData = getMockRunDetail(runId)
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = runData.workoutType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = runData.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share run */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share run"
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
            
            // Main metrics card
            item {
                RunMetricsCard(
                    metrics = runData.metrics
                )
            }
            
            // Achievement/Summary section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(RunIQSpacing.cardPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                    ) {
                        Text(
                            text = "🎉 Great Run!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = runData.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Pace analysis chart
            item {
                PaceChart(
                    data = runData.paceData,
                    title = "Pace Analysis"
                )
            }
            
            // Additional stats rings
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                ) {
                    Text(
                        text = "Performance Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    StatsRingRow(
                        rings = listOf(
                            StatsRingData(
                                value = 85f,
                                maxValue = 100f,
                                label = "Effort",
                                color = MaterialTheme.colorScheme.primary,
                                unit = "%"
                            ),
                            StatsRingData(
                                value = 92f,
                                maxValue = 100f,
                                label = "Consistency",
                                color = MaterialTheme.colorScheme.secondary,
                                unit = "%"
                            ),
                            StatsRingData(
                                value = 78f,
                                maxValue = 100f,
                                label = "Efficiency",
                                color = MaterialTheme.colorScheme.tertiary,
                                unit = "%"
                            )
                        )
                    )
                }
            }
            
            // Run details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small)
                ) {
                    Column(
                        modifier = Modifier.padding(RunIQSpacing.cardPadding),
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                    ) {
                        Text(
                            text = "Run Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        DetailRow(
                            label = "Start Time",
                            value = runData.date.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                        
                        DetailRow(
                            label = "Weather",
                            value = runData.weather
                        )
                        
                        DetailRow(
                            label = "Route Type",
                            value = runData.routeType
                        )
                        
                        DetailRow(
                            label = "Elevation Gain",
                            value = "${runData.elevationGain}m"
                        )
                        
                        DetailRow(
                            label = "Cadence",
                            value = "${runData.avgCadence} spm"
                        )
                    }
                }
            }
            
            // Coach feedback
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(RunIQSpacing.cardPadding),
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
                        ) {
                            Text(
                                text = "Coach Alex",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "💬",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        
                        Text(
                            text = runData.coachFeedback,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
 * Detail row component for run information
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Mock run detail data
 */
data class RunDetailData(
    val id: String,
    val date: LocalDateTime,
    val workoutType: String,
    val metrics: RunMetrics,
    val summary: String,
    val paceData: List<PaceDataPoint>,
    val weather: String,
    val routeType: String,
    val elevationGain: Int,
    val avgCadence: Int,
    val coachFeedback: String
)

private fun getMockRunDetail(runId: String): RunDetailData {
    return RunDetailData(
        id = runId,
        date = LocalDateTime.now().minusDays(1),
        workoutType = "Morning Run",
        metrics = RunMetrics(
            pace = "5:24",
            distance = "5.42",
            duration = "29:18",
            heartRate = 145,
            calories = 387
        ),
        summary = "Excellent pacing and consistent effort throughout the run. You maintained a steady rhythm and finished strong!",
        paceData = listOf(
            PaceDataPoint(0f, 5.8f),
            PaceDataPoint(5f, 5.6f),
            PaceDataPoint(10f, 5.3f),
            PaceDataPoint(15f, 5.2f),
            PaceDataPoint(20f, 5.1f),
            PaceDataPoint(25f, 5.0f),
            PaceDataPoint(29f, 4.9f)
        ),
        weather = "Sunny, 18°C",
        routeType = "Mixed terrain",
        elevationGain = 127,
        avgCadence = 174,
        coachFeedback = "Outstanding run today! Your pacing was spot-on and I loved seeing that negative split in the second half. This shows great race strategy development. Keep building on this momentum for your upcoming goals!"
    )
}

@Preview(showBackground = true, name = "Run Detail Screen")
@Composable
private fun RunDetailScreenPreview() {
    RunIQTheme {
        RunDetailScreen(
            runId = "1",
            onNavigateBack = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Run Detail Dark")
@Composable
private fun RunDetailScreenDarkPreview() {
    RunIQTheme {
        RunDetailScreen(
            runId = "1",
            onNavigateBack = { }
        )
    }
}