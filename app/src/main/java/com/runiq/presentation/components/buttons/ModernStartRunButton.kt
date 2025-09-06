package com.runiq.presentation.components.buttons

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.ModernAnimations
import com.runiq.ui.theme.ModernEffects
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern version of StartRunButton with 21st.dev design principles
 * Features advanced animations, glassmorphism, and micro-interactions
 */
@Composable
fun ModernStartRunButton(
    state: RunButtonState,
    onStartRun: () -> Unit,
    onPauseRun: () -> Unit,
    onResumeRun: () -> Unit,
    onStopRun: () -> Unit,
    modifier: Modifier = Modifier,
    size: ModernButtonSize = ModernButtonSize.Large,
    variant: ModernButtonVariant = ModernButtonVariant.GLASSMORPHISM
) {
    val animatedScale by animateFloatAsState(
        targetValue = when (state) {
            RunButtonState.RUNNING -> 1.1f
            RunButtonState.PAUSED -> 0.95f
            RunButtonState.READY -> 1.0f
        },
        animationSpec = ModernAnimations.springAnimation(),
        label = "button_scale"
    )
    
    val animatedColor by animateColorAsState(
        targetValue = when (state) {
            RunButtonState.READY -> MaterialTheme.colorScheme.primary
            RunButtonState.RUNNING -> MaterialTheme.colorScheme.secondary
            RunButtonState.PAUSED -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(300),
        label = "button_color"
    )
    
    val (icon, contentDescription, onClick) = when (state) {
        RunButtonState.READY -> Triple(Icons.Default.PlayArrow, "Start run", onStartRun)
        RunButtonState.RUNNING -> Triple(Icons.Default.Pause, "Pause run", onPauseRun)
        RunButtonState.PAUSED -> Triple(Icons.Default.PlayArrow, "Resume run", onResumeRun)
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Main button with modern styling
        ModernCircularButton(
            onClick = onClick,
            color = animatedColor,
            size = size.dp,
            variant = variant,
            modifier = Modifier.scale(animatedScale)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(size.iconSize)
            )
        }
        
        // Animated progress ring for running state
        if (state == RunButtonState.RUNNING) {
            AnimatedProgressRing(
                size = size.dp + 16.dp,
                color = animatedColor,
                modifier = Modifier.scale(animatedScale)
            )
        }
        
        // Stop button overlay when paused
        if (state == RunButtonState.PAUSED) {
            ModernCircularButton(
                onClick = onStopRun,
                color = MaterialTheme.colorScheme.error,
                size = (size.dp.value * 0.4f).dp,
                variant = ModernButtonVariant.ELEVATED,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .scale(animatedScale)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop run",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        // Pulsing halo effect for ready state
        if (state == RunButtonState.READY) {
            PulsingHalo(
                size = size.dp + 32.dp,
                color = animatedColor.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * Modern circular button component
 */
@Composable
private fun ModernCircularButton(
    onClick: () -> Unit,
    color: Color,
    size: Dp,
    variant: ModernButtonVariant,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Surface(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = when (variant) {
                    ModernButtonVariant.ELEVATED -> ModernEffects.Shadows.large
                    ModernButtonVariant.GLASSMORPHISM -> 0.dp
                    ModernButtonVariant.GRADIENT -> ModernEffects.Shadows.medium
                },
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    radius = size / 2,
                    color = Color.White.copy(alpha = 0.3f)
                ),
                onClick = onClick
            ),
        shape = CircleShape,
        color = when (variant) {
            ModernButtonVariant.GLASSMORPHISM -> Color.Transparent
            else -> color
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    when (variant) {
                        ModernButtonVariant.GLASSMORPHISM -> Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        color.copy(alpha = 0.3f),
                                        color.copy(alpha = 0.1f)
                                    )
                                )
                            )
                        ModernButtonVariant.GRADIENT -> Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        color,
                                        color.copy(alpha = 0.8f)
                                    )
                                )
                            )
                        else -> Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Animated progress ring for running state
 */
@Composable
private fun AnimatedProgressRing(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val strokeWidth = 4.dp.toPx()
        val radius = (this.size.minDimension - strokeWidth) / 2
        
        rotate(degrees = -90f) {
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )
            
            // Animated progress arc would be implemented here
            // This is a simplified version
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 120f, // This would be animated based on progress
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }
    }
}

/**
 * Pulsing halo effect
 */
@Composable
private fun PulsingHalo(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(size)
            .then(ModernAnimations.pulse())
    ) {
        val radius = this.size.minDimension / 2
        
        // Draw multiple concentric circles with decreasing opacity
        for (i in 1..3) {
            drawCircle(
                color = color.copy(alpha = 0.1f / i),
                radius = radius * (0.7f + i * 0.1f),
                style = Stroke(width = (2 * i).dp.toPx())
            )
        }
    }
}

/**
 * Modern button size options
 */
enum class ModernButtonSize(val dp: Dp, val iconSize: Dp) {
    Small(72.dp, 28.dp),
    Medium(96.dp, 36.dp),
    Large(128.dp, 48.dp),
    ExtraLarge(160.dp, 64.dp)
}

/**
 * Modern button variant options
 */
enum class ModernButtonVariant {
    ELEVATED,       // Traditional elevated button
    GLASSMORPHISM,  // Glassmorphism effect
    GRADIENT        // Gradient background
}

@Preview(showBackground = true, name = "Modern Ready State")
@Composable
private fun ModernStartRunButtonReadyPreview() {
    RunIQTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xl)
        ) {
            ModernStartRunButton(
                state = RunButtonState.READY,
                onStartRun = { },
                onPauseRun = { },
                onResumeRun = { },
                onStopRun = { },
                variant = ModernButtonVariant.GLASSMORPHISM
            )
            Text("Glassmorphism Variant", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true, name = "Modern Running State")
@Composable
private fun ModernStartRunButtonRunningPreview() {
    RunIQTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xl)
        ) {
            ModernStartRunButton(
                state = RunButtonState.RUNNING,
                onStartRun = { },
                onPauseRun = { },
                onResumeRun = { },
                onStopRun = { },
                variant = ModernButtonVariant.GRADIENT
            )
            Text("Gradient Variant", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true, name = "Modern Paused State")
@Composable
private fun ModernStartRunButtonPausedPreview() {
    RunIQTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xl)
        ) {
            ModernStartRunButton(
                state = RunButtonState.PAUSED,
                onStartRun = { },
                onPauseRun = { },
                onResumeRun = { },
                onStopRun = { },
                variant = ModernButtonVariant.ELEVATED
            )
            Text("Elevated Variant", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true, name = "Modern Button Sizes")
@Composable
private fun ModernStartRunButtonSizesPreview() {
    RunIQTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.lg),
            modifier = Modifier.padding(RunIQSpacing.lg)
        ) {
            ModernButtonSize.values().forEach { size ->
                ModernStartRunButton(
                    state = RunButtonState.READY,
                    onStartRun = { },
                    onPauseRun = { },
                    onResumeRun = { },
                    onStopRun = { },
                    size = size
                )
                Text(
                    text = size.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Modern Dark Theme")
@Composable
private fun ModernStartRunButtonDarkPreview() {
    RunIQTheme {
        ModernStartRunButton(
            state = RunButtonState.RUNNING,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { },
            variant = ModernButtonVariant.GLASSMORPHISM
        )
    }
}