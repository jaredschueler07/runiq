package com.runiq.domain.repository

import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for run session operations.
 * Defines the contract for data operations across multiple data sources.
 */
interface RunRepository {
    
    /**
     * Starts a new run session.
     */
    suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        coachId: String
    ): Result<RunSession>
    
    /**
     * Ends the current active run session.
     */
    suspend fun endRun(sessionId: String): Result<RunSession>
    
    /**
     * Observes the currently active run session for a user.
     */
    fun observeActiveRun(userId: String): Flow<RunSession?>
    
    /**
     * Observes all run sessions for a user.
     */
    fun observeUserSessions(userId: String): Flow<List<RunSession>>
    
    /**
     * Gets a specific run session by ID.
     */
    suspend fun getSessionById(sessionId: String): Result<RunSession>
    
    /**
     * Saves GPS track points for a session.
     */
    suspend fun saveGpsTrackPoints(
        sessionId: String,
        trackPoints: List<GpsTrackPoint>
    ): Result<Unit>
    
    /**
     * Gets GPS track for a session.
     */
    suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPoint>>
    
    /**
     * Syncs pending sessions to cloud storage.
     */
    suspend fun syncPendingSessions(): Result<Int>
    
    /**
     * Updates session metrics (distance, pace, etc.).
     */
    suspend fun updateSessionMetrics(
        sessionId: String,
        distance: Float,
        averagePace: Float,
        calories: Int,
        averageHeartRate: Int?
    ): Result<Unit>
}