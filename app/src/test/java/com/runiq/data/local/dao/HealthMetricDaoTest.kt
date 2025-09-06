package com.runiq.data.local.dao

import com.runiq.data.local.entities.HealthMetricCacheEntity
import com.runiq.testing.base.BaseDaoTest
import com.runiq.testing.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Comprehensive unit tests for HealthMetricDao
 */
@ExperimentalCoroutinesApi
class HealthMetricDaoTest : BaseDaoTest() {
    
    private lateinit var healthMetricDao: HealthMetricDao
    private val testUserId = "test-user-123"
    
    @Before
    fun setupDao() {
        healthMetricDao = database.healthMetricDao()
    }
    
    @Test
    fun `insert and retrieve health metric`() = runTest {
        // Given
        val metric = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
        
        // When
        val id = healthMetricDao.insert(metric)
        val retrieved = healthMetricDao.getById(id)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(testUserId, retrieved.userId)
        assertEquals(metric.metricType, retrieved.metricType)
        assertEquals(metric.value, retrieved.value)
    }
    
    @Test
    fun `insert multiple metrics`() = runTest {
        // Given
        val metrics = listOf(
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "heart_rate"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "steps"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "calories")
        )
        
        // When
        healthMetricDao.insertAll(metrics)
        val allMetrics = healthMetricDao.getAllByUser(testUserId)
        
