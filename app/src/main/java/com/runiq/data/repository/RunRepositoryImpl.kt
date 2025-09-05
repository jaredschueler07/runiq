package com.runiq.data.repository

<<<<<<< HEAD
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
=======
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
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
import javax.inject.Inject
import javax.inject.Singleton

/**
<<<<<<< HEAD
 * Implementation of RunRepository
 * Manages run sessions and GPS tracking data
=======
 * Implementation of RunRepository that coordinates between multiple data sources.
 * Follows the pattern: Room (local cache) -> Health Connect (primary) -> Firestore (backup)
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
 */
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val runSessionDao: RunSessionDao,
    private val gpsTrackDao: GpsTrackDao
<<<<<<< HEAD
) : BaseRepository(), RunRepository {

    override suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        coachId: String,
        targetPace: Float?
    ): Result<String> {
        return safeDatabaseCall {
            val session = RunSessionEntity(
=======
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
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
                userId = userId,
                startTime = System.currentTimeMillis(),
                workoutType = workoutType,
                coachId = coachId,
<<<<<<< HEAD
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
=======
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
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
