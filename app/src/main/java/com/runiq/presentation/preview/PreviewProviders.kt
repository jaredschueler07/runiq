package com.runiq.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.runiq.presentation.components.buttons.RunButtonState
import com.runiq.presentation.components.cards.CoachPersonality
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.charts.PaceDataPoint
import com.runiq.presentation.components.lists.RunHistoryEntry
import com.runiq.presentation.components.music.CurrentTrack

/**
 * Preview parameter providers for Compose previews
 * Enables testing components with different data states
 */

/**
 * Provides different run metrics states for component previews
 */
class RunMetricsPreviewProvider : PreviewParameterProvider<RunMetrics> {
    override val values = sequenceOf(
        MockData.sampleRunMetrics,
        MockData.compactRunMetrics,
        MockData.emptyRunMetrics,
        MockData.generateRandomRunMetrics(),
        RunMetrics(
            pace = "4:30",
            distance = "21.10",
            duration = "95:12",
            heartRate = 165,
            calories = 1247
        ), // Marathon-like run
        RunMetrics(
            pace = "7:15",
            distance = "1.00",
            duration = "07:15",
            heartRate = 130
        ) // Short easy run
    )
}

/**
 * Provides different run button states for component previews
 */
class RunButtonStatePreviewProvider : PreviewParameterProvider<RunButtonState> {
    override val values = sequenceOf(
        RunButtonState.READY,
        RunButtonState.RUNNING,
        RunButtonState.PAUSED
    )
}

/**
 * Provides different coach personalities for component previews
 */
class CoachPersonalityPreviewProvider : PreviewParameterProvider<CoachPersonality> {
    override val values = sequenceOf(
        MockData.availableCoaches[0], // Alex - Motivational
        MockData.availableCoaches[1], // Sam - Supportive
        MockData.availableCoaches[2], // Taylor - Analytical
        MockData.availableCoaches[3]  // Riley - Balanced
    )
}

/**
 * Provides different run history entries for component previews
 */
class RunHistoryPreviewProvider : PreviewParameterProvider<RunHistoryEntry> {
    override val values = sequenceOf(
        MockData.sampleRunHistory[0], // Recent morning run
        MockData.sampleRunHistory[2], // Long run
        MockData.sampleRunHistory[4], // Tempo run
        RunHistoryEntry(
            id = "short",
            date = java.time.LocalDateTime.now().minusHours(2),
            distance = 0.8,
            duration = "4:32",
            pace = "5:40",
            workoutType = "Warm-up"
        ), // Very short run
        RunHistoryEntry(
            id = "ultra",
            date = java.time.LocalDateTime.now().minusDays(7),
            distance = 42.2,
            duration = "195:45",
            pace = "4:38",
            calories = 2847,
            workoutType = "Marathon"
        ) // Marathon distance
    )
}

/**
 * Provides different music tracks for component previews
 */
class MusicTrackPreviewProvider : PreviewParameterProvider<CurrentTrack?> {
    override val values = sequenceOf(
        null, // No track playing
        MockData.sampleMusicTracks[0], // Playing track
        MockData.sampleMusicTracks[1], // Paused track
        CurrentTrack(
            title = "This Is a Very Long Song Title That Should Be Truncated Properly",
            artist = "An Artist With a Really Long Name That Also Gets Cut Off",
            duration = 345000L,
            currentPosition = 67000L,
            isPlaying = true
        ), // Long names
        CurrentTrack(
            title = "Short",
            artist = "Artist",
            duration = 120000L,
            currentPosition = 0L,
            isPlaying = false
        ) // Just started
    )
}

/**
 * Provides different pace data sets for chart previews
 */
class PaceDataPreviewProvider : PreviewParameterProvider<List<PaceDataPoint>> {
    override val values = sequenceOf(
        emptyList(), // No data
        listOf(PaceDataPoint(0f, 5.5f)), // Single point
        MockData.steadyPaceData, // Consistent pace
        MockData.samplePaceData, // Negative split
        MockData.variablePaceData, // Variable pace
        MockData.generateRandomPaceData(15) // Random longer run
    )
}

/**
 * Provides different loading/error states for screens
 */
class ScreenStatePreviewProvider : PreviewParameterProvider<ScreenState> {
    override val values = sequenceOf(
        ScreenState.LOADING,
        ScreenState.SUCCESS,
        ScreenState.ERROR,
        ScreenState.EMPTY
    )
}

/**
 * Screen state enum for preview purposes
 */
enum class ScreenState {
    LOADING,
    SUCCESS,
    ERROR,
    EMPTY
}

/**
 * Provides boolean values for toggle states
 */
class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

/**
 * Provides different list sizes for testing component behavior
 */
class ListSizePreviewProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(0, 1, 3, 5, 10, 50)
}

/**
 * Provides different permission states for onboarding screens
 */
class PermissionStatePreviewProvider : PreviewParameterProvider<PermissionState> {
    override val values = sequenceOf(
        PermissionState.ALL_GRANTED,
        PermissionState.PARTIAL_GRANTED,
        PermissionState.NONE_GRANTED
    )
}

enum class PermissionState {
    ALL_GRANTED,
    PARTIAL_GRANTED,
    NONE_GRANTED
}

/**
 * Provides different connectivity states for music integration
 */
class ConnectivityStatePreviewProvider : PreviewParameterProvider<ConnectivityState> {
    override val values = sequenceOf(
        ConnectivityState.CONNECTED,
        ConnectivityState.DISCONNECTED,
        ConnectivityState.CONNECTING,
        ConnectivityState.ERROR
    )
}

enum class ConnectivityState {
    CONNECTED,
    DISCONNECTED,
    CONNECTING,
    ERROR
}

/**
 * Utility object for creating preview configurations
 */
object PreviewConfigurations {
    
    /**
     * Create a complete run session for detailed previews
     */
    fun createCompleteRunSession() = RunSessionPreview(
        metrics = MockData.sampleRunMetrics,
        paceData = MockData.samplePaceData,
        coachingMessage = MockData.randomCoachingMessage(),
        currentTrack = MockData.currentTrack,
        isActive = true
    )
    
    /**
     * Create an empty state for testing empty scenarios
     */
    fun createEmptyState() = RunSessionPreview(
        metrics = MockData.emptyRunMetrics,
        paceData = emptyList(),
        coachingMessage = "",
        currentTrack = null,
        isActive = false
    )
    
    /**
     * Create a long run session for testing edge cases
     */
    fun createLongRunSession() = RunSessionPreview(
        metrics = RunMetrics(
            pace = "5:12",
            distance = "21.10",
            duration = "110:15",
            heartRate = 155,
            calories = 1456
        ),
        paceData = MockData.generateRandomPaceData(25),
        coachingMessage = "Incredible endurance! You're running like a champion today!",
        currentTrack = MockData.sampleMusicTracks[2],
        isActive = true
    )
}

/**
 * Data class for comprehensive run session previews
 */
data class RunSessionPreview(
    val metrics: RunMetrics,
    val paceData: List<PaceDataPoint>,
    val coachingMessage: String,
    val currentTrack: CurrentTrack?,
    val isActive: Boolean
)