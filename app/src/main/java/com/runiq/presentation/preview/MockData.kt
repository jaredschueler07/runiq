package com.runiq.presentation.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.ui.graphics.Color
import com.runiq.presentation.components.cards.CoachPersonality
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.charts.PaceDataPoint
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.lists.RunHistoryEntry
import com.runiq.presentation.components.music.CurrentTrack
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Mock data provider for UI previews and development
 * Contains realistic sample data for all components and screens
 */
object MockData {
    
    // MARK: - Run Metrics
    
    val sampleRunMetrics = RunMetrics(
        pace = "5:24",
        distance = "5.42",
        duration = "29:18",
        heartRate = 145,
        calories = 387
    )
    
    val compactRunMetrics = RunMetrics(
        pace = "5:45",
        distance = "3.21",
        duration = "18:32",
        heartRate = 152
    )
    
    val emptyRunMetrics = RunMetrics()
    
    // MARK: - Coach Personalities
    
    val availableCoaches = listOf(
        CoachPersonality(
            id = "alex",
            name = "Alex",
            description = "High-energy motivational coach who pushes you to achieve your personal best. Great for building confidence and breaking through barriers.",
            personality = "Motivational & Energetic",
            avatarIcon = Icons.Default.SportsScore,
            color = Color(0xFF4CAF50)
        ),
        CoachPersonality(
            id = "sam",
            name = "Sam",
            description = "Calm and supportive coach focused on mindful running and sustainable progress. Perfect for stress relief and building healthy habits.",
            personality = "Supportive & Mindful",
            avatarIcon = Icons.Default.Favorite,
            color = Color(0xFF2196F3)
        ),
        CoachPersonality(
            id = "taylor",
            name = "Taylor",
            description = "Data-driven analytical coach who focuses on metrics, pacing strategies, and performance optimization for serious runners.",
            personality = "Analytical & Strategic",
            avatarIcon = Icons.Default.Analytics,
            color = Color(0xFF9C27B0)
        ),
        CoachPersonality(
            id = "riley",
            name = "Riley",
            description = "Balanced coach combining motivation with technical guidance. Adapts coaching style based on your mood and performance.",
            personality = "Adaptive & Balanced",
            avatarIcon = Icons.Default.Psychology,
            color = Color(0xFFFF9800)
        )
    )
    
    val currentCoach = availableCoaches.first()
    
    // MARK: - Run History
    
    val sampleRunHistory = listOf(
        RunHistoryEntry(
            id = "1",
            date = LocalDateTime.now().minusDays(1),
            distance = 5.42,
            duration = "29:18",
            pace = "5:24",
            calories = 387,
            workoutType = "Morning Run"
        ),
        RunHistoryEntry(
            id = "2",
            date = LocalDateTime.now().minusDays(2),
            distance = 3.21,
            duration = "18:32",
            pace = "5:47",
            calories = 198,
            workoutType = "Recovery Run"
        ),
        RunHistoryEntry(
            id = "3",
            date = LocalDateTime.now().minusDays(4),
            distance = 8.15,
            duration = "42:18",
            pace = "5:11",
            calories = 567,
            workoutType = "Long Run"
        ),
        RunHistoryEntry(
            id = "4",
            date = LocalDateTime.now().minusDays(6),
            distance = 2.34,
            duration = "12:45",
            pace = "5:32",
            calories = 156,
            workoutType = "Easy Run"
        ),
        RunHistoryEntry(
            id = "5",
            date = LocalDateTime.now().minusDays(8),
            distance = 6.42,
            duration = "32:18",
            pace = "5:03",
            calories = 423,
            workoutType = "Tempo Run"
        ),
        RunHistoryEntry(
            id = "6",
            date = LocalDateTime.now().minusDays(10),
            distance = 4.73,
            duration = "24:12",
            pace = "5:09",
            calories = 287,
            workoutType = "Easy Run"
        ),
        RunHistoryEntry(
            id = "7",
            date = LocalDateTime.now().minusDays(12),
            distance = 10.5,
            duration = "54:32",
            pace = "5:12",
            calories = 678,
            workoutType = "Long Run"
        ),
        RunHistoryEntry(
            id = "8",
            date = LocalDateTime.now().minusDays(15),
            distance = 3.8,
            duration = "19:45",
            pace = "5:18",
            calories = 234,
            workoutType = "Recovery Run"
        )
    )
    
    val recentRuns = sampleRunHistory.take(3)
    
    // MARK: - Pace Data
    
    val samplePaceData = listOf(
        PaceDataPoint(0f, 5.8f),
        PaceDataPoint(5f, 5.6f),
        PaceDataPoint(10f, 5.3f),
        PaceDataPoint(15f, 5.2f),
        PaceDataPoint(20f, 5.1f),
        PaceDataPoint(25f, 5.0f),
        PaceDataPoint(29f, 4.9f)
    )
    
    val steadyPaceData = listOf(
        PaceDataPoint(0f, 5.5f),
        PaceDataPoint(10f, 5.5f),
        PaceDataPoint(20f, 5.5f),
        PaceDataPoint(30f, 5.5f)
    )
    
    val variablePaceData = listOf(
        PaceDataPoint(0f, 6.2f),
        PaceDataPoint(3f, 5.8f),
        PaceDataPoint(7f, 5.3f),
        PaceDataPoint(12f, 5.7f),
        PaceDataPoint(18f, 5.1f),
        PaceDataPoint(23f, 5.4f),
        PaceDataPoint(28f, 5.0f)
    )
    
