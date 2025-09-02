package com.runiq.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.annotation.Keep
import java.util.UUID

/**
 * Run session entity representing a single running workout
 */
@Keep
@Entity(
    tableName = "run_sessions",
    indices = [
        Index(value = ["userId", "startTime"]),
        Index(value = ["healthConnectId"], unique = true),
        Index(value = ["syncStatus"]),
        Index(value = ["workoutType"]),
        Index(value = ["endTime"])
    ]
)
data class RunSession(
    @PrimaryKey 
    val sessionId: String = UUID.randomUUID().toString(),
    
    val userId: String,
    
    // Core timing
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long = 0L, // milliseconds
    
    // Distance and pace
    val distance: Float = 0f, // meters
    val averagePace: Float = 0f, // min/km
    val bestPace: Float = 0f, // min/km
    
    // Health metrics
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val calories: Int = 0,
    val steps: Int? = null,
    
    // Workout details
    @ColumnInfo(name = "workout_type")
    val workoutType: String, // WorkoutType enum as string
    val targetDistance: Float? = null,
    val targetDuration: Long? = null,
    val targetPace: Float? = null,
    
    // GPS and location
    val startLatitude: Double? = null,
    val startLongitude: Double? = null,
    val endLatitude: Double? = null,
    val endLongitude: Double? = null,
    val elevationGain: Float = 0f, // meters
    val elevationLoss: Float = 0f, // meters
    
    // AI Coaching
    val coachId: String,
    val coachingMessagesCount: Int = 0,
    val aiAnalysisCompleted: Boolean = false,
    
    // Music integration
    val spotifyPlaylistId: String? = null,
    val musicGenre: String? = null,
    val targetBpm: Int? = null,
    
    // Health Connect integration
    val healthConnectId: String? = null,
    val healthConnectSynced: Boolean = false,
    
    // Cloud sync
    val firestoreId: String? = null,
    val gpsTrackUrl: String? = null, // Cloud storage URL for compressed GPS data
    val syncStatus: String = SyncStatus.PENDING.name, // SyncStatus enum as string
    val lastSyncedAt: Long? = null,
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val weather: String? = null, // JSON string of weather data
    val tags: List<String> = emptyList() // Converted via TypeConverter
) {
    enum class SyncStatus {
        PENDING,
        SYNCING,
        SYNCED,
        FAILED
    }
}

/**
 * GPS track point entity for detailed route tracking
 */
@Keep
@Entity(
    tableName = "gps_track_points",
    indices = [
        Index(value = ["sessionId", "timestamp"]),
        Index(value = ["sessionId"])
    ]
)
data class GpsTrackPoint(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val sessionId: String,
    val timestamp: Long,
    
    // Location data
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val bearing: Float? = null,
    val speed: Float? = null, // m/s
    
    // Calculated metrics
    val distanceFromStart: Float = 0f, // meters
    val currentPace: Float? = null, // min/km
    val heartRate: Int? = null,
    
    // Quality indicators
    val isAccurate: Boolean = true,
    val isPaused: Boolean = false
)

/**
 * Coach entity representing different AI coaching personalities
 */
@Keep
@Entity(
    tableName = "coaches",
    indices = [
        Index(value = ["isActive"]),
        Index(value = ["category"])
    ]
)
data class Coach(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val description: String,
    val category: String, // MOTIVATIONAL, TECHNICAL, FRIENDLY, etc.
    val personality: String, // JSON string of personality traits
    
    // Voice characteristics
    val voiceId: String? = null, // ElevenLabs voice ID
    val voiceStability: Float = 0.5f,
    val voiceSimilarity: Float = 0.75f,
    val voiceStyle: Float = 0.0f,
    
    // Coaching style
    val coachingStyle: String, // JSON string of coaching preferences
    val specialties: List<String> = emptyList(), // Areas of expertise
    val experienceLevel: String, // BEGINNER, INTERMEDIATE, ADVANCED
    
    // Availability
    val isActive: Boolean = true,
    val isPremium: Boolean = false,
    val isCustom: Boolean = false,
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val avatarUrl: String? = null
)

/**
 * Coach text line entity for rule-based coaching messages
 */
@Keep
@Entity(
    tableName = "coach_text_lines",
    indices = [
        Index(value = ["coachId", "category"]),
        Index(value = ["category", "conditions"]),
        Index(value = ["isActive"])
    ]
)
data class CoachTextLine(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val coachId: String,
    val category: String, // MOTIVATION, PACE_FEEDBACK, MILESTONE, etc.
    val text: String,
    val conditions: String, // JSON string of when to use this line
    
    // Usage tracking
    val priority: Int = 0, // Higher priority = more likely to be selected
    val usageCount: Int = 0,
    val lastUsed: Long? = null,
    
    // Metadata
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)

/**
 * Health metric cache entity for storing aggregated health data
 */
@Keep
@Entity(
    tableName = "health_metric_cache",
    indices = [
        Index(value = ["userId", "date"]),
        Index(value = ["metricType", "date"]),
        Index(value = ["lastUpdated"])
    ]
)
data class HealthMetricCache(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val userId: String,
    val date: String, // YYYY-MM-DD format
    val metricType: String, // STEPS, HEART_RATE, SLEEP, etc.
    
    // Metric data
    val value: Float,
    val unit: String,
    val source: String, // HEALTH_CONNECT, MANUAL, CALCULATED
    val confidence: Float = 1.0f, // 0.0 to 1.0
    
    // Aggregation info
    val aggregationType: String, // DAILY, WEEKLY, MONTHLY
    val dataPoints: Int = 1, // Number of raw data points used
    val minValue: Float? = null,
    val maxValue: Float? = null,
    
    // Sync info
    val healthConnectId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val syncedToCloud: Boolean = false
)