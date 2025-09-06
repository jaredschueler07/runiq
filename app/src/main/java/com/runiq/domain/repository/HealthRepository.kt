package com.runiq.domain.repository

import com.runiq.core.util.Result
import com.runiq.data.local.entities.HealthMetricCacheEntity
import com.runiq.domain.model.GpsTrackPoint
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for health-related operations
 */
interface HealthRepository {

    /**
     * Check if Health Connect is available
     */
    suspend fun isHealthConnectAvailable(): Result<Boolean>

    /**
     * Check Health Connect permissions
     */
    suspend fun checkHealthConnectPermissions(): Result<List<String>>

    /**
     * Request Health Connect permissions
     */
    suspend fun requestHealthConnectPermissions(permissions: List<String>): Result<Boolean>

    /**
     * Write run session to Health Connect
     */
    suspend fun writeRunSessionToHealthConnect(
        sessionId: String,
        startTime: Long,
        endTime: Long,
        distance: Float,
        calories: Int,
        heartRateData: List<HeartRatePoint> = emptyList(),
        gpsTrack: List<GpsTrackPoint> = emptyList()
    ): Result<String>

    /**
     * Read run sessions from Health Connect
     */
    suspend fun readRunSessionsFromHealthConnect(
        startTime: Long,
        endTime: Long
    ): Result<List<ExternalRunSession>>

    /**
     * Write heart rate data to Health Connect
     */
    suspend fun writeHeartRateToHealthConnect(
        heartRatePoints: List<HeartRatePoint>
    ): Result<Unit>

    /**
     * Read heart rate data from Health Connect
     */
    suspend fun readHeartRateFromHealthConnect(
        startTime: Long,
        endTime: Long
    ): Result<List<HeartRatePoint>>

    /**
     * Write steps data to Health Connect
     */
    suspend fun writeStepsToHealthConnect(
        steps: Int,
        startTime: Long,
        endTime: Long
    ): Result<Unit>

    /**
     * Read steps data from Health Connect
     */
    suspend fun readStepsFromHealthConnect(
        startTime: Long,
        endTime: Long
    ): Result<List<StepsRecord>>

    /**
     * Observe cached health metrics
     */
    fun observeHealthMetrics(
        userId: String,
        metricType: String
    ): Flow<List<HealthMetricCacheEntity>>

    /**
     * Get health metrics for date range
     */
    suspend fun getHealthMetricsInDateRange(
        userId: String,
        metricType: String,
        startDate: String,
        endDate: String
    ): Result<List<HealthMetricCacheEntity>>

    /**
     * Cache health metric
     */
    suspend fun cacheHealthMetric(
        userId: String,
        metricType: String,
        value: Float,
        unit: String,
        date: String,
        source: String
    ): Result<Unit>

    /**
     * Sync health data from Health Connect
     */
    suspend fun syncFromHealthConnect(userId: String): Result<Unit>

    /**
     * Get daily summary
     */
    suspend fun getDailySummary(
        userId: String,
        date: String
    ): Result<DailySummary>

    /**
     * Get weekly summary
     */
    suspend fun getWeeklySummary(
        userId: String,
        weekStart: String
    ): Result<WeeklySummary>
}

/**
 * Heart rate data point
 */
data class HeartRatePoint(
    val timestamp: Long,
    val heartRate: Int,
    val confidence: Float = 1.0f
)

/**
 * External run session from Health Connect
 */
data class ExternalRunSession(
    val healthConnectId: String,
    val startTime: Long,
    val endTime: Long,
    val distance: Float,
    val calories: Int,
    val averageHeartRate: Int?,
    val maxHeartRate: Int?,
    val source: String,
    val exerciseType: String
)

/**
 * Steps record from Health Connect
 */
data class StepsRecord(
    val timestamp: Long,
    val steps: Int,
    val source: String
)

/**
 * Daily health summary
 */
data class DailySummary(
    val date: String,
    val steps: Int,
    val distance: Float,
    val calories: Int,
    val activeMinutes: Int,
    val averageHeartRate: Int?,
    val restingHeartRate: Int?,
    val sleepHours: Float?,
    val workoutCount: Int
)

/**
 * Weekly health summary
 */
data class WeeklySummary(
    val weekStart: String,
    val weekEnd: String,
    val totalSteps: Int,
    val totalDistance: Float,
    val totalCalories: Int,
    val totalActiveMinutes: Int,
    val averageHeartRate: Int?,
    val totalWorkouts: Int,
    val averageSleepHours: Float?,
    val dailySummaries: List<DailySummary>
)