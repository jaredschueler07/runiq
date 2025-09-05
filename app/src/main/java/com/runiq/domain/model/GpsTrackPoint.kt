package com.runiq.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Domain model representing a GPS track point during a run.
 * Contains location, speed, and accuracy data.
 */
@Entity(
    tableName = "gps_track_points",
    indices = [
        Index(value = ["sessionId", "timestamp"]),
        Index(value = ["sessionId"])
    ]
)
@JsonClass(generateAdapter = true)
data class GpsTrackPoint(
    @PrimaryKey 
    val id: String = UUID.randomUUID().toString(),
    
    val sessionId: String,
    val timestamp: Long,
    
    // Location data
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0, // meters above sea level
    
    // Accuracy and movement data
    val accuracy: Float, // meters
    val speed: Float = 0f, // m/s
    val bearing: Float = 0f, // degrees from north
    
    // Calculated fields
    val distanceFromStart: Float = 0f, // meters
    val elevationGain: Float = 0f // meters gained since last point
) {
    
    /**
     * Calculates distance to another GPS point using Haversine formula.
     */
    fun distanceTo(other: GpsTrackPoint): Float {
        val earthRadius = 6371000f // Earth's radius in meters
        
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val deltaLatRad = Math.toRadians(other.latitude - latitude)
        val deltaLonRad = Math.toRadians(other.longitude - longitude)
        
        val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
                kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                kotlin.math.sin(deltaLonRad / 2) * kotlin.math.sin(deltaLonRad / 2)
        
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return (earthRadius * c).toFloat()
    }
    
    /**
     * Calculates bearing to another GPS point.
     */
    fun bearingTo(other: GpsTrackPoint): Float {
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val deltaLonRad = Math.toRadians(other.longitude - longitude)
        
        val y = kotlin.math.sin(deltaLonRad) * kotlin.math.cos(lat2Rad)
        val x = kotlin.math.cos(lat1Rad) * kotlin.math.sin(lat2Rad) -
                kotlin.math.sin(lat1Rad) * kotlin.math.cos(lat2Rad) * kotlin.math.cos(deltaLonRad)
        
        val bearingRad = kotlin.math.atan2(y, x)
        return ((Math.toDegrees(bearingRad) + 360) % 360).toFloat()
    }
}