package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.HealthMetricCacheDao
import com.runiq.data.local.entities.HealthMetricCache
import com.runiq.domain.repository.DailySummary
import com.runiq.domain.repository.WeeklySummary
import com.runiq.testing.base.BaseRepositoryTest
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for HealthRepositoryImpl
 */
class HealthRepositoryImplTest : BaseRepositoryTest() {
    
    private lateinit var repository: HealthRepositoryImpl
    private lateinit var healthMetricCacheDao: HealthMetricCacheDao
    
    private val testUserId = "test-user-123"
    private val testMetricType = "heart_rate"
    private val testDate = "2024-01-15"
    
    @Before
    fun setup() {
        healthMetricCacheDao = mockk()
        repository = HealthRepositoryImpl(healthMetricCacheDao)
    }
    
    @Test
    fun `isHealthConnectAvailable returns false when not implemented`() = runTest {
        // When
        val result = repository.isHealthConnectAvailable()
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse(result.data)
    }
    
    @Test
    fun `checkHealthConnectPermissions returns empty list when not implemented`() = runTest {
        // When
        val result = repository.checkHealthConnectPermissions()
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
    
    @Test
    fun `requestHealthConnectPermissions returns false when not implemented`() = runTest {
        // Given
        val permissions = listOf("ACTIVITY", "HEART_RATE")
        
        // When
        val result = repository.requestHealthConnectPermissions(permissions)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse(result.data)
    }
    
    @Test
    fun `writeRunSessionToHealthConnect returns error when not implemented`() = runTest {
        // When
        val result = repository.writeRunSessionToHealthConnect(
            sessionId = "session-123",
            startTime = System.currentTimeMillis() - 3600000,
            endTime = System.currentTimeMillis(),
            distance = 5000f,
            calories = 350,
            heartRateData = emptyList(),
            gpsTrack = emptyList()
        )
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is NotImplementedError)
    }
    
