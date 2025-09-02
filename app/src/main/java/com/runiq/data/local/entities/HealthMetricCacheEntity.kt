package com.runiq.data.local.entities

import androidx.annotation.Keep
import androidx.room.*
import com.runiq.domain.model.SyncStatus

/**
 * Room entity for caching health metrics from various sources
 */
@Keep
@Entity(
    tableName = "health_metric_cache",
    indices = [
        Index(value = ["sessionId", "metricType"]),
        Index(value = ["timestamp"]),
        Index(value = ["source", "syncStatus"]),
        Index(value = ["metricType", "timestamp"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = RunSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HealthMetricCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    
    @ColumnInfo(name = "metric_type")
    val metricType: HealthMetricType,
    
    @ColumnInfo(name = "value")
    val value: Double,
    
    @ColumnInfo(name = "unit")
    val unit: String, // e.g., "bpm", "m/s", "calories"
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "source")
    val source: DataSource,
    
    @ColumnInfo(name = "accuracy")
    val accuracy: Float? = null, // Confidence in the measurement
    
    @ColumnInfo(name = "device_id")
    val deviceId: String? = null, // Which device recorded this metric
    
    @ColumnInfo(name = "raw_data")
    val rawData: String? = null, // JSON for complex data structures
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    @ColumnInfo(name = "health_connect_id")
    val healthConnectId: String? = null, // Reference to Health Connect record
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Keep
enum class HealthMetricType {
    HEART_RATE,
    SPEED,
    PACE,
    CADENCE,
    POWER,
    CALORIES_BURNED,
    STEPS,
    DISTANCE,
    ELEVATION,
    TEMPERATURE,
    HYDRATION_LEVEL,
    PERCEIVED_EXERTION,
    OXYGEN_SATURATION,
    BREATHING_RATE,
    STRIDE_LENGTH,
    GROUND_CONTACT_TIME,
    VERTICAL_OSCILLATION
}

@Keep
enum class DataSource {
    PHONE_GPS,
    PHONE_SENSORS,
    HEALTH_CONNECT,
    WEARABLE_DEVICE,
    EXTERNAL_APP,
    MANUAL_ENTRY,
    AI_ESTIMATION,
    SPOTIFY_API // For music-related metrics like BPM
}