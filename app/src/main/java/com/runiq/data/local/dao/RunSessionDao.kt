package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.runiq.data.local.entities.RunSession
import kotlinx.coroutines.flow.Flow

/**
 * DAO for RunSession entity operations
 */
@Dao
abstract class RunSessionDao : BaseIdDao<RunSession, String>() {

    @Query("SELECT * FROM run_sessions ORDER BY startTime DESC")
    abstract override fun observeAll(): Flow<List<RunSession>>

    @Query("SELECT * FROM run_sessions ORDER BY startTime DESC")
    abstract override suspend fun getAll(): List<RunSession>

    @Query("SELECT COUNT(*) FROM run_sessions")
    abstract override suspend fun getCount(): Int

    @Query("DELETE FROM run_sessions")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT * FROM run_sessions WHERE sessionId = :id")
    abstract override suspend fun getById(id: String): RunSession?

    @Query("SELECT * FROM run_sessions WHERE sessionId = :id")
    abstract override fun observeById(id: String): Flow<RunSession?>

    @Query("DELETE FROM run_sessions WHERE sessionId = :id")
    abstract override suspend fun deleteById(id: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM run_sessions WHERE sessionId = :id)")
    abstract override suspend fun existsById(id: String): Boolean

    @Query("SELECT * FROM run_sessions WHERE sessionId IN (:ids)")
    abstract override suspend fun getByIds(ids: List<String>): List<RunSession>

    @Query("DELETE FROM run_sessions WHERE sessionId IN (:ids)")
    abstract override suspend fun deleteByIds(ids: List<String>): Int

    // Custom queries specific to RunSession
    
    @Query("SELECT * FROM run_sessions WHERE userId = :userId ORDER BY startTime DESC")
    abstract fun observeByUserId(userId: String): Flow<List<RunSession>>

    @Query("SELECT * FROM run_sessions WHERE userId = :userId AND startTime >= :fromTime ORDER BY startTime DESC")
    abstract suspend fun getByUserIdSince(userId: String, fromTime: Long): List<RunSession>

    @Query("SELECT * FROM run_sessions WHERE userId = :userId AND startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    abstract suspend fun getByUserIdInDateRange(userId: String, startTime: Long, endTime: Long): List<RunSession>

    @Query("SELECT * FROM run_sessions WHERE syncStatus = :status ORDER BY startTime ASC")
    abstract suspend fun getBySyncStatus(status: String): List<RunSession>

    @Query("SELECT * FROM run_sessions WHERE healthConnectId IS NULL AND userId = :userId")
    abstract suspend fun getUnsynced(userId: String): List<RunSession>

    @Query("UPDATE run_sessions SET healthConnectId = :healthConnectId, healthConnectSynced = 1 WHERE sessionId = :sessionId")
    abstract suspend fun updateHealthConnectId(sessionId: String, healthConnectId: String?)

    @Query("UPDATE run_sessions SET syncStatus = :status, lastSyncedAt = :timestamp WHERE sessionId = :sessionId")
    abstract suspend fun updateSyncStatus(sessionId: String, status: String, timestamp: Long)

    @Query("UPDATE run_sessions SET firestoreId = :firestoreId, syncedToCloud = 1 WHERE sessionId = :sessionId")
    abstract suspend fun updateFirestoreId(sessionId: String, firestoreId: String)

    @Query("SELECT * FROM run_sessions WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    abstract suspend fun getActiveSession(): RunSession?

    @Query("SELECT * FROM run_sessions WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    abstract fun observeActiveSession(): Flow<RunSession?>

    @Query("SELECT COUNT(*) FROM run_sessions WHERE userId = :userId AND startTime >= :weekStart")
    abstract suspend fun getWeeklyRunCount(userId: String, weekStart: Long): Int

    @Query("SELECT COALESCE(SUM(distance), 0) FROM run_sessions WHERE userId = :userId AND startTime >= :weekStart AND endTime IS NOT NULL")
    abstract suspend fun getWeeklyDistance(userId: String, weekStart: Long): Float

    @Query("SELECT AVG(averagePace) FROM run_sessions WHERE userId = :userId AND averagePace > 0 AND startTime >= :fromTime")
    abstract suspend fun getAveragePaceSince(userId: String, fromTime: Long): Float?

    @Query("SELECT * FROM run_sessions WHERE userId = :userId ORDER BY distance DESC LIMIT 1")
    abstract suspend fun getLongestRun(userId: String): RunSession?

    @Query("SELECT * FROM run_sessions WHERE userId = :userId AND averagePace > 0 ORDER BY averagePace ASC LIMIT 1")
    abstract suspend fun getFastestRun(userId: String): RunSession?

    @Query("DELETE FROM run_sessions WHERE userId = :userId AND startTime < :cutoffTime")
    abstract suspend fun deleteOldSessions(userId: String, cutoffTime: Long): Int
}