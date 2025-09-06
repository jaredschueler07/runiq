package com.runiq.presentation.components.lists

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.runiq.ui.theme.PaceColor
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class representing a run history entry
 */
data class RunHistoryEntry(
    val id: String,
    val date: LocalDateTime,
    val distance: Double, // in kilometers
    val duration: String, // formatted duration
    val pace: String, // formatted pace
    val calories: Int? = null,
    val workoutType: String = "Run"
)

/**
 * List item component for displaying run history entries
 * Used in the history screen and home screen recent runs
 */
@Composable
fun RunHistoryItem(
    run: RunHistoryEntry,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(run.id) },
            elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(RunIQSpacing.cardPadding),
                horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Run icon
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = "Run",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                // Run details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
                ) {
                    // Date and workout type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = run.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = run.workoutType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Time
                    Text(
                        text = run.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.lg)
                    ) {
                        // Distance
                        MetricText(
                            label = "Distance",
                            value = "${String.format("%.2f", run.distance)} km",
                            color = DistanceColor
                        )
                        
                        // Duration
                        MetricText(
                            label = "Time",
                            value = run.duration,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Pace
                        MetricText(
                            label = "Pace",
                            value = "${run.pace}/km",
                            color = PaceColor
                        )
                        
                        // Calories (if available)
                        run.calories?.let { calories ->
                            MetricText(
                                label = "Calories",
                                value = "$calories cal",
                                color = CaloriesColor
                            )
                        }
                    }
                }
                
                // Chevron indicator
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = RunIQSpacing.md),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

/**
 * Small metric text component
 */
@Composable
private fun MetricText(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Run History Item")
@Composable
private fun RunHistoryItemPreview() {
    RunIQTheme {
        RunHistoryItem(
            run = RunHistoryEntry(
                id = "1",
                date = LocalDateTime.now().minusDays(1),
                distance = 5.42,
                duration = "28:45",
                pace = "5:18",
                calories = 387,
                workoutType = "Long Run"
            ),
            onClick = { }
        )
    }
}

@Preview(showBackground = true, name = "Run Without Calories")
@Composable
private fun RunHistoryItemNoCaloriesPreview() {
    RunIQTheme {
        RunHistoryItem(
            run = RunHistoryEntry(
                id = "2",
                date = LocalDateTime.now().minusDays(3),
                distance = 3.21,
                duration = "18:32",
                pace = "5:47",
                workoutType = "Easy Run"
            ),
            onClick = { }
        )
    }
}

@Preview(showBackground = true, name = "Multiple Items")
@Composable
private fun RunHistoryItemsPreview() {
    RunIQTheme {
        Column {
            RunHistoryItem(
                run = RunHistoryEntry(
                    id = "1",
                    date = LocalDateTime.now().minusDays(1),
                    distance = 5.42,
                    duration = "28:45",
                    pace = "5:18",
                    calories = 387,
                    workoutType = "Long Run"
                ),
                onClick = { }
            )
            
            RunHistoryItem(
                run = RunHistoryEntry(
                    id = "2",
                    date = LocalDateTime.now().minusDays(3),
                    distance = 3.21,
                    duration = "18:32",
                    pace = "5:47",
                    workoutType = "Easy Run"
                ),
                onClick = { }
            )
            
            RunHistoryItem(
                run = RunHistoryEntry(
                    id = "3",
                    date = LocalDateTime.now().minusDays(5),
                    distance = 8.15,
                    duration = "42:18",
                    pace = "5:11",
                    calories = 621,
                    workoutType = "Tempo Run"
                ),
                onClick = { },
                showDivider = false
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun RunHistoryItemDarkPreview() {
    RunIQTheme {
        RunHistoryItem(
            run = RunHistoryEntry(
                id = "1",
                date = LocalDateTime.now().minusDays(1),
                distance = 5.42,
                duration = "28:45",
                pace = "5:18",
                calories = 387,
                workoutType = "Long Run"
            ),
            onClick = { }
        )
    }
}