package com.runiq.domain.repository

<<<<<<< HEAD
import com.runiq.core.util.Result
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.model.SyncStatus
=======
import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.RunSession
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
<<<<<<< HEAD
 * Repository interface for managing run sessions and GPS tracking
 */
interface RunRepository {
    
    // Run Session Management
    suspend fun startRun(
        userId: String,
        workoutType: WorkoutType,
        coachId: String,
        targetPace: Float? = null
    ): Result<String>
    
    suspend fun endRun(
        sessionId: String,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int,
        steps: Int? = null
    ): Result<Unit>
    
    suspend fun pauseRun(sessionId: String): Result<Unit>
    
    suspend fun resumeRun(sessionId: String): Result<Unit>
    
    suspend fun getActiveRun(userId: String): Result<RunSessionEntity?>
    
    suspend fun getRunById(sessionId: String): Result<RunSessionEntity?>
    
    fun observeActiveRun(userId: String): Flow<Result<RunSessionEntity?>>
    
    fun observeRunHistory(userId: String): Flow<Result<List<RunSessionEntity>>>
    
    suspend fun getRunHistory(
        userId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<RunSessionEntity>>
    
    // GPS Tracking
    suspend fun saveGpsTrackPoint(
        sessionId: String,
        latitude: Double,
        longitude: Double,
        altitude: Double? = null,
        speed: Float? = null,
        accuracy: Float? = null,
        timestamp: Long = System.currentTimeMillis()
    ): Result<Unit>
    
    suspend fun saveGpsTrackBatch(
        sessionId: String,
        points: List<GpsTrackPointEntity>
    ): Result<Unit>
    
    suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPointEntity>>
    
    fun observeGpsTrack(sessionId: String): Flow<Result<List<GpsTrackPointEntity>>>
    
    // Sync Management
    suspend fun updateSyncStatus(
        sessionId: String,
        status: SyncStatus,
        errorMessage: String? = null
    ): Result<Unit>
    
    suspend fun getUnsyncedSessions(): Result<List<RunSessionEntity>>
    
    // Data Management
    suspend fun deleteRun(sessionId: String): Result<Unit>
    
    // Statistics
    suspend fun calculateRunStatistics(sessionId: String): Result<RunStatistics>
=======
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
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
}