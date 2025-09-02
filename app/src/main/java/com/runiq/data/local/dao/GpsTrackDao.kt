package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.runiq.domain.model.GpsTrackPoint
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for GPS track point operations.
 */
@Dao
interface GpsTrackDao {
    
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
}