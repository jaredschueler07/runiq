package com.runiq.presentation.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.CaloriesColor
import com.runiq.ui.theme.DistanceColor
import com.runiq.ui.theme.HeartRateColor
import com.runiq.ui.theme.ModernAnimations
import com.runiq.ui.theme.ModernEffects
import com.runiq.ui.theme.PaceColor
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Modern version of RunMetricsCard using 21st.dev design principles
 * Features glassmorphism, gradients, and micro-interactions
 */
@Composable
fun ModernRunMetricsCard(
    metrics: RunMetrics,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    variant: ModernCardVariant = ModernCardVariant.GLASS
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fadeInUp()
            .scaleIn(delay = 100),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (variant) {
                ModernCardVariant.GLASS -> 0.dp
                ModernCardVariant.ELEVATED -> 12.dp
                ModernCardVariant.GRADIENT -> 8.dp
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (variant) {
                ModernCardVariant.GLASS -> Color.Transparent
                ModernCardVariant.ELEVATED -> MaterialTheme.colorScheme.surface
                ModernCardVariant.GRADIENT -> Color.Transparent
            }
        ),
        shape = RoundedCornerShape(ModernEffects.Radius.lg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    when (variant) {
                        ModernCardVariant.GLASS -> Modifier.glassmorphism()
                        ModernCardVariant.GRADIENT -> Modifier.background(
                            ModernEffects.Gradients.surfaceGradient()
                        )
                        else -> Modifier
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    if (isCompact) RunIQSpacing.md else RunIQSpacing.lg
                ),
                verticalArrangement = Arrangement.spacedBy(
                    if (isCompact) RunIQSpacing.sm else RunIQSpacing.md
                )
            ) {
                if (!isCompact) {
                    // Header with gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(ModernEffects.Radius.sm))
                            .background(ModernEffects.Gradients.heroGradient())
                            .padding(RunIQSpacing.sm)
                    ) {
                        Text(
                            text = "Live Metrics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                
                // Primary metrics with modern styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModernMetricItem(
                        label = "Pace",
                        value = "${metrics.pace}/km",
                        color = PaceColor,
                        modifier = Modifier.weight(1f),
                        isCompact = isCompact,
                        animationDelay = ModernAnimations.staggeredDelay(0)
                    )
                    ModernMetricItem(
                        label = "Distance",
                        value = "${metrics.distance} km",
                        color = DistanceColor,
                        modifier = Modifier.weight(1f),
                        isCompact = isCompact,
                        animationDelay = ModernAnimations.staggeredDelay(1)
                    )
                    ModernMetricItem(
                        label = "Time",
                        value = metrics.duration,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        isCompact = isCompact,
                        animationDelay = ModernAnimations.staggeredDelay(2)
                    )
                }
                
                // Secondary metrics with gradient backgrounds
                if (!isCompact && (metrics.heartRate != null || metrics.calories != null)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (metrics.heartRate != null) {
                            ModernMetricItem(
                                label = "Heart Rate",
                                value = "${metrics.heartRate} bpm",
                                color = HeartRateColor,
                                modifier = Modifier.weight(1f),
                                showBackground = true,
                                animationDelay = ModernAnimations.staggeredDelay(3)
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                        
                        if (metrics.calories != null) {
                            ModernMetricItem(
                                label = "Calories",
                                value = "${metrics.calories} cal",
                                color = CaloriesColor,
                                modifier = Modifier.weight(1f),
                                showBackground = true,
                                animationDelay = ModernAnimations.staggeredDelay(4)
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                        
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Modern metric item with enhanced visual design
 */
@Composable
private fun ModernMetricItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    showBackground: Boolean = false,
    animationDelay: Int = 0
) {
    Column(
        modifier = modifier
            .fadeInUp(delay = animationDelay)
            .then(
                if (showBackground) {
                    Modifier
                        .clip(RoundedCornerShape(ModernEffects.Radius.md))
                        .background(
                            ModernEffects.Gradients.metricGradient(color)
                        )
                        .padding(RunIQSpacing.sm)
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
    ) {
        // Value with modern typography
        Text(
            text = value,
            style = if (isCompact) {
                MaterialTheme.typography.headlineSmall
            } else {
                MaterialTheme.typography.displayMedium
            },
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = if (!isCompact) Modifier.pulse() else Modifier
        )
        
        // Label with subtle styling
        Text(
            text = label,
            style = if (isCompact) {
                MaterialTheme.typography.labelSmall
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        
        // Modern accent line
        if (!isCompact) {
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 2.dp)
                    .clip(RoundedCornerShape(ModernEffects.Radius.full))
                    .background(color.copy(alpha = 0.6f))
            )
        }
    }
}

/**
 * Card variant options for different visual styles
 */
enum class ModernCardVariant {
    GLASS,      // Glassmorphism effect
    ELEVATED,   // Traditional elevated card
    GRADIENT    // Gradient background
}

// Extension functions for modern animations
@Composable
private fun Modifier.fadeInUp(delay: Int = 0): Modifier = 
    this.then(ModernAnimations.fadeInUp(delay = delay))

@Composable
private fun Modifier.scaleIn(delay: Int = 0): Modifier = 
    this.then(ModernAnimations.scaleIn(delay = delay))

@Composable
private fun Modifier.pulse(): Modifier = 
    this.then(ModernAnimations.pulse())

@Composable
private fun Modifier.glassmorphism(): Modifier = 
    this.then(ModernEffects.glassmorphism())

@Preview(showBackground = true, name = "Modern Glass Card")
@Composable
private fun ModernRunMetricsCardGlassPreview() {
    RunIQTheme {
        ModernRunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            ),
            variant = ModernCardVariant.GLASS
        )
    }
}

@Preview(showBackground = true, name = "Modern Elevated Card")
@Composable
private fun ModernRunMetricsCardElevatedPreview() {
    RunIQTheme {
        ModernRunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            ),
            variant = ModernCardVariant.ELEVATED
        )
    }
}

@Preview(showBackground = true, name = "Modern Gradient Card")
@Composable
private fun ModernRunMetricsCardGradientPreview() {
    RunIQTheme {
        ModernRunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            ),
            variant = ModernCardVariant.GRADIENT
        )
    }
}

@Preview(showBackground = true, name = "Modern Compact Card")
@Composable
private fun ModernRunMetricsCardCompactPreview() {
    RunIQTheme {
        ModernRunMetricsCard(
            metrics = RunMetrics(
                pace = "5:45",
                distance = "2.15",
                duration = "12:23"
            ),
            isCompact = true,
            variant = ModernCardVariant.GLASS
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Modern Dark Theme")
@Composable
private fun ModernRunMetricsCardDarkPreview() {
    RunIQTheme {
        ModernRunMetricsCard(
            metrics = RunMetrics(
                pace = "5:30",
                distance = "3.42",
                duration = "18:45",
                heartRate = 145,
                calories = 287
            ),
            variant = ModernCardVariant.GLASS
        )
    }
}