package com.runiq.presentation.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.CaloriesColor
import com.runiq.ui.theme.DistanceColor
import com.runiq.ui.theme.HeartRateColor
import com.runiq.ui.theme.PaceColor
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Data class representing run metrics to display
 */
data class RunMetrics(
    val pace: String = "--:--",
    val distance: String = "0.00",
    val duration: String = "00:00",
    val heartRate: Int? = null,
    val calories: Int? = null
)

/**
 * Card component displaying key run metrics
 * Used during active runs and in run history
 */
@Composable
fun RunMetricsCard(
    metrics: RunMetrics,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(RunIQSpacing.metricsCardPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.metricsSpacing)
        ) {
            if (!isCompact) {
                // Primary metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "Pace",
                        value = "${metrics.pace}/km",
                        color = PaceColor,
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "Distance",
                        value = "${metrics.distance} km",
                        color = DistanceColor,
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "Time",
                        value = metrics.duration,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Secondary metrics row (if available)
                if (metrics.heartRate != null || metrics.calories != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (metrics.heartRate != null) {
                            MetricItem(
                                label = "Heart Rate",
                                value = "${metrics.heartRate} bpm",
                                color = HeartRateColor,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty space to maintain layout
                            Column(modifier = Modifier.weight(1f)) {}
                        }
                        
                        if (metrics.calories != null) {
                            MetricItem(
                                label = "Calories",
                                value = "${metrics.calories} cal",
                                color = CaloriesColor,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty space to maintain layout
                            Column(modifier = Modifier.weight(1f)) {}
                        }
                        
                        // Always have a third column for symmetry
                        Column(modifier = Modifier.weight(1f)) {}
                    }
                }
            } else {
                // Compact layout - single row with most important metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "Pace",
                        value = "${metrics.pace}/km",
                        color = PaceColor,
                        modifier = Modifier.weight(1f),
                        isCompact = true
                    )
                    MetricItem(
                        label = "Distance",
                        value = "${metrics.distance} km",
                        color = DistanceColor,
                        modifier = Modifier.weight(1f),
                        isCompact = true
                    )
                    MetricItem(
                        label = "Time",
                        value = metrics.duration,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        isCompact = true
                    )
                }
            }
        }
    }
}

/**
 * Individual metric item within the card
 */
@Composable
private fun MetricItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.metricLabelSpacing)
    ) {
        Text(
            text = value,
            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.displaySmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = if (isCompact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Full Metrics Card")
@Composable
private fun RunMetricsCardPreview() {
    RunIQTheme {
        RunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            )
        )
    }
}

@Preview(showBackground = true, name = "Basic Metrics Card")
@Composable
private fun RunMetricsCardBasicPreview() {
    RunIQTheme {
        RunMetricsCard(
            metrics = RunMetrics(
                pace = "6:12",
                distance = "1.25",
                duration = "07:45"
            )
        )
    }
}

@Preview(showBackground = true, name = "Compact Metrics Card")
@Composable
private fun RunMetricsCardCompactPreview() {
    RunIQTheme {
        RunMetricsCard(
            metrics = RunMetrics(
                pace = "5:45",
                distance = "2.15",
                duration = "12:23",
                heartRate = 152
            ),
            isCompact = true
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun RunMetricsCardDarkPreview() {
    RunIQTheme {
        RunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            )
        )
    }
}