    @Test
    fun `readRunSessionsFromHealthConnect returns empty list when not implemented`() = runTest {
        // Given
        val startTime = System.currentTimeMillis() - 86400000
        val endTime = System.currentTimeMillis()
        
        // When
        val result = repository.readRunSessionsFromHealthConnect(startTime, endTime)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
    
    @Test
    fun `observeHealthMetrics returns flow from dao`() = runTest {
        // Given
        val metrics = listOf(
            HealthMetricCache(
                userId = testUserId,
                date = testDate,
                metricType = testMetricType,
                value = 145f,
                unit = "bpm",
                source = "Health Connect",
                aggregationType = "DAILY"
            )
        )
        
        every { 
            healthMetricCacheDao.observeByUserIdAndType(testUserId, testMetricType) 
        } returns flowOf(metrics)
        
        // When
        val result = repository.observeHealthMetrics(testUserId, testMetricType).first()
        
        // Then
        assertEquals(metrics, result)
        
        verify(exactly = 1) {
            healthMetricCacheDao.observeByUserIdAndType(testUserId, testMetricType)
        }
    }
    
    @Test
    fun `getHealthMetricsInDateRange returns metrics successfully`() = runTest {
        // Given
        val startDate = "2024-01-01"
        val endDate = "2024-01-31"
        val metrics = listOf(
            HealthMetricCache(
                userId = testUserId,
                date = "2024-01-15",
                metricType = testMetricType,
                value = 150f,
                unit = "bpm",
                source = "Health Connect",
                aggregationType = "DAILY"
            )
        )
        
        coEvery { 
            healthMetricCacheDao.getByUserIdInDateRange(testUserId, testMetricType, startDate, endDate)
        } returns metrics
        
        // When
        val result = repository.getHealthMetricsInDateRange(
            testUserId, testMetricType, startDate, endDate
        )
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(metrics, result.data)
    }
    
    @Test
    fun `getHealthMetricsInDateRange handles dao error`() = runTest {
        // Given
        coEvery { 
            healthMetricCacheDao.getByUserIdInDateRange(any(), any(), any(), any())
        } throws Exception("Database error")
        
        // When
        val result = repository.getHealthMetricsInDateRange(
            testUserId, testMetricType, "2024-01-01", "2024-01-31"
        )
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
    }
    
    @Test
    fun `cacheHealthMetric inserts metric successfully`() = runTest {
        // Given
        val value = 72f
        val unit = "bpm"
        val source = "Manual"
        val capturedMetric = slot<HealthMetricCache>()
        
        coEvery { healthMetricCacheDao.insert(capture(capturedMetric)) } just runs
        
        // When
        val result = repository.cacheHealthMetric(
            userId = testUserId,
            metricType = testMetricType,
            value = value,
            unit = unit,
            date = testDate,
            source = source
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        val savedMetric = capturedMetric.captured
        assertEquals(testUserId, savedMetric.userId)
        assertEquals(testDate, savedMetric.date)
        assertEquals(testMetricType, savedMetric.metricType)
        assertEquals(value, savedMetric.value)
        assertEquals(unit, savedMetric.unit)
        assertEquals(source, savedMetric.source)
        assertEquals("DAILY", savedMetric.aggregationType)
        
        coVerify(exactly = 1) { healthMetricCacheDao.insert(any()) }
    }
    
    @Test
    fun `syncFromHealthConnect returns success when not implemented`() = runTest {
        // When
        val result = repository.syncFromHealthConnect(testUserId)
        
        // Then
        assertTrue(result is Result.Success)
    }
    
    @Test
    fun `getDailySummary returns empty summary`() = runTest {
        // Given
        val metrics = listOf(
            HealthMetricCache(
                userId = testUserId,
                date = testDate,
                metricType = "steps",
                value = 8000f,
                unit = "count",
                source = "Health Connect",
                aggregationType = "DAILY"
            )
        )
        
        coEvery { 
            healthMetricCacheDao.getByUserIdAndDate(testUserId, testDate) 
        } returns metrics
        
        // When
        val result = repository.getDailySummary(testUserId, testDate)
        
        // Then
        assertTrue(result is Result.Success)
        val summary = result.data
        assertEquals(testDate, summary.date)
        assertEquals(0, summary.steps) // Not implemented yet
        assertEquals(0f, summary.distance)
        assertEquals(0, summary.calories)
        assertEquals(0, summary.activeMinutes)
        assertNull(summary.averageHeartRate)
        assertNull(summary.restingHeartRate)
        assertNull(summary.sleepHours)
        assertEquals(0, summary.workoutCount)
    }
    
    @Test
    fun `getDailySummary handles dao error`() = runTest {
        // Given
        coEvery { 
            healthMetricCacheDao.getByUserIdAndDate(any(), any()) 
        } throws Exception("Database error")
        
        // When
        val result = repository.getDailySummary(testUserId, testDate)
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
    }
    
    @Test
    fun `getWeeklySummary returns empty summary`() = runTest {
        // Given
        val weekStart = "2024-01-08"
        
        // When
        val result = repository.getWeeklySummary(testUserId, weekStart)
        
        // Then
        assertTrue(result is Result.Success)
        val summary = result.data
        assertEquals(weekStart, summary.weekStart)
        assertEquals(weekStart, summary.weekEnd) // Not calculated yet
        assertEquals(0, summary.totalSteps)
        assertEquals(0f, summary.totalDistance)
        assertEquals(0, summary.totalCalories)
        assertEquals(0, summary.totalActiveMinutes)
        assertNull(summary.averageHeartRate)
        assertEquals(0, summary.totalWorkouts)
        assertNull(summary.averageSleepHours)
        assertTrue(summary.dailySummaries.isEmpty())
    }
    
    @Test
    fun `writeHeartRateToHealthConnect returns success`() = runTest {
        // When
        val result = repository.writeHeartRateToHealthConnect(emptyList())
        
        // Then
        assertTrue(result is Result.Success)
    }
    
    @Test
    fun `readHeartRateFromHealthConnect returns empty list`() = runTest {
        // Given
        val startTime = System.currentTimeMillis() - 3600000
        val endTime = System.currentTimeMillis()
        
        // When
        val result = repository.readHeartRateFromHealthConnect(startTime, endTime)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
    
    @Test
    fun `writeStepsToHealthConnect returns success`() = runTest {
        // When
        val result = repository.writeStepsToHealthConnect(
            steps = 5000,
            startTime = System.currentTimeMillis() - 3600000,
            endTime = System.currentTimeMillis()
        )
        
        // Then
        assertTrue(result is Result.Success)
    }
    
    @Test
    fun `readStepsFromHealthConnect returns empty list`() = runTest {
        // Given
        val startTime = System.currentTimeMillis() - 86400000
        val endTime = System.currentTimeMillis()
        
        // When
        val result = repository.readStepsFromHealthConnect(startTime, endTime)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
}