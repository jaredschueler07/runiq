package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RunRepository
 * Manages run sessions and GPS tracking data
 */
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val runSessionDao: RunSessionDao,
    private val gpsTrackDao: GpsTrackDao
) : BaseRepository(), RunRepository {

    override suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        coachId: String,
        targetPace: Float?
    ): Result<String> {
        return safeDatabaseCall {
            val session = RunSessionEntity(
                userId = userId,
                startTime = System.currentTimeMillis(),
                workoutType = workoutType,
                coachId = coachId,
                targetPace = targetPace
            )
            runSessionDao.insert(session)
            session.sessionId
        }
    }

    override suspend fun endRun(
        sessionId: String,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int,
        steps: Int?
    ): Result<Unit> {
        return safeDatabaseCall {
            val session = runSessionDao.getById(sessionId)
                ?: throw IllegalArgumentException("Session not found: $sessionId")
            
            val updatedSession = session.copy(
                endTime = System.currentTimeMillis(),
                distance = distance,
                duration = duration,
                averagePace = averagePace,
                calories = calories,
                steps = steps,
                syncStatus = SyncStatus.PENDING
            )
            runSessionDao.update(updatedSession)
        }
    }

    override suspend fun pauseRun(sessionId: String): Result<Unit> {
        return safeDatabaseCall {
            val pausePoint = gpsTrackDao.getLastPoint(sessionId)?.let { lastPoint ->
                lastPoint.copy(
                    id = 0,
                    timestamp = System.currentTimeMillis(),
                    sequenceNumber = lastPoint.sequenceNumber + 1,
                    isPausePoint = true
                )
            }
            
            pausePoint?.let {
                gpsTrackDao.insert(it)
            }
            Unit
        }
    }

    override suspend fun resumeRun(sessionId: String): Result<Unit> {
        return safeDatabaseCall {
            // Simply continue tracking, pause point already marked
            Unit
        }
    }

    override suspend fun getActiveRun(userId: String): Result<RunSessionEntity?> {
        return safeDatabaseCall {
            runSessionDao.getActiveSession(userId)
        }
    }

    override suspend fun getRunById(sessionId: String): Result<RunSessionEntity?> {
        return safeDatabaseCall {
            runSessionDao.getById(sessionId)
        }
    }

    override fun observeActiveRun(userId: String): Flow<Result<RunSessionEntity?>> {
        return runSessionDao.getAllByUser(userId)
            .map { sessions -> sessions.firstOrNull { it.endTime == null } }
            .asResult()
    }

    override fun observeRunHistory(userId: String): Flow<Result<List<RunSessionEntity>>> {
        return runSessionDao.getAllByUser(userId).asResult()
    }

    override suspend fun getRunHistory(
        userId: String,
        limit: Int,
        offset: Int
    ): Result<List<RunSessionEntity>> {
        return safeDatabaseCall {
            // For now, collect from Flow - would need paging query in DAO for full implementation  
            val allSessions = mutableListOf<RunSessionEntity>()
            runSessionDao.getAllByUser(userId).collect { sessions ->
                allSessions.addAll(sessions)
            }
            allSessions.drop(offset).take(limit)
        }
    }

    override suspend fun saveGpsTrackPoint(
        sessionId: String,
        latitude: Double,
        longitude: Double,
        altitude: Double?,
        speed: Float?,
        accuracy: Float?,
        timestamp: Long
    ): Result<Unit> {
        return safeDatabaseCall {
            val lastPoint = gpsTrackDao.getLastPoint(sessionId)
            val sequenceNumber = (lastPoint?.sequenceNumber ?: 0) + 1
            
            val point = GpsTrackPointEntity(
                sessionId = sessionId,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude ?: 0.0,
                speed = speed ?: 0f,
                accuracy = accuracy ?: 0f,
                timestamp = timestamp,
                sequenceNumber = sequenceNumber
            )
            
            gpsTrackDao.insert(point)
        }
    }

    override suspend fun saveGpsTrackBatch(
        sessionId: String,
        points: List<GpsTrackPointEntity>
    ): Result<Unit> {
        return safeDatabaseCall {
            gpsTrackDao.insertAll(points.map { it.copy(sessionId = sessionId) })
        }
    }

    override suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPointEntity>> {
        return safeDatabaseCall {
            gpsTrackDao.getTrackPoints(sessionId)
        }
    }

    override fun observeGpsTrack(sessionId: String): Flow<Result<List<GpsTrackPointEntity>>> {
        return gpsTrackDao.observeTrackPoints(sessionId).asResult()
    }

    override suspend fun updateSyncStatus(
        sessionId: String,
        status: SyncStatus,
        errorMessage: String?
    ): Result<Unit> {
        return safeDatabaseCall {
            runSessionDao.updateSyncStatus(
                sessionId = sessionId,
                status = status,
                timestamp = System.currentTimeMillis()
            )
            
            errorMessage?.let {
                runSessionDao.updateSyncError(sessionId, it)
            }
        }
    }

    override suspend fun getUnsyncedSessions(): Result<List<RunSessionEntity>> {
        return safeDatabaseCall {
            runSessionDao.getUnsyncedSessions()
        }
    }

    override suspend fun deleteRun(sessionId: String): Result<Unit> {
        return safeDatabaseCall {
            gpsTrackDao.deleteBySessionId(sessionId)
            runSessionDao.deleteById(sessionId)
        }
    }

    override suspend fun calculateRunStatistics(sessionId: String): Result<RunStatistics> {
        return safeDatabaseCall {
            val session = runSessionDao.getById(sessionId) 
                ?: throw IllegalArgumentException("Session not found: $sessionId")

            // Basic statistics calculation - would expand with actual GPS analysis
            RunStatistics(
                sessionId = sessionId,
                totalDistance = session.distance,
                duration = session.duration,
                averagePace = session.averagePace,
                maxSpeed = 0f, // Would calculate from GPS data
                averageHeartRate = session.averageHeartRate,
                elevationGain = 0f, // Would calculate from GPS data
                calories = session.calories,
                steps = session.steps
            )
        }
    }
}

/**
 * Statistics for a run session
 */
data class RunStatistics(
    val sessionId: String,
    val totalDistance: Float,
    val duration: Long,
    val averagePace: Float,
    val maxSpeed: Float,
    val averageHeartRate: Int?,
    val elevationGain: Float,
    val calories: Int,
    val steps: Int?
)