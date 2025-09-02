package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.runiq.domain.model.RunSession
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RunSession operations.
 * Provides methods to interact with the run_sessions table.
 */
@Dao
interface RunSessionDao {
    
    @Query("SELECT * FROM run_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun observeUserSessions(userId: String): Flow<List<RunSession>>
    
    @Query("SELECT * FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): RunSession?
    
    @Query("SELECT * FROM run_sessions WHERE userId = :userId AND endTime IS NULL LIMIT 1")
    suspend fun getActiveSession(userId: String): RunSession?
    
    @Query("SELECT * FROM run_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit")
    suspend fun getRecentSessions(userId: String, limit: Int = 10): List<RunSession>
    
    @Query("SELECT * FROM run_sessions WHERE syncStatus = :status")
    suspend fun getSessionsByStatus(status: RunSession.SyncStatus): List<RunSession>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: RunSession): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<RunSession>): List<Long>
    
    @Update
    suspend fun update(session: RunSession): Int
    
    @Delete
    suspend fun delete(session: RunSession): Int
    
    @Query("DELETE FROM run_sessions WHERE sessionId = :sessionId")
    suspend fun deleteById(sessionId: String): Int
    
    @Query("UPDATE run_sessions SET healthConnectId = :healthConnectId WHERE sessionId = :sessionId")
    suspend fun updateHealthConnectId(sessionId: String, healthConnectId: String?): Int
    
    @Query("UPDATE run_sessions SET syncStatus = :status, lastSyncedAt = :timestamp WHERE sessionId = :sessionId")
    suspend fun updateSyncStatus(sessionId: String, status: RunSession.SyncStatus, timestamp: Long): Int
    
    @Query("SELECT COUNT(*) FROM run_sessions WHERE userId = :userId")
    suspend fun getUserSessionCount(userId: String): Int
    
    @Query("SELECT SUM(distance) FROM run_sessions WHERE userId = :userId AND endTime IS NOT NULL")
    suspend fun getTotalDistanceForUser(userId: String): Float?
    
    @Query("SELECT AVG(averagePace) FROM run_sessions WHERE userId = :userId AND endTime IS NOT NULL AND averagePace > 0")
    suspend fun getAveragePaceForUser(userId: String): Float?
}