    // MARK: - Stats Rings
    
    val weeklyGoalRings = listOf(
        StatsRingData(
            value = 15.2f,
            maxValue = 20f,
            label = "Distance",
            color = Color(0xFF4CAF50),
            unit = "km"
        ),
        StatsRingData(
            value = 3f,
            maxValue = 4f,
            label = "Runs",
            color = Color(0xFF2196F3)
        ),
        StatsRingData(
            value = 180f,
            maxValue = 240f,
            label = "Minutes",
            color = Color(0xFF9C27B0),
            unit = "min"
        )
    )
    
    val monthlyStatsRings = listOf(
        StatsRingData(
            value = 12f,
            maxValue = 15f,
            label = "Runs",
            color = Color(0xFF4CAF50)
        ),
        StatsRingData(
            value = 67.3f,
            maxValue = 80f,
            label = "Distance",
            color = Color(0xFF2196F3),
            unit = "km"
        ),
        StatsRingData(
            value = 342f,
            maxValue = 400f,
            label = "Minutes",
            color = Color(0xFF9C27B0),
            unit = "min"
        )
    )
    
    val performanceRings = listOf(
        StatsRingData(
            value = 85f,
            maxValue = 100f,
            label = "Effort",
            color = Color(0xFF4CAF50),
            unit = "%"
        ),
        StatsRingData(
            value = 92f,
            maxValue = 100f,
            label = "Consistency",
            color = Color(0xFF2196F3),
            unit = "%"
        ),
        StatsRingData(
            value = 78f,
            maxValue = 100f,
            label = "Efficiency",
            color = Color(0xFF9C27B0),
            unit = "%"
        )
    )
    
    // MARK: - Music Tracks
    
    val sampleMusicTracks = listOf(
        CurrentTrack(
            title = "Thunder",
            artist = "Imagine Dragons",
            duration = 187000L,
            currentPosition = 89000L,
            isPlaying = true
        ),
        CurrentTrack(
            title = "Can't Hold Us",
            artist = "Macklemore & Ryan Lewis",
            duration = 257000L,
            currentPosition = 123000L,
            isPlaying = false
        ),
        CurrentTrack(
            title = "Stronger",
            artist = "Kelly Clarkson",
            duration = 222000L,
            currentPosition = 67000L,
            isPlaying = true
        ),
        CurrentTrack(
            title = "Eye of the Tiger",
            artist = "Survivor",
            duration = 245000L,
            currentPosition = 98000L,
            isPlaying = true
        ),
        CurrentTrack(
            title = "Uptown Funk",
            artist = "Mark Ronson ft. Bruno Mars",
            duration = 270000L,
            currentPosition = 145000L,
            isPlaying = false
        )
    )
    
    val currentTrack = sampleMusicTracks.first()
    
    // MARK: - Coaching Messages
    
    val coachingMessages = listOf(
        "Great pace! Keep this rhythm going. You're doing amazing!",
        "Nice and steady. Focus on your breathing and maintain this effort.",
        "You're crushing it! This is exactly the pace we want for this workout.",
        "Feeling strong today! Let's pick up the pace just a little bit.",
        "Perfect form! Remember to keep your shoulders relaxed.",
        "You're halfway through - keep pushing, you've got this!",
        "Outstanding effort! Your consistency is really paying off.",
        "Time to dig deep! Show me what you're made of!",
        "Smooth and controlled. This is textbook running right here.",
        "Final push! Give me everything you've got left!"
    )
    
    fun randomCoachingMessage(): String = coachingMessages.random()
    
    // MARK: - Helper Functions
    
    /**
     * Generate random run metrics for testing
     */
    fun generateRandomRunMetrics(): RunMetrics {
        return RunMetrics(
            pace = "${Random.nextInt(4, 7)}:${Random.nextInt(10, 60).toString().padStart(2, '0')}",
            distance = String.format("%.2f", Random.nextDouble(1.0, 15.0)),
            duration = "${Random.nextInt(10, 90)}:${Random.nextInt(10, 59).toString().padStart(2, '0')}",
            heartRate = if (Random.nextBoolean()) Random.nextInt(120, 180) else null,
            calories = if (Random.nextBoolean()) Random.nextInt(100, 800) else null
        )
    }
    
    /**
     * Generate random pace data points
     */
    fun generateRandomPaceData(points: Int = 10): List<PaceDataPoint> {
        val baseTime = Random.nextFloat() * 30f
        val basePace = Random.nextFloat() * 2f + 4.5f
        
        return (0 until points).map { i ->
            PaceDataPoint(
                timeMinutes = baseTime + (i * 3f),
                paceMinutesPerKm = basePace + Random.nextFloat() * 1f - 0.5f
            )
        }
    }
    
    /**
     * Generate random stats ring data
     */
    fun generateRandomStatsRings(count: Int = 3): List<StatsRingData> {
        val colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFF9C27B0),
            Color(0xFFFF9800),
            Color(0xFFF44336)
        )
        val labels = listOf("Distance", "Runs", "Time", "Calories", "Pace", "Heart Rate")
        val units = listOf("km", "", "min", "cal", "/km", "bpm")
        
        return (0 until count).map { i ->
            val maxValue = Random.nextFloat() * 100f + 50f
            StatsRingData(
                value = Random.nextFloat() * maxValue,
                maxValue = maxValue,
                label = labels[i % labels.size],
                color = colors[i % colors.size],
                unit = units[i % units.size]
            )
        }
    }
}