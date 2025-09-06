package com.runiq.data.local.entities

import androidx.annotation.Keep
import androidx.room.*
import com.runiq.domain.model.CoachingMessage
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import java.util.UUID

/**
 * Room entity representing a running session
 */
@Keep
@Entity(
    tableName = "run_sessions",
    indices = [
        Index(value = ["user_id", "start_time"]),
        Index(value = ["health_connect_id"], unique = true),
        Index(value = ["sync_status"]),
        Index(value = ["workout_type"]),
        Index(value = ["end_time"])
    ]
)
data class RunSessionEntity(
    @PrimaryKey 
    val sessionId: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Core timing and metrics
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    
    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,
    
    @ColumnInfo(name = "distance")
    val distance: Float = 0f, // meters
    
    @ColumnInfo(name = "duration")
    val duration: Long = 0L, // milliseconds
    
    @ColumnInfo(name = "workout_type")
    val workoutType: WorkoutType,
    
    // Pace and performance metrics
    @ColumnInfo(name = "average_pace")
    val averagePace: Float = 0f, // min/km
    
    @ColumnInfo(name = "best_pace")
    val bestPace: Float? = null, // min/km
    
    @ColumnInfo(name = "target_pace")
    val targetPace: Float? = null, // min/km
    
    // Health metrics
    @ColumnInfo(name = "average_heart_rate")
    val averageHeartRate: Int? = null,
    
    @ColumnInfo(name = "max_heart_rate")
    val maxHeartRate: Int? = null,
    
    @ColumnInfo(name = "calories")
    val calories: Int = 0,
    
    @ColumnInfo(name = "steps")
    val steps: Int? = null,
    
    // Elevation data
    @ColumnInfo(name = "elevation_gain")
    val elevationGain: Float? = null, // meters
    
    @ColumnInfo(name = "elevation_loss")
    val elevationLoss: Float? = null, // meters
    
    // Health Connect integration
    @ColumnInfo(name = "health_connect_id")
    val healthConnectId: String? = null,
    
    // AI Coaching
    @ColumnInfo(name = "coach_id")
    val coachId: String,
    
    @ColumnInfo(name = "coaching_messages")
    val coachingMessages: List<CoachingMessage> = emptyList(), // JSON via TypeConverter
    
    @ColumnInfo(name = "coaching_effectiveness_score")
    val coachingEffectivenessScore: Float? = null, // 0.0 to 1.0
    
    // Cloud storage
    @ColumnInfo(name = "gps_track_url")
    val gpsTrackUrl: String? = null, // Firestore/Cloud Storage URL
    
    @ColumnInfo(name = "heart_rate_data_url")
    val heartRateDataUrl: String? = null,
    
    // Sync management
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null,
    
    @ColumnInfo(name = "sync_error_message")
    val syncErrorMessage: String? = null,
    
    // Metadata
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "weather_condition")
    val weatherCondition: String? = null,
    
    @ColumnInfo(name = "perceived_exertion")
    val perceivedExertion: Int? = null, // 1-10 RPE scale
    
    @ColumnInfo(name = "route_name")
    val routeName: String? = null
)