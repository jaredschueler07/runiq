package com.runiq.domain.repository

import com.runiq.core.util.Result
import com.runiq.data.local.entities.RunSession
import com.runiq.data.local.entities.GpsTrackPoint
import com.runiq.data.local.preferences.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for run-related operations
 * Abstracts data sources (Room, Health Connect, Firestore)
 */
interface RunRepository {

    /**
     * Start a new run session
     */
    suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        targetDistance: Float? = null,
        targetDuration: Long? = null,
        targetPace: Float? = null
    ): Result<String>

    /**
     * End the current active run session
     */
    suspend fun endRun(sessionId: String): Result<RunSession>

    /**
     * Pause the current run session
     */
    suspend fun pauseRun(sessionId: String): Result<Unit>

    /**
     * Resume the current run session
     */
    suspend fun resumeRun(sessionId: String): Result<Unit>

    /**
     * Observe the currently active run session
     */
    fun observeActiveRun(): Flow<RunSession?>

    /**
     * Get all run sessions for a user
     */
    fun observeUserRuns(userId: String): Flow<List<RunSession>>

    /**
     * Get run sessions in date range
     */
    suspend fun getRunsInDateRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Result<List<RunSession>>

    /**
     * Get a specific run session by ID
     */
    suspend fun getRunById(sessionId: String): Result<RunSession?>

    /**
     * Save GPS track point
     */
    suspend fun saveGpsTrackPoint(trackPoint: GpsTrackPoint): Result<Unit>

    /**
     * Get GPS track for a run session
     */
    suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPoint>>

    /**
     * Observe GPS track for a run session
     */
    fun observeGpsTrack(sessionId: String): Flow<List<GpsTrackPoint>>

    /**
     * Update run session metrics
     */
    suspend fun updateRunMetrics(
        sessionId: String,
        distance: Float? = null,
        duration: Long? = null,
        averagePace: Float? = null,
        averageHeartRate: Int? = null,
        calories: Int? = null
    ): Result<Unit>

    /**
     * Sync run sessions with Health Connect
     */
    suspend fun syncWithHealthConnect(userId: String): Result<Unit>

    /**
     * Import runs from Health Connect
     */
    suspend fun importFromHealthConnect(userId: String, since: Long): Result<List<RunSession>>

    /**
     * Get weekly statistics
     */
    suspend fun getWeeklyStats(userId: String, weekStart: Long): Result<WeeklyStats>

    /**
     * Get personal records
     */
    suspend fun getPersonalRecords(userId: String): Result<PersonalRecords>

    /**
     * Delete old run sessions
     */
    suspend fun deleteOldRuns(userId: String, olderThanDays: Int): Result<Int>
}

/**
 * Weekly statistics data class
 */
data class WeeklyStats(
    val totalRuns: Int,
    val totalDistance: Float,
    val totalDuration: Long,
    val averagePace: Float,
    val totalCalories: Int,
    val targetDistance: Float,
    val targetRuns: Int
)

/**
 * Personal records data class
 */
data class PersonalRecords(
    val longestDistance: Float,
    val fastestPace: Float,
    val longestDuration: Long,
    val mostCalories: Int,
    val totalRuns: Int,
    val totalDistance: Float,
    val totalDuration: Long
)