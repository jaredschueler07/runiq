package com.runiq.data.local.dao

import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.testing.base.BaseDaoTest
import com.runiq.testing.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Comprehensive unit tests for GpsTrackDao
 */
@ExperimentalCoroutinesApi
class GpsTrackDaoTest : BaseDaoTest() {
    
    private lateinit var gpsTrackDao: GpsTrackDao
    private lateinit var runSessionDao: RunSessionDao
    private val testSessionId = "test-session-123"

    @Before
    fun setupDao() {
        gpsTrackDao = database.gpsTrackDao()
        runSessionDao = database.runSessionDao()

        // Insert a parent session for foreign key constraint
        runTest {
            val session = TestDataFactory.createRunSessionEntity(sessionId = testSessionId)
            runSessionDao.insert(session)
        }
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
    
    @Test
    fun `insert and retrieve single GPS point`() = runTest {
        // Given
        val point = TestDataFactory.createGpsTrackPointEntity(sessionId = testSessionId)

        // When
        val id = gpsTrackDao.insert(point)
        val retrieved = gpsTrackDao.getById(id)

        // Then
        assertNotNull(retrieved)
        assertEquals(testSessionId, retrieved.sessionId)
        assertEquals(point.latitude, retrieved.latitude)
        assertEquals(point.longitude, retrieved.longitude)
    }

    @Test
    fun `insert multiple GPS points and retrieve in order`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 10)

        // When
        gpsTrackDao.insertAll(points)
        val retrieved = gpsTrackDao.getTrackPoints(testSessionId)

