package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.GpsTrackPointEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for GPS tracking points with efficient query operations
 */
@Dao
interface GpsTrackDao {
    
    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: GpsTrackPointEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<GpsTrackPointEntity>): List<Long>
    
    @Update
    suspend fun update(point: GpsTrackPointEntity): Int
    
    @Delete
    suspend fun delete(point: GpsTrackPointEntity): Int
    
    @Query("DELETE FROM gps_track_points WHERE session_id = :sessionId")
    suspend fun deleteBySession(sessionId: String): Int
    
    // Basic queries
    @Query("SELECT * FROM gps_track_points WHERE id = :id")
    suspend fun getById(id: Long): GpsTrackPointEntity?
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC
    """)
    suspend fun getBySession(sessionId: String): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC
    """)
    fun observeBySession(sessionId: String): Flow<List<GpsTrackPointEntity>>
    
    // Pagination for large tracks
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getBySessionPaged(sessionId: String, limit: Int, offset: Int): List<GpsTrackPointEntity>
    
    // Time-based queries
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND timestamp BETWEEN :startTime AND :endTime 
        ORDER BY timestamp ASC
    """)
    suspend fun getByTimeRange(
        sessionId: String, 
        startTime: Long, 
        endTime: Long
    ): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND timestamp >= :sinceTimestamp 
        ORDER BY timestamp ASC
    """)
    suspend fun getRecentPoints(sessionId: String, sinceTimestamp: Long): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND timestamp >= :sinceTimestamp 
        ORDER BY timestamp ASC
    """)
    fun observeRecentPoints(sessionId: String, sinceTimestamp: Long): Flow<List<GpsTrackPointEntity>>
    
    // Distance-based queries
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND distance BETWEEN :startDistance AND :endDistance 
        ORDER BY sequence_number ASC
    """)
    suspend fun getByDistanceRange(
        sessionId: String, 
        startDistance: Float, 
        endDistance: Float
    ): List<GpsTrackPointEntity>
    
    // Sampling and optimization queries
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND sequence_number % :interval = 0 
        ORDER BY sequence_number ASC
    """)
    suspend fun getSampledPoints(sessionId: String, interval: Int): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND accuracy IS NOT NULL 
        AND accuracy <= :maxAccuracy 
        ORDER BY sequence_number ASC
    """)
    suspend fun getAccuratePoints(sessionId: String, maxAccuracy: Float): List<GpsTrackPointEntity>
    
    // Pause point queries
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND is_pause_point = 1 
        ORDER BY timestamp ASC
    """)
    suspend fun getPausePoints(sessionId: String): List<GpsTrackPointEntity>
    
    @Query("UPDATE gps_track_points SET is_pause_point = :isPause WHERE id = :id")
    suspend fun updatePauseStatus(id: Long, isPause: Boolean): Int
    
    // Statistics and analytics
    @Query("SELECT COUNT(*) FROM gps_track_points WHERE session_id = :sessionId")
    suspend fun getPointCount(sessionId: String): Int
    
    @Query("""
        SELECT MAX(distance) FROM gps_track_points 
        WHERE session_id = :sessionId
    """)
    suspend fun getTotalDistance(sessionId: String): Float?
    
    @Query("""
        SELECT MIN(timestamp) as start_time, MAX(timestamp) as end_time 
        FROM gps_track_points 
        WHERE session_id = :sessionId
    """)
    suspend fun getTimeRange(sessionId: String): TimeRange?
    
    @Query("""
        SELECT AVG(speed) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND speed IS NOT NULL 
        AND speed > 0
    """)
    suspend fun getAverageSpeed(sessionId: String): Float?
    
    @Query("""
        SELECT MAX(speed) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND speed IS NOT NULL
    """)
    suspend fun getMaxSpeed(sessionId: String): Float?
    
    @Query("""
        SELECT AVG(pace) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND pace IS NOT NULL 
        AND pace > 0
    """)
    suspend fun getAveragePace(sessionId: String): Float?
    
    @Query("""
        SELECT MIN(pace) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND pace IS NOT NULL 
        AND pace > 0
    """)
    suspend fun getBestPace(sessionId: String): Float?
    
    // Elevation queries
    @Query("""
        SELECT MIN(altitude) as min_altitude, MAX(altitude) as max_altitude 
        FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND altitude IS NOT NULL
    """)
    suspend fun getElevationRange(sessionId: String): ElevationRange?
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND altitude IS NOT NULL 
        ORDER BY sequence_number ASC
    """)
    suspend fun getPointsWithElevation(sessionId: String): List<GpsTrackPointEntity>
    
    // Heart rate queries (if available in GPS points)
    @Query("""
        SELECT AVG(heart_rate) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND heart_rate IS NOT NULL 
        AND heart_rate > 0
    """)
    suspend fun getAverageHeartRate(sessionId: String): Float?
    
    @Query("""
        SELECT MIN(heart_rate) as min_hr, MAX(heart_rate) as max_hr 
        FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND heart_rate IS NOT NULL
    """)
    suspend fun getHeartRateRange(sessionId: String): HeartRateRange?
    
    // Track analysis queries
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND sequence_number BETWEEN :startSequence AND :endSequence 
        ORDER BY sequence_number ASC
    """)
    suspend fun getSegment(
        sessionId: String, 
        startSequence: Int, 
        endSequence: Int
    ): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number DESC 
        LIMIT 1
    """)
    suspend fun getLastPoint(sessionId: String): GpsTrackPointEntity?
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC 
        LIMIT 1
    """)
    suspend fun getFirstPoint(sessionId: String): GpsTrackPointEntity?
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number DESC 
        LIMIT :count
    """)
    suspend fun getLastNPoints(sessionId: String, count: Int): List<GpsTrackPointEntity>
    
    // Batch operations
    @Transaction
    suspend fun replaceTrackPoints(sessionId: String, points: List<GpsTrackPointEntity>) {
        deleteBySession(sessionId)
        insertAll(points)
    }
    
    @Transaction
    suspend fun addPointsToTrack(points: List<GpsTrackPointEntity>) {
        insertAll(points)
    }
    
    // Cleanup operations
    @Query("DELETE FROM gps_track_points WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("""
        DELETE FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND accuracy IS NOT NULL 
        AND accuracy > :maxAccuracy
    """)
    suspend fun deleteInaccuratePoints(sessionId: String, maxAccuracy: Float): Int
    
    // Optimization queries for large datasets
    @Query("""
        SELECT id FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND sequence_number % :interval != 0 
        AND accuracy > :maxAccuracy
    """)
    suspend fun getRedundantPointIds(sessionId: String, interval: Int, maxAccuracy: Float): List<Long>
    
    @Query("DELETE FROM gps_track_points WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>): Int
}

// Data classes for complex query results
data class TimeRange(
    val startTime: Long,
    val endTime: Long
)

data class ElevationRange(
    val minAltitude: Double,
    val maxAltitude: Double
)

data class HeartRateRange(
    val minHr: Int,
    val maxHr: Int
)