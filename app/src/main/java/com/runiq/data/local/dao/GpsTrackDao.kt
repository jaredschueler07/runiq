package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.runiq.data.local.entities.GpsTrackPoint
import kotlinx.coroutines.flow.Flow

/**
 * DAO for GpsTrackPoint entity operations
 */
@Dao
abstract class GpsTrackDao : BaseIdDao<GpsTrackPoint, String>() {

    @Query("SELECT * FROM gps_track_points ORDER BY timestamp DESC")
    abstract override fun observeAll(): Flow<List<GpsTrackPoint>>

    @Query("SELECT * FROM gps_track_points ORDER BY timestamp DESC")
    abstract override suspend fun getAll(): List<GpsTrackPoint>

    @Query("SELECT COUNT(*) FROM gps_track_points")
    abstract override suspend fun getCount(): Int

    @Query("DELETE FROM gps_track_points")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT * FROM gps_track_points WHERE id = :id")
    abstract override suspend fun getById(id: String): GpsTrackPoint?

    @Query("SELECT * FROM gps_track_points WHERE id = :id")
    abstract override fun observeById(id: String): Flow<GpsTrackPoint?>

    @Query("DELETE FROM gps_track_points WHERE id = :id")
    abstract override suspend fun deleteById(id: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM gps_track_points WHERE id = :id)")
    abstract override suspend fun existsById(id: String): Boolean

    @Query("SELECT * FROM gps_track_points WHERE id IN (:ids)")
    abstract override suspend fun getByIds(ids: List<String>): List<GpsTrackPoint>

    @Query("DELETE FROM gps_track_points WHERE id IN (:ids)")
    abstract override suspend fun deleteByIds(ids: List<String>): Int

    // Custom queries specific to GpsTrackPoint
    
    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    abstract suspend fun getBySessionId(sessionId: String): List<GpsTrackPoint>

    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    abstract fun observeBySessionId(sessionId: String): Flow<List<GpsTrackPoint>>

    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId AND isAccurate = 1 ORDER BY timestamp ASC")
    abstract suspend fun getAccuratePointsBySessionId(sessionId: String): List<GpsTrackPoint>

    @Query("SELECT COUNT(*) FROM gps_track_points WHERE sessionId = :sessionId")
    abstract suspend fun getPointCountBySessionId(sessionId: String): Int

    @Query("DELETE FROM gps_track_points WHERE sessionId = :sessionId")
    abstract suspend fun deleteBySessionId(sessionId: String): Int

    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId AND timestamp >= :fromTime AND timestamp <= :toTime ORDER BY timestamp ASC")
    abstract suspend fun getBySessionIdInTimeRange(
        sessionId: String, 
        fromTime: Long, 
        toTime: Long
    ): List<GpsTrackPoint>

    @Query("SELECT MIN(timestamp) FROM gps_track_points WHERE sessionId = :sessionId")
    abstract suspend fun getFirstTimestamp(sessionId: String): Long?

    @Query("SELECT MAX(timestamp) FROM gps_track_points WHERE sessionId = :sessionId")
    abstract suspend fun getLastTimestamp(sessionId: String): Long?

    @Query("SELECT MAX(distanceFromStart) FROM gps_track_points WHERE sessionId = :sessionId")
    abstract suspend fun getTotalDistance(sessionId: String): Float?

    @Query("SELECT AVG(speed) FROM gps_track_points WHERE sessionId = :sessionId AND speed IS NOT NULL AND speed > 0")
    abstract suspend fun getAverageSpeed(sessionId: String): Float?

    @Query("SELECT MAX(speed) FROM gps_track_points WHERE sessionId = :sessionId AND speed IS NOT NULL")
    abstract suspend fun getMaxSpeed(sessionId: String): Float?

    @Query("SELECT * FROM gps_track_points WHERE sessionId = :sessionId AND accuracy IS NOT NULL AND accuracy <= :maxAccuracy ORDER BY timestamp ASC")
    abstract suspend fun getAccuratePoints(sessionId: String, maxAccuracy: Float): List<GpsTrackPoint>

    @Query("UPDATE gps_track_points SET isPaused = :isPaused WHERE sessionId = :sessionId AND timestamp >= :fromTime")
    abstract suspend fun updatePauseStatus(sessionId: String, fromTime: Long, isPaused: Boolean)

    @Query("DELETE FROM gps_track_points WHERE sessionId IN (SELECT sessionId FROM run_sessions WHERE startTime < :cutoffTime)")
    abstract suspend fun deleteOldTrackPoints(cutoffTime: Long): Int
}