        // Then
        assertEquals(3, allMetrics.size)
        assertTrue(allMetrics.map { it.metricType }.containsAll(listOf("heart_rate", "steps", "calories")))
    }
    
    @Test
    fun `get metrics by type`() = runTest {
        // Given
        val heartRateMetrics = (1..3).map {
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "heart_rate",
                value = 140f + it,
                timestamp = System.currentTimeMillis() - (it * 1000L)
            )
        }
        val stepsMetrics = (1..2).map {
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "steps",
                value = 1000f * it
            )
        }
        healthMetricDao.insertAll(heartRateMetrics + stepsMetrics)
        
        // When
        val heartRateOnly = healthMetricDao.getByType(testUserId, "heart_rate")
        
        // Then
        assertEquals(3, heartRateOnly.size)
        assertTrue(heartRateOnly.all { it.metricType == "heart_rate" })
    }
    
    @Test
    fun `get latest metric by type`() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val metrics = listOf(
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "heart_rate",
                value = 140f,
                timestamp = now - 3000L
            ),
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "heart_rate",
                value = 150f,
                timestamp = now - 1000L // Most recent
            ),
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "heart_rate",
                value = 145f,
                timestamp = now - 2000L
            )
        )
        healthMetricDao.insertAll(metrics)
        
        // When
        val latest = healthMetricDao.getLatestByType(testUserId, "heart_rate")
        
        // Then
        assertNotNull(latest)
        assertEquals(150f, latest.value)
    }
    
    @Test
    fun `get metrics in time range`() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val metrics = (0..4).map { hour ->
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                timestamp = now - (hour * 3600000L) // Each hour back
            )
        }
        healthMetricDao.insertAll(metrics)
        
        // When
        val lastTwoHours = healthMetricDao.getInTimeRange(
            userId = testUserId,
            startTime = now - (2 * 3600000L),
            endTime = now
        )
        
        // Then
        assertEquals(3, lastTwoHours.size) // Metrics from 0, 1, 2 hours ago
    }
    
    @Test
    fun `observe metrics with Flow`() = runTest {
        // Given
        val metric1 = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
        healthMetricDao.insert(metric1)
        
        // When
        val flow = healthMetricDao.observeByType(testUserId, "heart_rate")
        val firstEmission = flow.first()
        
        // Add another metric
        val metric2 = TestDataFactory.createHealthMetricCacheEntity(
            userId = testUserId,
            metricType = "heart_rate",
            value = 160f
        )
        healthMetricDao.insert(metric2)
        
        val secondEmission = flow.first()
        
        // Then
        assertEquals(1, firstEmission.size)
        assertEquals(2, secondEmission.size)
    }
    
    @Test
    fun `update sync status`() = runTest {
        // Given
        val metric = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
        val id = healthMetricDao.insert(metric)
        assertNull(healthMetricDao.getById(id)?.syncedAt)
        
        // When
        val syncTime = System.currentTimeMillis()
        healthMetricDao.updateSyncStatus(id, syncTime)
        
        // Then
        val updated = healthMetricDao.getById(id)
        assertNotNull(updated?.syncedAt)
        assertEquals(syncTime, updated?.syncedAt)
    }
    
    @Test
    fun `get unsynced metrics`() = runTest {
        // Given
        val syncedMetric = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(syncedAt = System.currentTimeMillis())
        val unsyncedMetric1 = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(syncedAt = null)
        val unsyncedMetric2 = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(syncedAt = null)
        
        healthMetricDao.insert(syncedMetric)
        val unsynced1Id = healthMetricDao.insert(unsyncedMetric1)
        val unsynced2Id = healthMetricDao.insert(unsyncedMetric2)
        
        // When
        val unsynced = healthMetricDao.getUnsynced(testUserId)
        
        // Then
        assertEquals(2, unsynced.size)
        assertTrue(unsynced.all { it.syncedAt == null })
    }
    
    @Test
    fun `delete expired metrics`() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val validMetric = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(expiresAt = now + 3600000L) // Expires in 1 hour
        val expiredMetric1 = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(expiresAt = now - 1000L) // Expired
        val expiredMetric2 = TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
            .copy(expiresAt = now - 2000L) // Expired
        
        healthMetricDao.insertAll(listOf(validMetric, expiredMetric1, expiredMetric2))
        
        // When
        val deletedCount = healthMetricDao.deleteExpired(now)
        val remaining = healthMetricDao.getAllByUser(testUserId)
        
        // Then
        assertEquals(2, deletedCount)
        assertEquals(1, remaining.size)
        assertTrue(remaining.first().expiresAt!! > now)
    }
    
    @Test
    fun `delete metrics older than timestamp`() = runTest {
        // Given
        val cutoffTime = System.currentTimeMillis()
        val oldMetrics = (1..3).map {
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                timestamp = cutoffTime - (it * 1000L)
            )
        }
        val newMetrics = (1..2).map {
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                timestamp = cutoffTime + (it * 1000L)
            )
        }
        healthMetricDao.insertAll(oldMetrics + newMetrics)
        
        // When
        val deletedCount = healthMetricDao.deleteOlderThan(cutoffTime)
        val remaining = healthMetricDao.getAllByUser(testUserId)
        
        // Then
        assertEquals(3, deletedCount)
        assertEquals(2, remaining.size)
        assertTrue(remaining.all { it.timestamp > cutoffTime })
    }
    
    @Test
    fun `get aggregated metrics`() = runTest {
        // Given
        val heartRates = listOf(140f, 150f, 145f, 160f, 155f).map { value ->
            TestDataFactory.createHealthMetricCacheEntity(
                userId = testUserId,
                metricType = "heart_rate",
                value = value
            )
        }
        healthMetricDao.insertAll(heartRates)
        
        // When
        val average = healthMetricDao.getAverageValue(testUserId, "heart_rate")
        val max = healthMetricDao.getMaxValue(testUserId, "heart_rate")
        val min = healthMetricDao.getMinValue(testUserId, "heart_rate")
        val count = healthMetricDao.getCount(testUserId, "heart_rate")
        
        // Then
        assertNotNull(average)
        assertEquals(150f, average)
        assertNotNull(max)
        assertEquals(160f, max)
        assertNotNull(min)
        assertEquals(140f, min)
        assertEquals(5, count)
    }
    
    @Test
    fun `get distinct metric types`() = runTest {
        // Given
        val metrics = listOf(
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "heart_rate"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "heart_rate"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "steps"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "calories"),
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId, metricType = "steps")
        )
        healthMetricDao.insertAll(metrics)
        
        // When
        val types = healthMetricDao.getDistinctTypes(testUserId)
        
        // Then
        assertEquals(3, types.size)
        assertTrue(types.containsAll(listOf("heart_rate", "steps", "calories")))
    }
    
    @Test
    fun `batch update sync status`() = runTest {
        // Given
        val metrics = (1..5).map {
            TestDataFactory.createHealthMetricCacheEntity(userId = testUserId)
        }
        val ids = metrics.map { healthMetricDao.insert(it) }
        
        // When
        val syncTime = System.currentTimeMillis()
        healthMetricDao.batchUpdateSyncStatus(ids, syncTime)
        
        // Then
        val updated = ids.map { healthMetricDao.getById(it) }
        assertTrue(updated.all { it?.syncedAt == syncTime })
    }
    
    @Test
    fun `delete by user ID`() = runTest {
        // Given
        val user1Metrics = (1..3).map {
            TestDataFactory.createHealthMetricCacheEntity(userId = "user1")
        }
        val user2Metrics = (1..2).map {
            TestDataFactory.createHealthMetricCacheEntity(userId = "user2")
        }
        healthMetricDao.insertAll(user1Metrics + user2Metrics)
        
        // When
        healthMetricDao.deleteByUser("user1")
        
        // Then
        val user1Remaining = healthMetricDao.getAllByUser("user1")
        val user2Remaining = healthMetricDao.getAllByUser("user2")
        assertTrue(user1Remaining.isEmpty())
        assertEquals(2, user2Remaining.size)
    }
    
    @Test
    fun `upsert updates existing metric with same timestamp and type`() = runTest {
        // Given
        val timestamp = System.currentTimeMillis()
        val original = TestDataFactory.createHealthMetricCacheEntity(
            userId = testUserId,
            metricType = "heart_rate",
            value = 140f,
            timestamp = timestamp
        )
        val id = healthMetricDao.insert(original)
        
        // When
        val updated = original.copy(
            id = 0, // New ID, but same timestamp and type
            value = 150f
        )
        healthMetricDao.upsert(updated)
        
        // Then
        val allMetrics = healthMetricDao.getByType(testUserId, "heart_rate")
        assertEquals(1, allMetrics.size) // Should replace, not add
        assertEquals(150f, allMetrics.first().value)
    }
}