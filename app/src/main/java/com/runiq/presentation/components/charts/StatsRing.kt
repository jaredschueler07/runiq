package com.runiq.presentation.components.charts

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQTheme
import kotlin.math.PI

/**
 * Data class for stats ring configuration
 */
data class StatsRingData(
    val value: Float,
    val maxValue: Float,
    val label: String,
    val color: Color,
    val unit: String = ""
)

/**
 * Circular progress ring component for displaying stats
 * Used for goals, progress indicators, and metrics visualization
 */
@Composable
fun StatsRing(
    data: StatsRingData,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 8.dp,
    showCenterText: Boolean = true,
    animationDuration: Int = 1000
) {
    val progress by animateFloatAsState(
        targetValue = (data.value / data.maxValue).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration),
        label = "progress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val center = androidx.compose.ui.geometry.Offset(
                x = this.size.width / 2,
                y = this.size.height / 2
            )
            
            // Background circle
            drawCircle(
                color = data.color.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
            
            // Progress arc
            val sweepAngle = progress * 360f
            if (sweepAngle > 0) {
                drawArc(
                    color = data.color,
                    startAngle = -90f, // Start from top
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = center.x - radius,
                        y = center.y - radius
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = radius * 2,
                        height = radius * 2
                    ),
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
        
        if (showCenterText) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${data.value.toInt()}${data.unit}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = data.color
                )
                Text(
                    text = data.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Multiple stats rings in a row layout
 */
@Composable
fun StatsRingRow(
    rings: List<StatsRingData>,
    modifier: Modifier = Modifier,
    ringSize: Dp = 100.dp,
    strokeWidth: Dp = 6.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        rings.forEach { ringData ->
            StatsRing(
                data = ringData,
                size = ringSize,
                strokeWidth = strokeWidth,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Single Stats Ring")
@Composable
private fun StatsRingPreview() {
    RunIQTheme {
        StatsRing(
            data = StatsRingData(
                value = 7.5f,
                maxValue = 10f,
                label = "Distance",
                color = MaterialTheme.colorScheme.primary,
                unit = "km"
            )
        )
    }
}

@Preview(showBackground = true, name = "Full Progress Ring")
@Composable
private fun StatsRingFullPreview() {
    RunIQTheme {
        StatsRing(
            data = StatsRingData(
                value = 100f,
                maxValue = 100f,
                label = "Goal",
                color = MaterialTheme.colorScheme.secondary,
                unit = "%"
            )
        )
    }
}

@Preview(showBackground = true, name = "Multiple Stats Rings")
@Composable
private fun StatsRingRowPreview() {
    RunIQTheme {
        StatsRingRow(
            rings = listOf(
                StatsRingData(
                    value = 7.5f,
                    maxValue = 10f,
                    label = "Distance",
                    color = MaterialTheme.colorScheme.primary,
                    unit = "km"
                ),
                StatsRingData(
                    value = 45f,
                    maxValue = 60f,
                    label = "Time",
                    color = MaterialTheme.colorScheme.secondary,
                    unit = "min"
                ),
                StatsRingData(
                    value = 350f,
                    maxValue = 500f,
                    label = "Calories",
                    color = MaterialTheme.colorScheme.tertiary,
                    unit = "cal"
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Small Rings")
@Composable
private fun StatsRingSmallPreview() {
    RunIQTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsRing(
                data = StatsRingData(
                    value = 3f,
                    maxValue = 5f,
                    label = "Runs",
                    color = MaterialTheme.colorScheme.primary
                ),
                size = 80.dp,
                strokeWidth = 4.dp
            )
            
            StatsRing(
                data = StatsRingData(
                    value = 15f,
                    maxValue = 20f,
                    label = "Weekly",
                    color = MaterialTheme.colorScheme.secondary,
                    unit = "km"
                ),
                size = 80.dp,
                strokeWidth = 4.dp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun StatsRingDarkPreview() {
    RunIQTheme {
        StatsRingRow(
            rings = listOf(
                StatsRingData(
                    value = 7.5f,
                    maxValue = 10f,
                    label = "Distance",
                    color = MaterialTheme.colorScheme.primary,
                    unit = "km"
                ),
                StatsRingData(
                    value = 45f,
                    maxValue = 60f,
                    label = "Time",
                    color = MaterialTheme.colorScheme.secondary,
                    unit = "min"
                )
            )
        )
    }
}