package com.runiq.presentation.components.music

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Data class representing currently playing track
 */
data class CurrentTrack(
    val title: String,
    val artist: String,
    val albumArtUrl: String? = null,
    val duration: Long = 0L, // in milliseconds
    val currentPosition: Long = 0L, // in milliseconds
    val isPlaying: Boolean = false
)

/**
 * Mini music player control bar
 * Displayed at the bottom of screens during active runs
 */
@Composable
fun MusicControlBar(
    currentTrack: CurrentTrack?,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    if (currentTrack == null) return
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpand() },
        elevation = CardDefaults.cardElevation(defaultElevation = RunIQElevation.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(
            topStart = if (isCompact) 0.dp else 12.dp,
            topEnd = if (isCompact) 0.dp else 12.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Column {
            // Progress indicator
            if (currentTrack.duration > 0) {
                val progress = (currentTrack.currentPosition.toFloat() / currentTrack.duration.toFloat())
                    .coerceIn(0f, 1f)
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(RunIQSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Album art placeholder and track info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Album art placeholder
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 40.dp else 48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Album art",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(RunIQSpacing.sm))
                    
                    // Track info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentTrack.title,
                            style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = currentTrack.artist,
                            style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Control buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isCompact) {
                        IconButton(
                            onClick = onPrevious,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous track",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    // Play/Pause button
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(if (isCompact) 40.dp else 48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (currentTrack.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (currentTrack.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                        )
                    }
                    
                    if (!isCompact) {
                        IconButton(
                            onClick = onNext,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next track",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Playing Track")
@Composable
private fun MusicControlBarPlayingPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = CurrentTrack(
                title = "Thunder",
                artist = "Imagine Dragons",
                duration = 187000L,
                currentPosition = 45000L,
                isPlaying = true
            ),
            onPlayPause = { },
            onNext = { },
            onPrevious = { },
            onExpand = { }
        )
    }
}

@Preview(showBackground = true, name = "Paused Track")
@Composable
private fun MusicControlBarPausedPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = CurrentTrack(
                title = "Can't Hold Us",
                artist = "Macklemore & Ryan Lewis",
                duration = 257000L,
                currentPosition = 123000L,
                isPlaying = false
            ),
            onPlayPause = { },
            onNext = { },
            onPrevious = { },
            onExpand = { }
        )
    }
}

@Preview(showBackground = true, name = "Long Track Names")
@Composable
private fun MusicControlBarLongNamesPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = CurrentTrack(
                title = "This Is a Very Long Song Title That Should Be Truncated",
                artist = "An Artist With a Really Long Name That Also Gets Cut Off",
                duration = 345000L,
                currentPosition = 67000L,
                isPlaying = true
            ),
            onPlayPause = { },
            onNext = { },
            onPrevious = { },
            onExpand = { }
        )
    }
}

@Preview(showBackground = true, name = "Compact Mode")
@Composable
private fun MusicControlBarCompactPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = CurrentTrack(
                title = "Stronger",
                artist = "Kelly Clarkson",
                duration = 222000L,
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
}

@Preview(showBackground = true, name = "No Track")
@Composable
private fun MusicControlBarNoTrackPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = null,
            onPlayPause = { },
            onNext = { },
            onPrevious = { },
            onExpand = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun MusicControlBarDarkPreview() {
    RunIQTheme {
        MusicControlBar(
            currentTrack = CurrentTrack(
                title = "Eye of the Tiger",
                artist = "Survivor",
                duration = 245000L,
                currentPosition = 98000L,
                isPlaying = true
            ),
            onPlayPause = { },
            onNext = { },
            onPrevious = { },
            onExpand = { }
        )
    }
}