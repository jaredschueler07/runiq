package com.runiq.data.local.dao

<<<<<<< HEAD
import androidx.room.*
import com.runiq.data.local.entities.GpsTrackPointEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for GPS track points with location tracking and route analysis
=======
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.runiq.domain.model.GpsTrackPoint
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for GPS track point operations.
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
 */
@Dao
interface GpsTrackDao {
    
<<<<<<< HEAD
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: GpsTrackPointEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<GpsTrackPointEntity>)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBatch(points: List<GpsTrackPointEntity>): List<Long>
    
    // Update operations
    @Update
    suspend fun update(point: GpsTrackPointEntity)
    
    @Query("UPDATE gps_track_points SET is_pause_point = :isPaused WHERE id = :pointId")
    suspend fun updatePauseStatus(pointId: Long, isPaused: Boolean)
    
    @Query("UPDATE gps_track_points SET heart_rate = :heartRate WHERE id = :pointId")
    suspend fun updateHeartRate(pointId: Long, heartRate: Int)
    
    // Delete operations
    @Delete
    suspend fun delete(point: GpsTrackPointEntity)
    
    @Query("DELETE FROM gps_track_points WHERE session_id = :sessionId")
    suspend fun deleteBySessionId(sessionId: String)
    
    @Query("DELETE FROM gps_track_points WHERE id = :pointId")
    suspend fun deleteById(pointId: Long)
    
    // Query operations - Single points
    @Query("SELECT * FROM gps_track_points WHERE id = :pointId")
    suspend fun getById(pointId: Long): GpsTrackPointEntity?
    
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
    
    // Query operations - Track retrieval
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC
    """)
    suspend fun getTrackPoints(sessionId: String): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC
    """)
    fun observeTrackPoints(sessionId: String): Flow<List<GpsTrackPointEntity>>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND is_pause_point = 0 
        ORDER BY sequence_number ASC
    """)
    suspend fun getActiveTrackPoints(sessionId: String): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND timestamp >= :startTime 
        AND timestamp <= :endTime 
        ORDER BY sequence_number ASC
    """)
    suspend fun getPointsInTimeRange(
        sessionId: String,
        startTime: Long,
        endTime: Long
    ): List<GpsTrackPointEntity>
    
    // Pagination support for large tracks
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        ORDER BY sequence_number ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getTrackPointsPaged(
        sessionId: String,
        limit: Int,
        offset: Int
    ): List<GpsTrackPointEntity>
    
    // Statistics and analysis queries
    @Query("SELECT COUNT(*) FROM gps_track_points WHERE session_id = :sessionId")
    suspend fun getPointCount(sessionId: String): Int
    
    @Query("SELECT COUNT(*) FROM gps_track_points WHERE session_id = :sessionId AND is_pause_point = 1")
    suspend fun getPausePointCount(sessionId: String): Int
    
    @Query("""
        SELECT MAX(distance) FROM gps_track_points 
        WHERE session_id = :sessionId
    """)
    suspend fun getTotalDistance(sessionId: String): Float?
    
    @Query("""
        SELECT MAX(altitude) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND altitude IS NOT NULL
    """)
    suspend fun getMaxAltitude(sessionId: String): Double?
    
    @Query("""
        SELECT MIN(altitude) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND altitude IS NOT NULL
    """)
    suspend fun getMinAltitude(sessionId: String): Double?
    
    @Query("""
        SELECT AVG(speed) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND speed IS NOT NULL 
        AND is_pause_point = 0
    """)
    suspend fun getAverageSpeed(sessionId: String): Float?
    
    @Query("""
        SELECT MAX(speed) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND speed IS NOT NULL
    """)
    suspend fun getMaxSpeed(sessionId: String): Float?
    
    @Query("""
        SELECT AVG(heart_rate) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND heart_rate IS NOT NULL 
        AND is_pause_point = 0
    """)
    suspend fun getAverageHeartRate(sessionId: String): Float?
    
    // Segment analysis
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND sequence_number >= :startSequence 
        AND sequence_number <= :endSequence 
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
        AND distance >= :startDistance 
        AND distance <= :endDistance 
        ORDER BY sequence_number ASC
    """)
    suspend fun getPointsByDistance(
        sessionId: String,
        startDistance: Float,
        endDistance: Float
    ): List<GpsTrackPointEntity>
    
    // Pace analysis
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND pace IS NOT NULL 
        AND pace <= :maxPace 
        ORDER BY pace ASC 
        LIMIT :limit
    """)
    suspend fun getFastestSegments(
        sessionId: String,
        maxPace: Float,
        limit: Int = 10
    ): List<GpsTrackPointEntity>
    
    @Query("""
        SELECT AVG(pace) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND pace IS NOT NULL 
        AND is_pause_point = 0
    """)
    suspend fun getAveragePace(sessionId: String): Float?
    
    // Split times (per kilometer or mile)
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND (CAST(distance / :splitDistance AS INTEGER) != 
             CAST((SELECT distance FROM gps_track_points 
                   WHERE session_id = :sessionId 
                   AND sequence_number = sequence_number - 1) / :splitDistance AS INTEGER) 
             OR sequence_number = 1)
        ORDER BY sequence_number ASC
    """)
    suspend fun getSplitPoints(sessionId: String, splitDistance: Float = 1000f): List<GpsTrackPointEntity>
    
    // Cleanup and maintenance
    @Query("DELETE FROM gps_track_points WHERE session_id IN (SELECT sessionId FROM run_sessions WHERE start_time < :beforeDate)")
    suspend fun deleteOldTrackPoints(beforeDate: Long): Int
    
    @Query("""
        DELETE FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND accuracy > :maxAccuracy
    """)
    suspend fun deleteLowAccuracyPoints(sessionId: String, maxAccuracy: Float = 50f): Int
    
    // Batch operations for performance
    @Transaction
    suspend fun saveTrackBatch(points: List<GpsTrackPointEntity>) {
        if (points.isNotEmpty()) {
            val sessionId = points.first().sessionId
            val lastPoint = getLastPoint(sessionId)
            val startSequence = (lastPoint?.sequenceNumber ?: -1) + 1
            
            val pointsWithSequence = points.mapIndexed { index, point ->
                point.copy(sequenceNumber = startSequence + index)
            }
            insertAll(pointsWithSequence)
        }
    }
    
    // Data optimization
    @Query("""
        SELECT * FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND (sequence_number % :nth = 0 OR is_pause_point = 1) 
        ORDER BY sequence_number ASC
    """)
    suspend fun getSimplifiedTrack(sessionId: String, nth: Int = 5): List<GpsTrackPointEntity>
    
    // Route matching and comparison
    @Query("""
        SELECT COUNT(*) FROM gps_track_points 
        WHERE session_id = :sessionId 
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
    """)
    suspend fun getPointsInBoundingBox(
        sessionId: String,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Int
=======
    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observeTrackPoints(sessionId: String): Flow<List<GpsTrackPoint>>
    
    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getTrackPoints(sessionId: String): List<GpsTrackPoint>
    
    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastTrackPoint(sessionId: String): GpsTrackPoint?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackPoint: GpsTrackPoint): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trackPoints: List<GpsTrackPoint>): List<Long>
    
    @Query("DELETE FROM gps_track_points WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: String): Int
    
    @Query("SELECT COUNT(*) FROM gps_track_points WHERE sessionId = :sessionId")
    suspend fun getPointCount(sessionId: String): Int
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
}