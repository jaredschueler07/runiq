package com.runiq.data.local.entities

import androidx.annotation.Keep
import androidx.room.*

/**
 * Room entity representing a GPS tracking point
 */
@Keep
@Entity(
    tableName = "gps_track_points",
    indices = [
        Index(value = ["sessionId", "timestamp"]),
        Index(value = ["sessionId", "sequenceNumber"]),
        Index(value = ["timestamp"])
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
data class GpsTrackPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    
    @ColumnInfo(name = "altitude")
    val altitude: Double? = null,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "accuracy")
    val accuracy: Float? = null,
    
    @ColumnInfo(name = "speed")
    val speed: Float? = null, // m/s
    
    @ColumnInfo(name = "bearing")
    val bearing: Float? = null, // degrees
    
    @ColumnInfo(name = "distance")
    val distance: Float = 0f, // cumulative distance from start in meters
    
    @ColumnInfo(name = "sequence_number")
    val sequenceNumber: Int, // Order of points in the track
    
    @ColumnInfo(name = "is_pause_point")
    val isPausePoint: Boolean = false,
    
    @ColumnInfo(name = "pace")
    val pace: Float? = null, // current pace in min/km
    
    @ColumnInfo(name = "heart_rate")
    val heartRate: Int? = null // BPM at this point if available
)