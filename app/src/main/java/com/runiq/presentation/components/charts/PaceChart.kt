package com.runiq.presentation.components.charts

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.PaceColor
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import kotlin.math.max
import kotlin.math.min

/**
 * Data point for pace chart
 */
data class PaceDataPoint(
    val timeMinutes: Float,
    val paceMinutesPerKm: Float
)

/**
 * Simple line chart for displaying pace over time
 * Used in run detail screens and active run monitoring
 */
@Composable
fun PaceChart(
    data: List<PaceDataPoint>,
    modifier: Modifier = Modifier,
    title: String = "Pace",
    lineColor: Color = PaceColor,
    fillColor: Color = PaceColor.copy(alpha = 0.1f),
    showFill: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.small),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(RunIQSpacing.cardPadding)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = RunIQSpacing.sm)
            )
            
            if (data.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    drawPaceChart(
                        data = data,
                        lineColor = lineColor,
                        fillColor = fillColor,
                        showFill = showFill
                    )
                }
            } else {
                Text(
                    text = "No pace data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 48.dp)
                )
            }
        }
    }
}

/**
 * Draw the pace chart on canvas
 */
private fun DrawScope.drawPaceChart(
    data: List<PaceDataPoint>,
    lineColor: Color,
    fillColor: Color,
    showFill: Boolean
) {
    if (data.size < 2) return
    
    val width = size.width
    val height = size.height
    val padding = 20f
    
    // Calculate bounds
    val minTime = data.minOf { it.timeMinutes }
    val maxTime = data.maxOf { it.timeMinutes }
    val minPace = data.minOf { it.paceMinutesPerKm }
    val maxPace = data.maxOf { it.paceMinutesPerKm }
    
    // Add some padding to the pace range
    val paceRange = maxPace - minPace
    val paddedMinPace = minPace - paceRange * 0.1f
    val paddedMaxPace = maxPace + paceRange * 0.1f
    
    val timeRange = maxTime - minTime
    val adjustedTimeRange = if (timeRange == 0f) 1f else timeRange
    
    // Convert data points to canvas coordinates
    val points = data.map { point ->
        val x = padding + (point.timeMinutes - minTime) / adjustedTimeRange * (width - 2 * padding)
        val y = height - padding - (point.paceMinutesPerKm - paddedMinPace) / (paddedMaxPace - paddedMinPace) * (height - 2 * padding)
        Offset(x, y)
    }
    
    // Draw fill area if enabled
    if (showFill && points.size >= 2) {
        val fillPath = Path().apply {
            moveTo(points.first().x, height - padding) // Bottom left
            points.forEach { point ->
                lineTo(point.x, point.y)
            }
            lineTo(points.last().x, height - padding) // Bottom right
            close()
        }
        
        drawPath(
            path = fillPath,
            color = fillColor
        )
    }
    
    // Draw the line
    if (points.size >= 2) {
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }
        
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
    
    // Draw data points
    points.forEach { point ->
        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = point
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = point
        )
    }
}

@Preview(showBackground = true, name = "Pace Chart with Data")
@Composable
private fun PaceChartPreview() {
    RunIQTheme {
        PaceChart(
            data = listOf(
                PaceDataPoint(0f, 6.0f),
                PaceDataPoint(5f, 5.8f),
                PaceDataPoint(10f, 5.5f),
                PaceDataPoint(15f, 5.7f),
                PaceDataPoint(20f, 5.3f),
                PaceDataPoint(25f, 5.6f),
                PaceDataPoint(30f, 5.4f)
            ),
            title = "Pace During Run"
        )
    }
}

@Preview(showBackground = true, name = "Empty Pace Chart")
@Composable
private fun PaceChartEmptyPreview() {
    RunIQTheme {
        PaceChart(
            data = emptyList(),
            title = "Pace Analysis"
        )
    }
}

@Preview(showBackground = true, name = "Single Data Point")
@Composable
private fun PaceChartSinglePointPreview() {
    RunIQTheme {
        PaceChart(
            data = listOf(
                PaceDataPoint(10f, 5.5f)
            ),
            title = "Current Pace"
        )
    }
}

@Preview(showBackground = true, name = "Steady Pace")
@Composable
private fun PaceChartSteadyPreview() {
    RunIQTheme {
        PaceChart(
            data = listOf(
                PaceDataPoint(0f, 5.5f),
                PaceDataPoint(10f, 5.5f),
                PaceDataPoint(20f, 5.5f),
                PaceDataPoint(30f, 5.5f)
            ),
            title = "Steady Pace Run",
            showFill = false
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun PaceChartDarkPreview() {
    RunIQTheme {
        PaceChart(
            data = listOf(
                PaceDataPoint(0f, 6.0f),
                PaceDataPoint(5f, 5.8f),
                PaceDataPoint(10f, 5.5f),
                PaceDataPoint(15f, 5.7f),
                PaceDataPoint(20f, 5.3f),
                PaceDataPoint(25f, 5.6f),
                PaceDataPoint(30f, 5.4f)
            ),
            title = "Pace During Run"
        )
    }
}