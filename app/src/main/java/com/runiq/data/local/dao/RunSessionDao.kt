package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for RunSession entity with comprehensive operations for fitness tracking
 */
@Dao
interface RunSessionDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: RunSessionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<RunSessionEntity>)
    
    // Update operations
    @Update
    suspend fun update(session: RunSessionEntity)
    
    @Query("UPDATE run_sessions SET sync_status = :status, last_synced_at = :timestamp WHERE sessionId = :sessionId")
    suspend fun updateSyncStatus(sessionId: String, status: SyncStatus, timestamp: Long)
    
    @Query("UPDATE run_sessions SET end_time = :endTime, duration = :duration, distance = :distance, average_pace = :pace WHERE sessionId = :sessionId")
    suspend fun updateRunCompletion(
        sessionId: String,
        endTime: Long,
        duration: Long,
        distance: Float,
        pace: Float
    )
    
    @Query("UPDATE run_sessions SET health_connect_id = :healthConnectId WHERE sessionId = :sessionId")
    suspend fun updateHealthConnectId(sessionId: String, healthConnectId: String)
    
    @Query("UPDATE run_sessions SET sync_error_message = :errorMessage WHERE sessionId = :sessionId")
    suspend fun updateSyncError(sessionId: String, errorMessage: String)
    
    // Delete operations
    @Delete
    suspend fun delete(session: RunSessionEntity)
    
    @Query("DELETE FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun deleteById(sessionId: String)
    
    @Query("DELETE FROM run_sessions WHERE user_id = :userId")
    suspend fun deleteAllByUser(userId: String)
    
    // Query operations - Single items
    @Query("SELECT * FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun getById(sessionId: String): RunSessionEntity?
    
    @Query("SELECT * FROM run_sessions WHERE health_connect_id = :healthConnectId")
    suspend fun getByHealthConnectId(healthConnectId: String): RunSessionEntity?
    
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId AND end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    suspend fun getActiveSession(userId: String): RunSessionEntity?
    
    // Query operations - Lists with Flow for reactive updates
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId ORDER BY start_time DESC")
    fun getAllByUser(userId: String): Flow<List<RunSessionEntity>>
    
    @Query("SELECT * FROM run_sessions WHERE user_id = :userId ORDER BY start_time DESC LIMIT :limit")
    fun getRecentSessions(userId: String, limit: Int): Flow<List<RunSessionEntity>>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :startDate 
        AND start_time <= :endDate 
        ORDER BY start_time DESC
    """)
    fun getSessionsInDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<RunSessionEntity>>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND workout_type = :workoutType 
        ORDER BY start_time DESC
    """)
    fun getSessionsByWorkoutType(
        userId: String,
        workoutType: WorkoutType
    ): Flow<List<RunSessionEntity>>
    
    // Sync management queries
    @Query("SELECT * FROM run_sessions WHERE sync_status = :status")
    suspend fun getSessionsBySyncStatus(status: SyncStatus): List<RunSessionEntity>
    
    @Query("SELECT * FROM run_sessions WHERE sync_status = 'PENDING' OR sync_status = 'FAILED'")
    suspend fun getUnsyncedSessions(): List<RunSessionEntity>
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE sync_status = 'FAILED' 
        AND updated_at > :retryAfter
        ORDER BY updated_at ASC
    """)
    suspend fun getFailedSessionsForRetry(retryAfter: Long): List<RunSessionEntity>
    
    // Statistics queries
    @Query("""
        SELECT COUNT(*) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
    """)
    suspend fun getTotalRunCount(userId: String): Int
    
    @Query("""
        SELECT SUM(distance) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
    """)
    suspend fun getTotalDistance(userId: String): Float?
    
    @Query("""
        SELECT SUM(duration) FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL
    """)
    suspend fun getTotalDuration(userId: String): Long?
    
    @Query("""
        SELECT AVG(average_pace) FROM run_sessions 
        WHERE user_id = :userId 
        AND workout_type = :workoutType 
        AND end_time IS NOT NULL
    """)
    suspend fun getAveragePaceByType(userId: String, workoutType: WorkoutType): Float?
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL 
        ORDER BY distance DESC 
        LIMIT 1
    """)
    suspend fun getLongestRun(userId: String): RunSessionEntity?
    
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND end_time IS NOT NULL 
        AND average_pace > 0 
        ORDER BY average_pace ASC 
        LIMIT 1
    """)
    suspend fun getFastestRun(userId: String): RunSessionEntity?
    
    // Weekly/Monthly statistics
    @Query("""
        SELECT COUNT(*) FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :weekStart 
        AND end_time IS NOT NULL
    """)
    suspend fun getWeeklyRunCount(userId: String, weekStart: Long): Int
    
    @Query("""
        SELECT SUM(distance) FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :weekStart 
        AND end_time IS NOT NULL
    """)
    suspend fun getWeeklyDistance(userId: String, weekStart: Long): Float?
    
    @Query("""
        SELECT COUNT(DISTINCT DATE(start_time/1000, 'unixepoch')) FROM run_sessions 
        WHERE user_id = :userId 
        AND start_time >= :monthStart 
        AND end_time IS NOT NULL
    """)
    suspend fun getMonthlyActiveDays(userId: String, monthStart: Long): Int
    
    // Coach effectiveness queries
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND coach_id = :coachId 
        AND end_time IS NOT NULL 
        ORDER BY start_time DESC
    """)
    fun getSessionsByCoach(userId: String, coachId: String): Flow<List<RunSessionEntity>>
    
    @Query("""
        SELECT AVG(coaching_effectiveness_score) FROM run_sessions 
        WHERE user_id = :userId 
        AND coach_id = :coachId 
        AND coaching_effectiveness_score IS NOT NULL
    """)
    suspend fun getCoachEffectivenessAverage(userId: String, coachId: String): Float?
    
    // Performance trend queries
    @Query("""
        SELECT * FROM run_sessions 
        WHERE user_id = :userId 
        AND workout_type = :workoutType 
        AND end_time IS NOT NULL 
        AND start_time >= :since 
        ORDER BY start_time ASC
    """)
    suspend fun getPerformanceTrend(
        userId: String,
        workoutType: WorkoutType,
        since: Long
    ): List<RunSessionEntity>
    
    // Cleanup operations
    @Query("DELETE FROM run_sessions WHERE start_time < :beforeDate AND sync_status = 'SYNCED'")
    suspend fun deleteOldSyncedSessions(beforeDate: Long): Int
    
    @Query("UPDATE run_sessions SET updated_at = :timestamp WHERE sessionId = :sessionId")
    suspend fun touch(sessionId: String, timestamp: Long = System.currentTimeMillis())
    
    // Transaction for complex operations
    @Transaction
    suspend fun completeRun(
        sessionId: String,
        endTime: Long,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int,
        steps: Int?
    ) {
        val session = getById(sessionId)
        session?.let {
            update(
                it.copy(
                    endTime = endTime,
                    distance = distance,
                    duration = duration,
                    averagePace = averagePace,
                    calories = calories,
                    steps = steps,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}