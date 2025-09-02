package com.runiq.domain.model

import androidx.annotation.Keep
import java.util.UUID

/**
 * Domain model representing a complete running session
 */
@Keep
data class Run(
    val sessionId: String = UUID.randomUUID().toString(),
    val userId: String,
    
    // Core timing and metrics
    val startTime: Long,
    val endTime: Long? = null,
    val distance: Float = 0f, // meters
    val duration: Long = 0L, // milliseconds
    val workoutType: WorkoutType,
    
    // Performance metrics
    val averagePace: Float = 0f, // min/km
    val bestPace: Float? = null,
    val targetPace: Float? = null,
    
    // Health metrics
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val calories: Int = 0,
    val steps: Int? = null,
    
    // Elevation data
    val elevationGain: Float? = null,
    val elevationLoss: Float? = null,
    
    // GPS track
    val gpsTrack: List<GpsTrackPoint> = emptyList(),
    
    // AI Coaching
    val coachId: String,
    val coachingMessages: List<CoachingMessage> = emptyList(),
    val coachingEffectivenessScore: Float? = null,
    
    // Metadata
    val notes: String? = null,
    val weatherCondition: WeatherCondition? = null,
    val perceivedExertion: Int? = null, // 1-10 RPE scale
    val routeName: String? = null,
    
    // Sync status
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSyncedAt: Long? = null
) {
    
    /**
     * Check if the run is currently active (not completed)
     */
    val isActive: Boolean
        get() = endTime == null
    
    /**
     * Get the current duration (if active) or total duration (if completed)
     */
    fun getCurrentDuration(): Long {
        return if (isActive) {
            System.currentTimeMillis() - startTime
        } else {
            duration
        }
    }
    
    /**
     * Calculate current pace based on distance and time
     */
    fun getCurrentPace(): Float {
        val currentDistance = distance / 1000f // Convert to km
        val currentDuration = getCurrentDuration() / 60000f // Convert to minutes
        
        return if (currentDistance > 0) {
            currentDuration / currentDistance // minutes per km
        } else {
            0f
        }
    }
    
    /**
     * Check if the run meets a minimum distance threshold
     */
    fun isValidRun(minDistanceMeters: Float = 100f): Boolean {
        return distance >= minDistanceMeters && duration > 0
    }
    
    /**
     * Get formatted duration string
     */
    fun getFormattedDuration(): String {
        val totalSeconds = getCurrentDuration() / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    /**
     * Get formatted distance string
     */
    fun getFormattedDistance(): String {
        val km = distance / 1000f
        return if (km >= 1.0f) {
            String.format("%.2f km", km)
        } else {
            String.format("%.0f m", distance)
        }
    }
    
    /**
     * Get formatted pace string
     */
    fun getFormattedPace(): String {
        val pace = if (averagePace > 0) averagePace else getCurrentPace()
        if (pace <= 0) return "--:--"
        
        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).toInt()
        return String.format("%d:%02d /km", minutes, seconds)
    }
}