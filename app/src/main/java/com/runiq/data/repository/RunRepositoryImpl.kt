package com.runiq.data.repository

import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RunRepository that coordinates between multiple data sources.
 * Follows the pattern: Room (local cache) -> Health Connect (primary) -> Firestore (backup)
 */
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val runSessionDao: RunSessionDao,
    private val gpsTrackDao: GpsTrackDao
    // TODO: Add HealthConnectManager and FirestoreService when implemented
) : RunRepository {
    
    override suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        coachId: String
    ): Result<RunSession> {
        return try {
            val session = RunSession(
                sessionId = UUID.randomUUID().toString(),
                userId = userId,
                startTime = System.currentTimeMillis(),
                workoutType = workoutType,
                coachId = coachId,
                syncStatus = RunSession.SyncStatus.PENDING
            )
            
            // Save to local database first
            withContext(Dispatchers.IO) {
                runSessionDao.insert(session)
            }
            
            // TODO: Write to Health Connect
            // TODO: Backup to Firestore
            
            Timber.d("Started run session: ${session.sessionId}")
            Result.success(session)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start run session")
            Result.failure(e)
        }
    }
    
    override suspend fun endRun(sessionId: String): Result<RunSession> {
        return try {
            val session = runSessionDao.getSessionById(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found: $sessionId"))
            
            val endedSession = session.copy(
                endTime = System.currentTimeMillis(),
                syncStatus = RunSession.SyncStatus.PENDING
            )
            
            withContext(Dispatchers.IO) {
                runSessionDao.update(endedSession)
            }
            
            Timber.d("Ended run session: $sessionId")
            Result.success(endedSession)
        } catch (e: Exception) {
            Timber.e(e, "Failed to end run session")
            Result.failure(e)
        }
    }
    
    override fun observeActiveRun(userId: String): Flow<RunSession?> {
        return runSessionDao.observeUserSessions(userId)
            .map { sessions -> sessions.firstOrNull { it.isActive } }
    }
    
    override fun observeUserSessions(userId: String): Flow<List<RunSession>> {
        return runSessionDao.observeUserSessions(userId)
    }
    
    override suspend fun getSessionById(sessionId: String): Result<RunSession> {
        return try {
            val session = runSessionDao.getSessionById(sessionId)
            if (session != null) {
                Result.success(session)
            } else {
                Result.failure(NoSuchElementException("Session not found: $sessionId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get session by ID")
            Result.failure(e)
        }
    }
    
    override suspend fun saveGpsTrackPoints(
        sessionId: String,
        trackPoints: List<GpsTrackPoint>
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                gpsTrackDao.insertAll(trackPoints)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save GPS track points")
            Result.failure(e)
        }
    }
    
    override suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPoint>> {
        return try {
            val trackPoints = gpsTrackDao.getTrackPoints(sessionId)
            Result.success(trackPoints)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get GPS track")
            Result.failure(e)
        }
    }
    
    override suspend fun syncPendingSessions(): Result<Int> {
        return try {
            val pendingSessions = runSessionDao.getSessionsByStatus(RunSession.SyncStatus.PENDING)
            
            // TODO: Implement actual sync logic with Health Connect and Firestore
            var syncedCount = 0
            pendingSessions.forEach { session ->
                try {
                    // Simulate sync
                    runSessionDao.updateSyncStatus(
                        session.sessionId,
                        RunSession.SyncStatus.SYNCED,
                        System.currentTimeMillis()
                    )
                    syncedCount++
                } catch (e: Exception) {
                    Timber.w(e, "Failed to sync session: ${session.sessionId}")
                    runSessionDao.updateSyncStatus(
                        session.sessionId,
                        RunSession.SyncStatus.FAILED,
                        System.currentTimeMillis()
                    )
                }
            }
            
            Result.success(syncedCount)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync pending sessions")
            Result.failure(e)
        }
    }
    
    override suspend fun updateSessionMetrics(
        sessionId: String,
        distance: Float,
        averagePace: Float,
        calories: Int,
        averageHeartRate: Int?
    ): Result<Unit> {
        return try {
            val session = runSessionDao.getSessionById(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found: $sessionId"))
            
            val updatedSession = session.copy(
                distance = distance,
                averagePace = averagePace,
                calories = calories,
                averageHeartRate = averageHeartRate,
                syncStatus = RunSession.SyncStatus.PENDING
            )
            
            withContext(Dispatchers.IO) {
                runSessionDao.update(updatedSession)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update session metrics")
            Result.failure(e)
        }
    }
}