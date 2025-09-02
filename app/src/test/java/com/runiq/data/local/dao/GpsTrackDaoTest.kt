package com.runiq.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GpsTrackDaoTest {
    
    private lateinit var database: RunIQDatabase
    private lateinit var gpsTrackDao: GpsTrackDao
    private lateinit var runSessionDao: RunSessionDao
    
    private val testSessionId = "test_session_123"
    
    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        ).allowMainThreadQueries().build()
        
        gpsTrackDao = database.gpsTrackDao()
        runSessionDao = database.runSessionDao()
        
        // Insert a test session for foreign key constraints
        runTest {
            val session = RunSessionEntity(
                sessionId = testSessionId,
                userId = "test_user",
                startTime = System.currentTimeMillis(),
                workoutType = WorkoutType.EASY_RUN,
                coachId = "test_coach"
            )
            runSessionDao.insert(session)
        }
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetGpsPoints() = runTest {
        val points = createTestGpsTrack(testSessionId, 5)
        
        val insertedIds = gpsTrackDao.insertAll(points)
        assertEquals("Should insert all points", 5, insertedIds.size)
        assertTrue("All IDs should be positive", insertedIds.all { it > 0 })
        
        val retrieved = gpsTrackDao.getBySession(testSessionId)
        assertEquals("Should retrieve all points", 5, retrieved.size)
        
        // Check ordering by sequence number
        for (i in 0 until 5) {
            assertEquals("Sequence numbers should match", i, retrieved[i].sequenceNumber)
        }
    }
    
    @Test
    fun observeGpsTrackUpdates() = runTest {
        val initialPoints = createTestGpsTrack(testSessionId, 3)
        gpsTrackDao.insertAll(initialPoints)
        
        val flow = gpsTrackDao.observeBySession(testSessionId)
        val initialValue = flow.first()
        
        assertEquals("Initial value should have 3 points", 3, initialValue.size)
        
        // Add more points
        val additionalPoints = createTestGpsTrack(testSessionId, 2, startSequence = 3)
        gpsTrackDao.insertAll(additionalPoints)
        
        val updatedValue = flow.first()
        assertEquals("Updated value should have 5 points", 5, updatedValue.size)
    }
    
    @Test
    fun getByTimeRange() = runTest {
        val baseTime = System.currentTimeMillis()
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, timestamp = baseTime),
            createTestGpsPoint(testSessionId, 1, timestamp = baseTime + 1000),
            createTestGpsPoint(testSessionId, 2, timestamp = baseTime + 2000),
            createTestGpsPoint(testSessionId, 3, timestamp = baseTime + 3000),
            createTestGpsPoint(testSessionId, 4, timestamp = baseTime + 4000)
        )
        
        gpsTrackDao.insertAll(points)
        
        val rangePoints = gpsTrackDao.getByTimeRange(
            testSessionId, 
            baseTime + 1500, 
            baseTime + 3500
        )
        
        assertEquals("Should have 2 points in range", 2, rangePoints.size)
        assertEquals("First point should be sequence 2", 2, rangePoints[0].sequenceNumber)
        assertEquals("Second point should be sequence 3", 3, rangePoints[1].sequenceNumber)
    }
    
    @Test
    fun getByDistanceRange() = runTest {
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, distance = 0f),
            createTestGpsPoint(testSessionId, 1, distance = 250f),
            createTestGpsPoint(testSessionId, 2, distance = 500f),
            createTestGpsPoint(testSessionId, 3, distance = 750f),
            createTestGpsPoint(testSessionId, 4, distance = 1000f)
        )
        
        gpsTrackDao.insertAll(points)
        
        val rangePoints = gpsTrackDao.getByDistanceRange(testSessionId, 200f, 800f)
        
        assertEquals("Should have 3 points in range", 3, rangePoints.size)
        assertTrue("Should include points with distances 250, 500, 750", 
            rangePoints.map { it.distance }.containsAll(listOf(250f, 500f, 750f)))
    }
    
    @Test
    fun getSampledPoints() = runTest {
        val points = createTestGpsTrack(testSessionId, 10)
        gpsTrackDao.insertAll(points)
        
        val sampledPoints = gpsTrackDao.getSampledPoints(testSessionId, 3)
        
        assertEquals("Should have 4 sampled points (0, 3, 6, 9)", 4, sampledPoints.size)
        val expectedSequences = listOf(0, 3, 6, 9)
        val actualSequences = sampledPoints.map { it.sequenceNumber }
        assertEquals("Sampled sequences should match", expectedSequences, actualSequences)
    }
    
    @Test
    fun getAccuratePoints() = runTest {
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, accuracy = 5f),
            createTestGpsPoint(testSessionId, 1, accuracy = 15f),
            createTestGpsPoint(testSessionId, 2, accuracy = 8f),
            createTestGpsPoint(testSessionId, 3, accuracy = 25f),
            createTestGpsPoint(testSessionId, 4, accuracy = 3f)
        )
        
        gpsTrackDao.insertAll(points)
        
        val accuratePoints = gpsTrackDao.getAccuratePoints(testSessionId, 10f)
        
        assertEquals("Should have 3 accurate points", 3, accuratePoints.size)
        assertTrue("All points should have accuracy <= 10", 
            accuratePoints.all { it.accuracy != null && it.accuracy <= 10f })
    }
    
    @Test
    fun pausePointOperations() = runTest {
        val points = createTestGpsTrack(testSessionId, 5)
        gpsTrackDao.insertAll(points)
        
        // Mark some points as pause points
        val pointsToUpdate = gpsTrackDao.getBySession(testSessionId)
        gpsTrackDao.updatePauseStatus(pointsToUpdate[1].id, true)
        gpsTrackDao.updatePauseStatus(pointsToUpdate[3].id, true)
        
        val pausePoints = gpsTrackDao.getPausePoints(testSessionId)
        
        assertEquals("Should have 2 pause points", 2, pausePoints.size)
        assertTrue("All pause points should have isPausePoint = true", 
            pausePoints.all { it.isPausePoint })
    }
    
    @Test
    fun statisticsQueries() = runTest {
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, distance = 0f, speed = 0f, pace = 0f),
            createTestGpsPoint(testSessionId, 1, distance = 250f, speed = 3f, pace = 5.5f),
            createTestGpsPoint(testSessionId, 2, distance = 500f, speed = 3.5f, pace = 5.0f),
            createTestGpsPoint(testSessionId, 3, distance = 750f, speed = 4f, pace = 4.5f),
            createTestGpsPoint(testSessionId, 4, distance = 1000f, speed = 3.2f, pace = 5.2f)
        )
        
        gpsTrackDao.insertAll(points)
        
        val pointCount = gpsTrackDao.getPointCount(testSessionId)
        assertEquals("Should have 5 points", 5, pointCount)
        
        val totalDistance = gpsTrackDao.getTotalDistance(testSessionId)
        assertEquals("Total distance should be 1000m", 1000f, totalDistance!!, 0.01f)
        
        val averageSpeed = gpsTrackDao.getAverageSpeed(testSessionId)
        // Average of 3, 3.5, 4, 3.2 (excluding 0)
        assertEquals("Average speed should be ~3.4", 3.425f, averageSpeed!!, 0.1f)
        
        val maxSpeed = gpsTrackDao.getMaxSpeed(testSessionId)
        assertEquals("Max speed should be 4.0", 4.0f, maxSpeed!!, 0.01f)
        
        val bestPace = gpsTrackDao.getBestPace(testSessionId)
        assertEquals("Best pace should be 4.5", 4.5f, bestPace!!, 0.01f)
    }
    
    @Test
    fun elevationQueries() = runTest {
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, altitude = 100.0),
            createTestGpsPoint(testSessionId, 1, altitude = 105.0),
            createTestGpsPoint(testSessionId, 2, altitude = 95.0),
            createTestGpsPoint(testSessionId, 3, altitude = 110.0),
            createTestGpsPoint(testSessionId, 4, altitude = 98.0)
        )
        
        gpsTrackDao.insertAll(points)
        
        val elevationRange = gpsTrackDao.getElevationRange(testSessionId)
        assertNotNull("Elevation range should not be null", elevationRange)
        assertEquals("Min altitude should be 95.0", 95.0, elevationRange!!.minAltitude, 0.01)
        assertEquals("Max altitude should be 110.0", 110.0, elevationRange.maxAltitude, 0.01)
        
        val pointsWithElevation = gpsTrackDao.getPointsWithElevation(testSessionId)
        assertEquals("Should have 5 points with elevation", 5, pointsWithElevation.size)
        assertTrue("All points should have altitude", 
            pointsWithElevation.all { it.altitude != null })
    }
    
    @Test
    fun heartRateQueries() = runTest {
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, heartRate = 120),
            createTestGpsPoint(testSessionId, 1, heartRate = 135),
            createTestGpsPoint(testSessionId, 2, heartRate = 145),
            createTestGpsPoint(testSessionId, 3, heartRate = 140),
            createTestGpsPoint(testSessionId, 4, heartRate = 130)
        )
        
        gpsTrackDao.insertAll(points)
        
        val averageHr = gpsTrackDao.getAverageHeartRate(testSessionId)
        assertEquals("Average HR should be 134", 134f, averageHr!!, 1f)
        
        val hrRange = gpsTrackDao.getHeartRateRange(testSessionId)
        assertNotNull("HR range should not be null", hrRange)
        assertEquals("Min HR should be 120", 120, hrRange!!.minHr)
        assertEquals("Max HR should be 145", 145, hrRange.maxHr)
    }
    
    @Test
    fun trackAnalysisQueries() = runTest {
        val points = createTestGpsTrack(testSessionId, 10)
        gpsTrackDao.insertAll(points)
        
        val segment = gpsTrackDao.getSegment(testSessionId, 2, 5)
        assertEquals("Segment should have 4 points", 4, segment.size)
        assertEquals("First point should be sequence 2", 2, segment[0].sequenceNumber)
        assertEquals("Last point should be sequence 5", 5, segment[3].sequenceNumber)
        
        val lastPoint = gpsTrackDao.getLastPoint(testSessionId)
        assertNotNull("Last point should not be null", lastPoint)
        assertEquals("Last point should be sequence 9", 9, lastPoint!!.sequenceNumber)
        
        val firstPoint = gpsTrackDao.getFirstPoint(testSessionId)
        assertNotNull("First point should not be null", firstPoint)
        assertEquals("First point should be sequence 0", 0, firstPoint!!.sequenceNumber)
        
        val lastNPoints = gpsTrackDao.getLastNPoints(testSessionId, 3)
        assertEquals("Should have 3 points", 3, lastNPoints.size)
        // Points should be in descending order by sequence
        assertEquals("First should be sequence 9", 9, lastNPoints[0].sequenceNumber)
        assertEquals("Second should be sequence 8", 8, lastNPoints[1].sequenceNumber)
        assertEquals("Third should be sequence 7", 7, lastNPoints[2].sequenceNumber)
    }
    
    @Test
    fun replaceTrackPoints() = runTest {
        val initialPoints = createTestGpsTrack(testSessionId, 5)
        gpsTrackDao.insertAll(initialPoints)
        
        val initialCount = gpsTrackDao.getPointCount(testSessionId)
        assertEquals("Should have 5 initial points", 5, initialCount)
        
        val newPoints = createTestGpsTrack(testSessionId, 3)
        gpsTrackDao.replaceTrackPoints(testSessionId, newPoints)
        
        val finalCount = gpsTrackDao.getPointCount(testSessionId)
        assertEquals("Should have 3 final points", 3, finalCount)
        
        val retrievedPoints = gpsTrackDao.getBySession(testSessionId)
        assertEquals("Retrieved points should match new points count", 3, retrievedPoints.size)
    }
    
    @Test
    fun cleanupOperations() = runTest {
        val currentTime = System.currentTimeMillis()
        val oldTime = currentTime - (24 * 60 * 60 * 1000L) // 1 day ago
        
        val points = listOf(
            createTestGpsPoint(testSessionId, 0, timestamp = currentTime, accuracy = 5f),
            createTestGpsPoint(testSessionId, 1, timestamp = oldTime, accuracy = 15f),
            createTestGpsPoint(testSessionId, 2, timestamp = currentTime, accuracy = 25f)
        )
        
        gpsTrackDao.insertAll(points)
        
        val deletedOld = gpsTrackDao.deleteOlderThan(currentTime - 1000)
        assertEquals("Should delete 1 old point", 1, deletedOld)
        
        val deletedInaccurate = gpsTrackDao.deleteInaccuratePoints(testSessionId, 20f)
        assertEquals("Should delete 1 inaccurate point", 1, deletedInaccurate)
        
        val remainingPoints = gpsTrackDao.getBySession(testSessionId)
        assertEquals("Should have 1 remaining point", 1, remainingPoints.size)
        assertEquals("Remaining point should have good accuracy", 5f, remainingPoints[0].accuracy!!, 0.01f)
    }
    
    @Test
    fun foreignKeyConstraint() = runTest {
        // This test verifies that GPS points are deleted when the parent session is deleted
        val points = createTestGpsTrack(testSessionId, 3)
        gpsTrackDao.insertAll(points)
        
        val initialCount = gpsTrackDao.getPointCount(testSessionId)
        assertEquals("Should have 3 initial points", 3, initialCount)
        
        // Delete the parent session
        runSessionDao.deleteById(testSessionId)
        
        val finalCount = gpsTrackDao.getPointCount(testSessionId)
        assertEquals("Should have 0 points after session deletion", 0, finalCount)
    }
    
    private fun createTestGpsTrack(
        sessionId: String, 
        count: Int, 
        startSequence: Int = 0
    ): List<GpsTrackPointEntity> {
        return (0 until count).map { i ->
            createTestGpsPoint(sessionId, startSequence + i)
        }
    }
    
    private fun createTestGpsPoint(
        sessionId: String,
        sequenceNumber: Int,
        latitude: Double = 37.7749 + (sequenceNumber * 0.001),
        longitude: Double = -122.4194 + (sequenceNumber * 0.001),
        altitude: Double? = null,
        timestamp: Long = System.currentTimeMillis() + (sequenceNumber * 1000L),
        accuracy: Float? = null,
        speed: Float? = null,
        bearing: Float? = null,
        distance: Float = sequenceNumber * 100f,
        pace: Float? = null,
        heartRate: Int? = null
    ): GpsTrackPointEntity {
        return GpsTrackPointEntity(
            sessionId = sessionId,
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            timestamp = timestamp,
            accuracy = accuracy,
            speed = speed,
            bearing = bearing,
            distance = distance,
            sequenceNumber = sequenceNumber,
            pace = pace,
            heartRate = heartRate
        )
    }
}