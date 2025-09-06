package com.runiq.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Domain model representing a running session.
 * This is the core entity for tracking user runs.
 */
@Entity(
    tableName = "run_sessions",
    indices = [
        Index(value = ["userId", "startTime"]),
        Index(value = ["healthConnectId"], unique = true),
        Index(value = ["syncStatus"])
    ]
)
@JsonClass(generateAdapter = true)
data class RunSession(
    @PrimaryKey 
    val sessionId: String = UUID.randomUUID().toString(),
    
    val userId: String,
    
    // Core metrics
    val startTime: Long,
    val endTime: Long? = null,
    val distance: Float = 0f, // meters
    val duration: Long = 0L, // milliseconds
    
    // Workout details
    val workoutType: WorkoutType,
    
    // Health Connect integration
    val healthConnectId: String? = null,
    val averagePace: Float = 0f, // min/km
    val averageHeartRate: Int? = null,
    val calories: Int = 0,
    
    // AI Coaching
    val coachId: String,
    @ColumnInfo(name = "coaching_messages")
    val coachingMessages: List<CoachingMessage> = emptyList(),
    
    // GPS tracking
    val gpsTrackUrl: String? = null, // Cloud storage URL for compressed GPS data
    
    // Sync management
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val lastSyncedAt: Long? = null
) {
    enum class SyncStatus {
        @Json(name = "pending") PENDING,
        @Json(name = "syncing") SYNCING,
        @Json(name = "synced") SYNCED,
        @Json(name = "failed") FAILED
    }
    
    /**
     * Calculates if the session is currently active (not ended).
     */
    val isActive: Boolean
        get() = endTime == null
    
    /**
     * Calculates the current duration of the session.
     */
    val currentDuration: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime
    
    /**
     * Calculates average speed in m/s.
     */
    val averageSpeed: Float
        get() = if (duration > 0) distance / (duration / 1000f) else 0f
}