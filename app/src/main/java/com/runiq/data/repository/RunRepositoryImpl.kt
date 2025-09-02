package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.entities.RunSession
import com.runiq.data.local.entities.GpsTrackPoint
import com.runiq.data.local.preferences.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.repository.PersonalRecords
import com.runiq.domain.repository.WeeklyStats
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RunRepository
 * Coordinates between Room database, Health Connect, and Firestore
 */
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val runSessionDao: RunSessionDao,
    private val gpsTrackDao: GpsTrackDao
    // TODO: Add HealthConnectManager and FirestoreService when implemented
) : BaseRepository(), RunRepository {

    override suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        targetDistance: Float?,
        targetDuration: Long?,
        targetPace: Float?
    ): Result<String> {
        return safeDatabaseCall {
            val sessionId = UUID.randomUUID().toString()
            val session = RunSession(
                sessionId = sessionId,
                userId = userId,
                startTime = System.currentTimeMillis(),
                workoutType = workoutType.name,
                targetDistance = targetDistance,
                targetDuration = targetDuration,
                targetPace = targetPace,
                coachId = "default" // TODO: Get from user preferences
            )
            
            runSessionDao.insert(session)
            sessionId
        }
    }

    override suspend fun endRun(sessionId: String): Result<RunSession> {
        return safeDatabaseCall {
            val session = runSessionDao.getById(sessionId)
                ?: throw IllegalArgumentException("Run session not found: $sessionId")
            
            val endTime = System.currentTimeMillis()
            val duration = endTime - session.startTime
            
            val updatedSession = session.copy(
                endTime = endTime,
                duration = duration,
                updatedAt = System.currentTimeMillis()
            )
            
            runSessionDao.update(updatedSession)
            updatedSession
        }
    }

    override suspend fun pauseRun(sessionId: String): Result<Unit> {
        return safeDatabaseCall {
            // TODO: Implement pause logic
            // Mark GPS points as paused from current time
            val currentTime = System.currentTimeMillis()
            gpsTrackDao.updatePauseStatus(sessionId, currentTime, true)
        }
    }

    override suspend fun resumeRun(sessionId: String): Result<Unit> {
        return safeDatabaseCall {
            // TODO: Implement resume logic
            // Resume GPS tracking
            Unit
        }
    }

    override fun observeActiveRun(): Flow<RunSession?> {
        return runSessionDao.observeActiveSession()
    }

    override fun observeUserRuns(userId: String): Flow<List<RunSession>> {
        return runSessionDao.observeByUserId(userId)
    }

    override suspend fun getRunsInDateRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Result<List<RunSession>> {
        return safeDatabaseCall {
            runSessionDao.getByUserIdInDateRange(userId, startTime, endTime)
        }
    }

    override suspend fun getRunById(sessionId: String): Result<RunSession?> {
        return safeDatabaseCall {
            runSessionDao.getById(sessionId)
        }
    }

    override suspend fun saveGpsTrackPoint(trackPoint: GpsTrackPoint): Result<Unit> {
        return safeDatabaseCall {
            gpsTrackDao.insert(trackPoint)
            Unit
        }
    }

    override suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPoint>> {
        return safeDatabaseCall {
            gpsTrackDao.getBySessionId(sessionId)
        }
    }

    override fun observeGpsTrack(sessionId: String): Flow<List<GpsTrackPoint>> {
        return gpsTrackDao.observeBySessionId(sessionId)
    }

    override suspend fun updateRunMetrics(
        sessionId: String,
        distance: Float?,
        duration: Long?,
        averagePace: Float?,
        averageHeartRate: Int?,
        calories: Int?
    ): Result<Unit> {
        return safeDatabaseCall {
            val session = runSessionDao.getById(sessionId)
                ?: throw IllegalArgumentException("Run session not found: $sessionId")
            
            val updatedSession = session.copy(
                distance = distance ?: session.distance,
                duration = duration ?: session.duration,
                averagePace = averagePace ?: session.averagePace,
                averageHeartRate = averageHeartRate ?: session.averageHeartRate,
                calories = calories ?: session.calories,
                updatedAt = System.currentTimeMillis()
            )
            
            runSessionDao.update(updatedSession)
        }
    }

    override suspend fun syncWithHealthConnect(userId: String): Result<Unit> {
        // TODO: Implement Health Connect sync
        return Result.Success(Unit)
    }

    override suspend fun importFromHealthConnect(
        userId: String,
        since: Long
    ): Result<List<RunSession>> {
        // TODO: Implement Health Connect import
        return Result.Success(emptyList())
    }

    override suspend fun getWeeklyStats(
        userId: String,
        weekStart: Long
    ): Result<WeeklyStats> {
        return safeDatabaseCall {
            val runCount = runSessionDao.getWeeklyRunCount(userId, weekStart)
            val totalDistance = runSessionDao.getWeeklyDistance(userId, weekStart)
            
            // TODO: Calculate other stats
            WeeklyStats(
                totalRuns = runCount,
                totalDistance = totalDistance,
                totalDuration = 0L,
                averagePace = 0f,
                totalCalories = 0,
                targetDistance = 20f, // TODO: Get from preferences
                targetRuns = 3 // TODO: Get from preferences
            )
        }
    }

    override suspend fun getPersonalRecords(userId: String): Result<PersonalRecords> {
        return safeDatabaseCall {
            val longestRun = runSessionDao.getLongestRun(userId)
            val fastestRun = runSessionDao.getFastestRun(userId)
            
            // TODO: Calculate other records
            PersonalRecords(
                longestDistance = longestRun?.distance ?: 0f,
                fastestPace = fastestRun?.averagePace ?: 0f,
                longestDuration = longestRun?.duration ?: 0L,
                mostCalories = 0,
                totalRuns = 0,
                totalDistance = 0f,
                totalDuration = 0L
            )
        }
    }

    override suspend fun deleteOldRuns(userId: String, olderThanDays: Int): Result<Int> {
        return safeDatabaseCall {
            val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
            runSessionDao.deleteOldSessions(userId, cutoffTime)
        }
    }
}