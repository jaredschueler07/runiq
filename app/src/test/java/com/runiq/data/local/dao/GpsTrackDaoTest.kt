package com.runiq.data.local.dao

import app.cash.turbine.test
import com.runiq.base.BaseDaoTest
import com.runiq.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for GpsTrackDao.
 * Tests GPS track point database operations.
 */
@ExperimentalCoroutinesApi
class GpsTrackDaoTest : BaseDaoTest() {
    
    private lateinit var gpsTrackDao: GpsTrackDao
    
    override fun setUp() {
        super.setUp()
        gpsTrackDao = database.gpsTrackDao()
    }
    
    @Test
    fun `insertAll should save all track points`() = runTest {
        // Given
        val sessionId = "session123"
        val trackPoints = TestDataFactory.createGpsTrack(sessionId = sessionId, pointCount = 5)
        
        // When
        val insertIds = gpsTrackDao.insertAll(trackPoints)
        
        // Then
        assertEquals("Should return 5 insert IDs", 5, insertIds.size)
        assertTrue("All insert IDs should be positive", insertIds.all { it > 0 })
        
        val retrievedPoints = gpsTrackDao.getTrackPoints(sessionId)
        assertEquals("Should retrieve all 5 points", 5, retrievedPoints.size)
    }
    
    @Test
    fun `observeTrackPoints should emit points in chronological order`() = runTest {
        // Given
        val sessionId = "session123"
        val trackPoints = TestDataFactory.createGpsTrack(sessionId = sessionId, pointCount = 3)
        
        // When
        gpsTrackDao.insertAll(trackPoints)
        
        // Then
        gpsTrackDao.observeTrackPoints(sessionId).test {
            val points = awaitItem()
            assertEquals("Should have 3 points", 3, points.size)
            
            // Verify chronological order
            for (i in 0 until points.size - 1) {
                assertTrue(
                    "Points should be in chronological order",
                    points[i].timestamp <= points[i + 1].timestamp
                )
            }
        }
    }
    
    @Test
    fun `getLastTrackPoint should return most recent point`() = runTest {
        // Given
        val sessionId = "session123"
        val baseTime = System.currentTimeMillis()
        val trackPoints = listOf(
            TestDataFactory.createGpsTrackPoint(sessionId = sessionId, timestamp = baseTime),
            TestDataFactory.createGpsTrackPoint(sessionId = sessionId, timestamp = baseTime + 10000),
            TestDataFactory.createGpsTrackPoint(sessionId = sessionId, timestamp = baseTime + 20000)
        )
        
        // When
        gpsTrackDao.insertAll(trackPoints)
        val lastPoint = gpsTrackDao.getLastTrackPoint(sessionId)
        
        // Then
        assertNotNull("Should find last track point", lastPoint)
        assertEquals("Should return the most recent point", baseTime + 20000, lastPoint?.timestamp)
    }
    
    @Test
    fun `deleteBySessionId should remove all points for session`() = runTest {
        // Given
        val sessionId1 = "session123"
        val sessionId2 = "session456"
        val points1 = TestDataFactory.createGpsTrack(sessionId = sessionId1, pointCount = 3)
        val points2 = TestDataFactory.createGpsTrack(sessionId = sessionId2, pointCount = 2)
        
        gpsTrackDao.insertAll(points1 + points2)
        
        // When
        val deleteCount = gpsTrackDao.deleteBySessionId(sessionId1)
        
        // Then
        assertEquals("Should delete 3 points", 3, deleteCount)
        
        val remainingPoints1 = gpsTrackDao.getTrackPoints(sessionId1)
        val remainingPoints2 = gpsTrackDao.getTrackPoints(sessionId2)
        
        assertEquals("Session 1 should have no points", 0, remainingPoints1.size)
        assertEquals("Session 2 should still have 2 points", 2, remainingPoints2.size)
    }
    
    @Test
    fun `getPointCount should return correct count`() = runTest {
        // Given
        val sessionId = "session123"
        val trackPoints = TestDataFactory.createGpsTrack(sessionId = sessionId, pointCount = 7)
        
        // When
        gpsTrackDao.insertAll(trackPoints)
        val count = gpsTrackDao.getPointCount(sessionId)
        
        // Then
        assertEquals("Should count all inserted points", 7, count)
    }
    
    @Test
    fun `getTrackPoints should return empty list for non-existent session`() = runTest {
        // When
        val points = gpsTrackDao.getTrackPoints("non_existent_session")
        
        // Then
        assertTrue("Should return empty list", points.isEmpty())
    }
    
    @Test
    fun `getLastTrackPoint should return null for non-existent session`() = runTest {
        // When
        val lastPoint = gpsTrackDao.getLastTrackPoint("non_existent_session")
        
        // Then
        assertNull("Should return null for non-existent session", lastPoint)
    }
}