        // Then
        assertEquals(10, retrieved.size)
        assertEquals(points.map { it.sequenceNumber }, retrieved.map { it.sequenceNumber })
        assertTrue(retrieved.zipWithNext().all { (a, b) -> a.sequenceNumber < b.sequenceNumber })
    }

    @Test
    fun `get last and first points of track`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 5)
        gpsTrackDao.insertAll(points)

        // When
        val firstPoint = gpsTrackDao.getFirstPoint(testSessionId)
        val lastPoint = gpsTrackDao.getLastPoint(testSessionId)

        // Then
        assertNotNull(firstPoint)
        assertNotNull(lastPoint)
        assertEquals(0, firstPoint.sequenceNumber)
        assertEquals(4, lastPoint.sequenceNumber)
    }

    @Test
    fun `observe track points with Flow`() = runTest {
        // Given
        val initialPoints = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 3)
        gpsTrackDao.insertAll(initialPoints)

        // When
        val flow = gpsTrackDao.observeTrackPoints(testSessionId)
        val firstEmission = flow.first()

        // Add more points
        val newPoint = TestDataFactory.createGpsTrackPointEntity(
            sessionId = testSessionId,
            sequenceNumber = 3
        )
        gpsTrackDao.insert(newPoint)

        val secondEmission = flow.first()

        // Then
        assertEquals(3, firstEmission.size)
        assertEquals(4, secondEmission.size)
    }

    @Test
    fun `get active track points excluding pauses`() = runTest {
        // Given
        val activePoints = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 3)
        val pausePoint = TestDataFactory.createGpsTrackPointEntity(
            sessionId = testSessionId,
            sequenceNumber = 3
        ).copy(isPausePoint = true)

        gpsTrackDao.insertAll(activePoints + pausePoint)

        // When
        val activeOnly = gpsTrackDao.getActiveTrackPoints(testSessionId)

        // Then
        assertEquals(3, activeOnly.size)
        assertTrue(activeOnly.none { it.isPausePoint })
    }

    @Test
    fun `get points within time range`() = runTest {
        // Given
        val startTime = System.currentTimeMillis()
        val points = (0..4).map { index ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                timestamp = startTime + (index * 1000L),
                sequenceNumber = index
            )
        }
        gpsTrackDao.insertAll(points)

        // When
        val rangePoints = gpsTrackDao.getPointsInTimeRange(
            testSessionId,
            startTime + 1000L,
            startTime + 3000L
        )

        // Then
        assertEquals(3, rangePoints.size) // Points at 1, 2, 3 seconds
    }

    @Test
    fun `get track points with pagination`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 20)
        gpsTrackDao.insertAll(points)

        // When
        val page1 = gpsTrackDao.getTrackPointsPaged(testSessionId, limit = 5, offset = 0)
        val page2 = gpsTrackDao.getTrackPointsPaged(testSessionId, limit = 5, offset = 5)
        val page3 = gpsTrackDao.getTrackPointsPaged(testSessionId, limit = 5, offset = 10)

        // Then
        assertEquals(5, page1.size)
        assertEquals(5, page2.size)
        assertEquals(5, page3.size)
        assertEquals(0, page1.first().sequenceNumber)
        assertEquals(5, page2.first().sequenceNumber)
        assertEquals(10, page3.first().sequenceNumber)
    }

    @Test
    fun `calculate statistics for track`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 10)
        gpsTrackDao.insertAll(points)

        // When
        val pointCount = gpsTrackDao.getPointCount(testSessionId)
        val totalDistance = gpsTrackDao.getTotalDistance(testSessionId)
        val avgSpeed = gpsTrackDao.getAverageSpeed(testSessionId)
        val maxSpeed = gpsTrackDao.getMaxSpeed(testSessionId)

        // Then
        assertEquals(10, pointCount)
        assertNotNull(totalDistance)
        assertTrue(totalDistance > 0f)
        assertNotNull(avgSpeed)
        assertNotNull(maxSpeed)
        assertTrue(maxSpeed >= avgSpeed)
    }

    @Test
    fun `get altitude statistics`() = runTest {
        // Given
        val points = listOf(10.0, 20.0, 15.0, 25.0, 5.0).mapIndexed { index, altitude ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                altitude = altitude
            )
        }
        gpsTrackDao.insertAll(points)

        // When
        val maxAltitude = gpsTrackDao.getMaxAltitude(testSessionId)
        val minAltitude = gpsTrackDao.getMinAltitude(testSessionId)

        // Then
        assertEquals(25.0, maxAltitude)
        assertEquals(5.0, minAltitude)
    }

    @Test
    fun `get heart rate statistics`() = runTest {
        // Given
        val points = listOf(140, 150, 145, 160, 155).mapIndexed { index, hr ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                heartRate = hr
            )
        }
        gpsTrackDao.insertAll(points)

        // When
        val avgHeartRate = gpsTrackDao.getAverageHeartRate(testSessionId)

        // Then
        assertNotNull(avgHeartRate)
        assertEquals(150f, avgHeartRate)
    }

    @Test
    fun `get segment between sequence numbers`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 10)
        gpsTrackDao.insertAll(points)

        // When
        val segment = gpsTrackDao.getSegment(testSessionId, startSequence = 3, endSequence = 6)

        // Then
        assertEquals(4, segment.size) // Points 3, 4, 5, 6
        assertEquals(3, segment.first().sequenceNumber)
        assertEquals(6, segment.last().sequenceNumber)
    }

    @Test
    fun `get points by distance range`() = runTest {
        // Given
        val points = (0..4).map { index ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                distance = index * 100f // 0m, 100m, 200m, 300m, 400m
            )
        }
        gpsTrackDao.insertAll(points)

        // When
        val distancePoints = gpsTrackDao.getPointsByDistance(
            testSessionId,
            startDistance = 100f,
            endDistance = 300f
        )

        // Then
        assertEquals(3, distancePoints.size)
    }

    @Test
    fun `get fastest segments by pace`() = runTest {
        // Given
        val points = listOf(5.5f, 6.0f, 5.0f, 7.0f, 5.2f).mapIndexed { index, pace ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                pace = pace
            )
        }
        gpsTrackDao.insertAll(points)

        // When
        val fastestSegments = gpsTrackDao.getFastestSegments(
            testSessionId,
            maxPace = 6.0f,
            limit = 3
        )

        // Then
        assertEquals(3, fastestSegments.size)
        assertEquals(5.0f, fastestSegments[0].pace) // Fastest
        assertEquals(5.2f, fastestSegments[1].pace)
        assertEquals(5.5f, fastestSegments[2].pace)
    }

    @Test
    fun `get average pace excluding pauses`() = runTest {
        // Given
        val activePoints = listOf(6.0f, 6.2f, 5.8f).mapIndexed { index, pace ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                pace = pace,
                isPausePoint = false
            )
        }
        val pausePoint = TestDataFactory.createGpsTrackPointEntity(
            sessionId = testSessionId,
            sequenceNumber = 3,
            pace = 0f,
            isPausePoint = true
        )

        gpsTrackDao.insertAll(activePoints + pausePoint)

        // When
        val avgPace = gpsTrackDao.getAveragePace(testSessionId)

        // Then
        assertNotNull(avgPace)
        assertEquals(6.0f, avgPace) // Average of 6.0, 6.2, 5.8
    }

    @Test
    fun `delete points by session ID`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 5)
        gpsTrackDao.insertAll(points)

        // When
        gpsTrackDao.deleteBySessionId(testSessionId)
        val remaining = gpsTrackDao.getTrackPoints(testSessionId)

        // Then
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun `delete low accuracy points`() = runTest {
        // Given
        val goodPoints = (0..2).map { index ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                accuracy = 10f
            )
        }
        val badPoints = (3..4).map { index ->
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = index,
                accuracy = 60f
            )
        }
        gpsTrackDao.insertAll(goodPoints + badPoints)

        // When
        val deletedCount = gpsTrackDao.deleteLowAccuracyPoints(testSessionId, maxAccuracy = 50f)
        val remaining = gpsTrackDao.getTrackPoints(testSessionId)

        // Then
        assertEquals(2, deletedCount)
        assertEquals(3, remaining.size)
        assertTrue(remaining.all { it.accuracy!! <= 50f })
    }

    @Test
    fun `save track batch with automatic sequencing`() = runTest {
        // Given
        val batch1 = (0..2).map {
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = -1 // Will be replaced
            )
        }
        val batch2 = (0..1).map {
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                sequenceNumber = -1 // Will be replaced
            )
        }

        // When
        gpsTrackDao.saveTrackBatch(batch1)
        gpsTrackDao.saveTrackBatch(batch2)
        val allPoints = gpsTrackDao.getTrackPoints(testSessionId)

        // Then
        assertEquals(5, allPoints.size)
        assertEquals(listOf(0, 1, 2, 3, 4), allPoints.map { it.sequenceNumber })
    }

    @Test
    fun `get simplified track with nth point sampling`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 20)
        gpsTrackDao.insertAll(points)

        // When
        val simplified = gpsTrackDao.getSimplifiedTrack(testSessionId, nth = 5)

        // Then
        assertEquals(4, simplified.size) // Points at indices 0, 5, 10, 15
        assertEquals(listOf(0, 5, 10, 15), simplified.map { it.sequenceNumber })
    }

    @Test
    fun `cascade delete when parent session is deleted`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList(testSessionId, pointCount = 5)
        gpsTrackDao.insertAll(points)

        // When
        runSessionDao.deleteById(testSessionId)
        val remainingPoints = gpsTrackDao.getTrackPoints(testSessionId)

        // Then
        assertTrue(remainingPoints.isEmpty())
    }
}