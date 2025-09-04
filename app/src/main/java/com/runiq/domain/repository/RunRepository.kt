package com.runiq.domain.repository

import com.runiq.core.util.Result
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
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
}