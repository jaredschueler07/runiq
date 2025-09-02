package com.runiq.domain.repository

import com.runiq.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for running session operations
 * Coordinates between Health Connect, Room database, and Firestore
 */
interface RunRepository {
    
    // Session management
    suspend fun startRun(workoutType: WorkoutType, coachId: String, userId: String): Result<String>
    suspend fun completeRun(sessionId: String): Result<Run>
    suspend fun pauseRun(sessionId: String): Result<Unit>
    suspend fun resumeRun(sessionId: String): Result<Unit>
    suspend fun cancelRun(sessionId: String): Result<Unit>
    
    // Session queries
    suspend fun getRunById(sessionId: String): Result<Run?>
    fun observeRunById(sessionId: String): Flow<Run?>
    suspend fun getActiveRun(userId: String): Result<Run?>
    fun observeActiveRun(userId: String): Flow<Run?>
    
    // Run history
    suspend fun getRecentRuns(userId: String, limit: Int = 20): Result<List<Run>>
    fun observeRecentRuns(userId: String, limit: Int = 20): Flow<List<Run>>
    suspend fun getRunsByDateRange(userId: String, startTime: Long, endTime: Long): Result<List<Run>>
    suspend fun getRunsByWorkoutType(userId: String, workoutType: WorkoutType): Result<List<Run>>
    
    // GPS tracking
    suspend fun addGpsPoint(sessionId: String, gpsPoint: GpsTrackPoint): Result<Unit>
    suspend fun addGpsPoints(sessionId: String, gpsPoints: List<GpsTrackPoint>): Result<Unit>
    suspend fun getGpsTrack(sessionId: String): Result<List<GpsTrackPoint>>
    fun observeGpsTrack(sessionId: String): Flow<List<GpsTrackPoint>>
    
    // Real-time updates
    suspend fun updateRunMetrics(
        sessionId: String,
        distance: Float,
        duration: Long,
        averagePace: Float,
        calories: Int
    ): Result<Unit>
    
    suspend fun updateHeartRate(sessionId: String, heartRate: Int): Result<Unit>
    
    // Statistics
    suspend fun getRunStatistics(userId: String, sinceTimestamp: Long): Result<RunStatistics>
    suspend fun getTotalDistance(userId: String, sinceTimestamp: Long): Result<Float>
    suspend fun getTotalDuration(userId: String, sinceTimestamp: Long): Result<Long>
    suspend fun getAveragePace(userId: String, sinceTimestamp: Long): Result<Float>
    suspend fun getBestPace(userId: String, sinceTimestamp: Long): Result<Float>
    
    // Sync operations
    suspend fun syncPendingRuns(): Result<Int>
    suspend fun syncRunWithHealthConnect(sessionId: String): Result<Unit>
    suspend fun syncRunWithFirestore(sessionId: String): Result<Unit>
    suspend fun importExternalRuns(userId: String, since: Long): Result<List<Run>>
    
    // Cleanup
    suspend fun deleteRun(sessionId: String): Result<Unit>
    suspend fun cleanupOldRuns(olderThanTimestamp: Long): Result<Int>
}

/**
 * Data class for run statistics
 */
data class RunStatistics(
    val totalRuns: Int,
    val totalDistance: Float, // meters
    val totalDuration: Long, // milliseconds
    val averagePace: Float, // min/km
    val bestPace: Float, // min/km
    val longestRun: Float, // meters
    val totalCalories: Int,
    val averageHeartRate: Int?,
    val workoutTypeDistribution: Map<WorkoutType, Int>
)