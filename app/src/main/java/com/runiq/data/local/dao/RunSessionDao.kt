package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for RunSession entity with comprehensive CRUD operations
 */
@Dao
interface RunSessionDao {
    
    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: RunSessionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<RunSessionEntity>): List<Long>
    
    @Update
    suspend fun update(session: RunSessionEntity): Int
    
    @Delete
    suspend fun delete(session: RunSessionEntity): Int
    
    @Query("DELETE FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun deleteById(sessionId: String): Int
    
    // Basic queries
    @Query("SELECT * FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun getById(sessionId: String): RunSessionEntity?
    
    @Query("SELECT * FROM run_sessions WHERE sessionId = :sessionId")
    fun observeById(sessionId: String): Flow<RunSessionEntity?>
    
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId ORDER BY start_time DESC")
    suspend fun getAllByUser(userId: String): List<RunSessionEntity>
    
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId ORDER BY start_time DESC")
    fun observeAllByUser(userId: String): Flow<List<RunSessionEntity>>
    
    // Pagination support
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        ORDER BY start_time DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getByUserPaged(userId: String, limit: Int, offset: Int): List<RunSessionEntity>
    
    // Recent runs
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :sinceTimestamp 
        ORDER BY start_time DESC
    """)
    suspend fun getRecentRuns(userId: String, sinceTimestamp: Long): List<RunSessionEntity>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :sinceTimestamp 
        ORDER BY start_time DESC
    """)
    fun observeRecentRuns(userId: String, sinceTimestamp: Long): Flow<List<RunSessionEntity>>
    
    // Filter by workout type
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND workout_type = :workoutType 
        ORDER BY start_time DESC
    """)
    suspend fun getByWorkoutType(userId: String, workoutType: WorkoutType): List<RunSessionEntity>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND workout_type IN (:workoutTypes) 
        ORDER BY start_time DESC
    """)
    suspend fun getByWorkoutTypes(userId: String, workoutTypes: List<WorkoutType>): List<RunSessionEntity>
    
    // Date range queries
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time BETWEEN :startTime AND :endTime 
        ORDER BY start_time DESC
    """)
    suspend fun getByDateRange(
        userId: String, 
        startTime: Long, 
        endTime: Long
    ): List<RunSessionEntity>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time BETWEEN :startTime AND :endTime 
        ORDER BY start_time DESC
    """)
    fun observeByDateRange(
        userId: String, 
        startTime: Long, 
        endTime: Long
    ): Flow<List<RunSessionEntity>>
    
    // Sync-related queries
    @Query("SELECT * FROM run_sessions WHERE sync_status = :status")
    suspend fun getBySyncStatus(status: SyncStatus): List<RunSessionEntity>
    
    @Query("SELECT * FROM run_sessions WHERE sync_status = :status")
    fun observeBySyncStatus(status: SyncStatus): Flow<List<RunSessionEntity>>
    
    @Query("SELECT * FROM run_sessions WHERE sync_status IN ('PENDING', 'FAILED')")
    suspend fun getPendingSyncSessions(): List<RunSessionEntity>
    
    @Query("""
        UPDATE run_sessions 
        SET sync_status = :status, last_synced_at = :timestamp, sync_error_message = :errorMessage 
        WHERE sessionId = :sessionId
    """)
    suspend fun updateSyncStatus(
        sessionId: String, 
        status: SyncStatus, 
        timestamp: Long = System.currentTimeMillis(),
        errorMessage: String? = null
    ): Int
    
    @Query("UPDATE run_sessions SET health_connect_id = :healthConnectId WHERE sessionId = :sessionId")
    suspend fun updateHealthConnectId(sessionId: String, healthConnectId: String?): Int
    
    // Statistics and analytics
    @Query("""
        SELECT COUNT(*) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
    """)
    suspend fun getCompletedRunCount(userId: String): Int
    
    @Query("""
        SELECT SUM(distance) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
        AND start_time >= :sinceTimestamp
    """)
    suspend fun getTotalDistance(userId: String, sinceTimestamp: Long): Float?
    
    @Query("""
        SELECT SUM(duration) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
        AND start_time >= :sinceTimestamp
    """)
    suspend fun getTotalDuration(userId: String, sinceTimestamp: Long): Long?
    
    @Query("""
        SELECT AVG(average_pace) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
        AND average_pace > 0
        AND start_time >= :sinceTimestamp
    """)
    suspend fun getAveragePace(userId: String, sinceTimestamp: Long): Float?
    
    @Query("""
        SELECT MIN(best_pace) FROM run_sessions 
        WHERE user_id = :userId 
        AND best_pace IS NOT NULL
        AND best_pace > 0
        AND start_time >= :sinceTimestamp
    """)
    suspend fun getBestPace(userId: String, sinceTimestamp: Long): Float?
    
    @Query("""
        SELECT MAX(distance) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
        AND start_time >= :sinceTimestamp
    """)
    suspend fun getLongestRun(userId: String, sinceTimestamp: Long): Float?
    
    // Active session management
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId AND end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    suspend fun getActiveSession(userId: String): RunSessionEntity?
    
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId AND end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    fun observeActiveSession(userId: String): Flow<RunSessionEntity?>
    
    @Query("UPDATE run_sessions SET end_time = :endTime, duration = :duration WHERE sessionId = :sessionId")
    suspend fun completeSession(sessionId: String, endTime: Long, duration: Long): Int
    
    // Coach-related queries
    @Query("SELECT * FROM run_sessions WHERE coach_id = :coachId ORDER BY start_time DESC")
    suspend fun getByCoach(coachId: String): List<RunSessionEntity>
    
    @Query("""
        SELECT AVG(coaching_effectiveness_score) FROM run_sessions 
        WHERE coach_id = :coachId 
        AND coaching_effectiveness_score IS NOT NULL
    """)
    suspend fun getCoachEffectivenessScore(coachId: String): Float?
    
    // Cleanup operations
    @Query("DELETE FROM run_sessions WHERE start_time < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("""
        DELETE FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NULL 
        AND start_time < :timestamp
    """)
    suspend fun cleanupAbandonedSessions(userId: String, timestamp: Long): Int
    
    // Transaction operations
    @Transaction
    suspend fun insertWithGpsTrack(
        session: RunSessionEntity,
        gpsPoints: List<com.runiq.data.local.entities.GpsTrackPointEntity>
    ) {
        insert(session)
        // Note: GPS points insertion would be handled by GpsTrackDao
    }
    
    @Transaction
    suspend fun updateSessionMetrics(
        sessionId: String,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int
    ) {
        val currentTime = System.currentTimeMillis()
        updateSessionMetricsInternal(sessionId, distance, duration, averagePace, calories, currentTime)
    }
    
    @Query("""
        UPDATE run_sessions 
        SET distance = :distance, 
            duration = :duration, 
            average_pace = :averagePace, 
            calories = :calories,
            updated_at = :updatedAt
        WHERE sessionId = :sessionId
    """)
    suspend fun updateSessionMetricsInternal(
        sessionId: String,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int,
        updatedAt: Long
    ): Int
}