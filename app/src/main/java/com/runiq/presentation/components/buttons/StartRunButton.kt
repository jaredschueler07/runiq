package com.runiq.presentation.components.buttons

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQTheme

/**
 * Enum representing different states of the run button
 */
enum class RunButtonState {
    READY,    // Ready to start a run
    RUNNING,  // Currently running (can pause)
    PAUSED    // Run is paused (can resume or stop)
}

/**
 * Large circular button for starting, pausing, and controlling runs
 * Central component of the home screen and active run screen
 */
@Composable
fun StartRunButton(
    state: RunButtonState,
    onStartRun: () -> Unit,
    onPauseRun: () -> Unit,
    onResumeRun: () -> Unit,
    onStopRun: () -> Unit,
    modifier: Modifier = Modifier,
    size: StartRunButtonSize = StartRunButtonSize.Large
) {
    val buttonColor by animateColorAsState(
        targetValue = when (state) {
            RunButtonState.READY -> MaterialTheme.colorScheme.primary
            RunButtonState.RUNNING -> MaterialTheme.colorScheme.secondary
            RunButtonState.PAUSED -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(300),
        label = "button_color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (state == RunButtonState.RUNNING) 1.1f else 1.0f,
        animationSpec = tween(300),
        label = "button_scale"
    )
    
    val (icon, contentDescription, onClick) = when (state) {
        RunButtonState.READY -> Triple(
            Icons.Default.PlayArrow,
            "Start run",
            onStartRun
        )
        RunButtonState.RUNNING -> Triple(
            Icons.Default.Pause,
            "Pause run",
            onPauseRun
        )
        RunButtonState.PAUSED -> Triple(
            Icons.Default.PlayArrow,
            "Resume run",
            onResumeRun
        )
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(size.dp)
                .scale(scale),
            shape = CircleShape,
            containerColor = buttonColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(size.iconSize)
            )
        }
        
        // Stop button overlay when paused
        if (state == RunButtonState.PAUSED) {
            FloatingActionButton(
                onClick = onStopRun,
                modifier = Modifier
                    .size((size.dp.value * 0.4f).dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop run",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Size variations for the start run button
 */
enum class StartRunButtonSize(val dp: androidx.compose.ui.unit.Dp, val iconSize: androidx.compose.ui.unit.Dp) {
    Small(64.dp, 24.dp),
    Medium(80.dp, 32.dp),
    Large(120.dp, 48.dp),
    ExtraLarge(160.dp, 64.dp)
}

@Preview(showBackground = true, name = "Ready State")
@Composable
private fun StartRunButtonReadyPreview() {
    RunIQTheme {
        StartRunButton(
            state = RunButtonState.READY,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { }
        )
    }
}

@Preview(showBackground = true, name = "Running State")
@Composable
private fun StartRunButtonRunningPreview() {
    RunIQTheme {
        StartRunButton(
            state = RunButtonState.RUNNING,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { }
        )
    }
}

@Preview(showBackground = true, name = "Paused State")
@Composable
private fun StartRunButtonPausedPreview() {
    RunIQTheme {
        StartRunButton(
            state = RunButtonState.PAUSED,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { }
        )
    }
}

@Preview(showBackground = true, name = "Small Size")
@Composable
private fun StartRunButtonSmallPreview() {
    RunIQTheme {
        StartRunButton(
            state = RunButtonState.READY,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { },
            size = StartRunButtonSize.Small
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun StartRunButtonDarkPreview() {
    RunIQTheme {
        StartRunButton(
            state = RunButtonState.RUNNING,
            onStartRun = { },
            onPauseRun = { },
            onResumeRun = { },
            onStopRun = { }
        )
    }
}