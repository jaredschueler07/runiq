package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
            runSessionDao.completeRun(
                sessionId = sessionId,
                endTime = System.currentTimeMillis(),
                distance = distance,
                duration = duration,
                averagePace = averagePace,
                calories = calories,
                steps = steps
            )
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
        return runSessionDao.observeActiveSession(userId).asResult()
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
            runSessionDao.getPagedSessions(userId, limit, offset)
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
            val distance = lastPoint?.let {
                calculateDistance(
                    it.latitude, it.longitude,
                    latitude, longitude
                ) + (it.distance ?: 0f)
            } ?: 0f
            
            val point = GpsTrackPointEntity(
                sessionId = sessionId,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                speed = speed,
                accuracy = accuracy,
                timestamp = timestamp,
                distance = distance,
                pace = speed?.let { if (it > 0) 1000f / (it * 60f) else null },
                sequenceNumber = (lastPoint?.sequenceNumber ?: -1) + 1
            )
            
            gpsTrackDao.insert(point)
            Unit
        }
    }

    override suspend fun saveGpsTrackBatch(
        sessionId: String,
        points: List<GpsTrackPointEntity>
    ): Result<Unit> {
        return safeDatabaseCall {
            gpsTrackDao.saveTrackBatch(points.map { it.copy(sessionId = sessionId) })
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
                syncStatus = status,
                syncTime = if (status == SyncStatus.SYNCED) System.currentTimeMillis() else null
            )
            
            errorMessage?.let {
                runSessionDao.updateSyncError(sessionId, it)
            }
            Unit
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
                ?: throw IllegalArgumentException("Session not found")
            
            val avgPace = gpsTrackDao.getAveragePace(sessionId) ?: 0f
            val maxSpeed = gpsTrackDao.getMaxSpeed(sessionId) ?: 0f
            val totalDistance = gpsTrackDao.getTotalDistance(sessionId) ?: 0f
            val avgHeartRate = gpsTrackDao.getAverageHeartRate(sessionId)?.toInt()
            val maxAltitude = gpsTrackDao.getMaxAltitude(sessionId) ?: 0.0
            val minAltitude = gpsTrackDao.getMinAltitude(sessionId) ?: 0.0
            
            RunStatistics(
                sessionId = sessionId,
                totalDistance = totalDistance,
                duration = session.duration,
                averagePace = avgPace,
                maxSpeed = maxSpeed,
                averageHeartRate = avgHeartRate,
                elevationGain = (maxAltitude - minAltitude).toFloat(),
                calories = session.calories,
                steps = session.steps
            )
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val R = 6371000 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return (R * c).toFloat()
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