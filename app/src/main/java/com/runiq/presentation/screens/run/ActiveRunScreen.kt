package com.runiq.presentation.screens.run

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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.presentation.components.buttons.RunButtonState
import com.runiq.presentation.components.buttons.StartRunButton
import com.runiq.presentation.components.buttons.StartRunButtonSize
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.cards.RunMetricsCard
import com.runiq.presentation.components.music.CurrentTrack
import com.runiq.presentation.components.music.MusicControlBar
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Active run screen - Displayed during an active workout
 * Shows real-time metrics, controls, and coaching information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRunScreen(
    onNavigateBack: () -> Unit,
    onRunComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock state for preview - in real app this would come from ViewModel
    var runState by remember { mutableStateOf(RunButtonState.RUNNING) }
    var currentMetrics by remember { 
        mutableStateOf(
            RunMetrics(
                pace = "5:24",
                distance = "2.34",
                duration = "12:41",
                heartRate = 142,
                calories = 187
            )
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Active Run",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
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
                    IconButton(onClick = { /* TODO: Open map view */ }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map view",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Music control bar
            MusicControlBar(
                currentTrack = CurrentTrack(
                    title = "Thunder",
                    artist = "Imagine Dragons",
                    duration = 187000L,
                    currentPosition = 89000L,
                    isPlaying = true
                ),
                onPlayPause = { },
                onNext = { },
                onPrevious = { },
                onExpand = { },
                isCompact = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = RunIQSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sectionSpacing)
        ) {
            Spacer(modifier = Modifier.height(RunIQSpacing.md))
            
            // Large metrics display
            RunMetricsCard(
                metrics = currentMetrics,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Coaching message section
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
                        text = "Coach Alex",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Great pace! Keep this rhythm going. You're doing amazing!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Spacer to push control button to center
            Spacer(modifier = Modifier.weight(1f))
            
            // Control button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                StartRunButton(
                    state = runState,
                    size = StartRunButtonSize.ExtraLarge,
                    onStartRun = { 
                        runState = RunButtonState.RUNNING
                    },
                    onPauseRun = { 
                        runState = RunButtonState.PAUSED
                    },
                    onResumeRun = { 
                        runState = RunButtonState.RUNNING
                    },
                    onStopRun = { 
                        onRunComplete("mock_run_id")
                    }
                )
            }
            
            // Current status text
            Text(
                text = when (runState) {
                    RunButtonState.RUNNING -> "Running • Tap to pause"
                    RunButtonState.PAUSED -> "Paused • Tap to resume"
                    RunButtonState.READY -> "Ready to start"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Emergency info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Emergency: Long press the power button to call for help",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(RunIQSpacing.sm)
                )
            }
            
            Spacer(modifier = Modifier.height(RunIQSpacing.sm))
        }
    }
}

@Preview(showBackground = true, name = "Active Run Screen - Running")
@Composable
private fun ActiveRunScreenRunningPreview() {
    RunIQTheme {
        ActiveRunScreen(
            onNavigateBack = { },
            onRunComplete = { }
        )
    }
}

@Preview(showBackground = true, name = "Active Run Screen - Paused")
@Composable
private fun ActiveRunScreenPausedPreview() {
    RunIQTheme {
        var runState by remember { mutableStateOf(RunButtonState.PAUSED) }
        
        Scaffold(
            bottomBar = {
                MusicControlBar(
                    currentTrack = CurrentTrack(
                        title = "Eye of the Tiger",
                        artist = "Survivor",
                        duration = 245000L,
                        currentPosition = 123000L,
                        isPlaying = false
                    ),
                    onPlayPause = { },
                    onNext = { },
                    onPrevious = { },
                    onExpand = { },
                    isCompact = true
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(RunIQSpacing.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                StartRunButton(
                    state = runState,
                    size = StartRunButtonSize.ExtraLarge,
                    onStartRun = { runState = RunButtonState.RUNNING },
                    onPauseRun = { runState = RunButtonState.PAUSED },
                    onResumeRun = { runState = RunButtonState.RUNNING },
                    onStopRun = { }
                )
                
                Spacer(modifier = Modifier.height(RunIQSpacing.md))
                
                Text(
                    text = "Paused • Tap to resume",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Active Run Screen Dark")
@Composable
private fun ActiveRunScreenDarkPreview() {
    RunIQTheme {
        ActiveRunScreen(
            onNavigateBack = { },
            onRunComplete = { }
        )
    }
}