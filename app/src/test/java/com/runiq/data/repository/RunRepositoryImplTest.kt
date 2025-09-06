package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import com.runiq.testing.base.BaseRepositoryTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for RunRepositoryImpl
 */
class RunRepositoryImplTest : BaseRepositoryTest() {
    
    private lateinit var repository: RunRepositoryImpl
    private lateinit var runSessionDao: RunSessionDao
    private lateinit var gpsTrackDao: GpsTrackDao
    
    private val testUserId = "test-user-123"
    private val testSessionId = "test-session-456"
    private val testCoachId = "test-coach-789"
    
    @Before
    fun setup() {
        runSessionDao = mockk()
        gpsTrackDao = mockk()
        repository = RunRepositoryImpl(runSessionDao, gpsTrackDao)
    }
    
    @Test
    fun `startRun creates new session successfully`() = runTest {
        // Given
        val workoutType = WorkoutType.EASY_RUN
        val targetPace = 6.0f
        val capturedSession = slot<RunSessionEntity>()
        
        coEvery { runSessionDao.insert(capture(capturedSession)) } returns 1L
        
        // When
        val result = repository.startRun(testUserId, workoutType, testCoachId, targetPace)
        
        // Then
        assertTrue(result is Result.Success)
        assertNotNull(result.data)
        
        val savedSession = capturedSession.captured
        assertEquals(testUserId, savedSession.userId)
        assertEquals(workoutType, savedSession.workoutType)
        assertEquals(testCoachId, savedSession.coachId)
        assertEquals(targetPace, savedSession.targetPace)
        assertNotNull(savedSession.startTime)
        assertNull(savedSession.endTime)
        
        coVerify(exactly = 1) { runSessionDao.insert(any()) }
    }
    
    @Test
    fun `startRun handles database error`() = runTest {
        // Given
        coEvery { runSessionDao.insert(any()) } throws Exception("Database error")
        
        // When
        val result = repository.startRun(testUserId, WorkoutType.TEMPO_RUN, testCoachId)
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
    }
    
    @Test
    fun `endRun completes session successfully`() = runTest {
        // Given
        val distance = 5000f
        val duration = 1800000L
        val averagePace = 6.0f
        val calories = 350
        val steps = 6500
        
        coEvery { 
            runSessionDao.completeRun(
                testSessionId, any(), distance, duration, averagePace, calories, steps
            )
        } just runs
        
        // When
        val result = repository.endRun(testSessionId, distance, duration, averagePace, calories, steps)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            runSessionDao.completeRun(
                testSessionId, any(), distance, duration, averagePace, calories, steps
            )
        }
    }
    
    @Test
    fun `pauseRun creates pause point`() = runTest {
        // Given
        val lastPoint = TestDataFactory.createGpsTrackPointEntity(
            sessionId = testSessionId,
            sequenceNumber = 10
        )
        
        coEvery { gpsTrackDao.getLastPoint(testSessionId) } returns lastPoint
        coEvery { gpsTrackDao.insert(any()) } returns 1L
        
        // When
        val result = repository.pauseRun(testSessionId)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            gpsTrackDao.getLastPoint(testSessionId)
            gpsTrackDao.insert(withArg { point ->
                assertEquals(testSessionId, point.sessionId)
                assertEquals(11, point.sequenceNumber)
                assertTrue(point.isPausePoint)
            })
        }
    }
    
    @Test
    fun `pauseRun handles no previous points`() = runTest {
        // Given
        coEvery { gpsTrackDao.getLastPoint(testSessionId) } returns null
        
        // When
        val result = repository.pauseRun(testSessionId)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) { gpsTrackDao.getLastPoint(testSessionId) }
        coVerify(exactly = 0) { gpsTrackDao.insert(any()) }
    }
    
    @Test
    fun `getActiveRun returns active session`() = runTest {
        // Given
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            userId = testUserId,
            endTime = null
        )
        
        coEvery { runSessionDao.getActiveSession(testUserId) } returns activeSession
        
        // When
        val result = repository.getActiveRun(testUserId)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(activeSession, result.data)
    }
    
    @Test
    fun `observeActiveRun returns flow of active session`() = runTest {
        // Given
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            userId = testUserId,
            endTime = null
        )
        
        every { runSessionDao.observeActiveSession(testUserId) } returns flowOf(activeSession)
        
        // When
        val result = repository.observeActiveRun(testUserId).first()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(activeSession, result.data)
    }
    
    @Test
    fun `getRunHistory returns paged sessions`() = runTest {
        // Given
        val sessions = TestDataFactory.createRunSessionList(count = 3)
        val limit = 10
        val offset = 0
        
        coEvery { runSessionDao.getPagedSessions(testUserId, limit, offset) } returns sessions
        
        // When
        val result = repository.getRunHistory(testUserId, limit, offset)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(3, result.data.size)
        assertEquals(sessions, result.data)
    }
    
    @Test
    fun `saveGpsTrackPoint calculates distance correctly`() = runTest {
        // Given
        val lastPoint = TestDataFactory.createGpsTrackPointEntity(
            sessionId = testSessionId,
            latitude = 37.7749,
            longitude = -122.4194,
            distance = 100f,
            sequenceNumber = 5
        )
        
        val newLatitude = 37.7750
        val newLongitude = -122.4195
        val altitude = 50.0
        val speed = 2.78f
        val accuracy = 5.0f
        val timestamp = System.currentTimeMillis()
        
        coEvery { gpsTrackDao.getLastPoint(testSessionId) } returns lastPoint
        coEvery { gpsTrackDao.insert(any()) } returns 1L
        
        // When
        val result = repository.saveGpsTrackPoint(
            testSessionId, newLatitude, newLongitude, altitude, speed, accuracy, timestamp
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            gpsTrackDao.insert(withArg { point ->
                assertEquals(testSessionId, point.sessionId)
                assertEquals(newLatitude, point.latitude)
                assertEquals(newLongitude, point.longitude)
                assertEquals(altitude, point.altitude)
                assertEquals(speed, point.speed)
                assertEquals(accuracy, point.accuracy)
                assertEquals(timestamp, point.timestamp)
                assertEquals(6, point.sequenceNumber)
                assertTrue((point.distance ?: 0f) > lastPoint.distance!!)
                assertNotNull(point.pace)
            })
        }
    }
    
    @Test
    fun `saveGpsTrackPoint handles first point`() = runTest {
        // Given
        coEvery { gpsTrackDao.getLastPoint(testSessionId) } returns null
        coEvery { gpsTrackDao.insert(any()) } returns 1L
        
        // When
        val result = repository.saveGpsTrackPoint(
            testSessionId, 37.7749, -122.4194
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            gpsTrackDao.insert(withArg { point ->
                assertEquals(0f, point.distance)
                assertEquals(0, point.sequenceNumber)
            })
        }
    }
    
    @Test
    fun `saveGpsTrackBatch saves multiple points`() = runTest {
        // Given
        val points = TestDataFactory.createGpsTrackList("other-session", pointCount = 5)
        
        coEvery { gpsTrackDao.saveTrackBatch(any()) } just runs
        
        // When
        val result = repository.saveGpsTrackBatch(testSessionId, points)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            gpsTrackDao.saveTrackBatch(withArg { savedPoints ->
                assertEquals(5, savedPoints.size)
                assertTrue(savedPoints.all { it.sessionId == testSessionId })
            })
        }
    }
    
    @Test
    fun `updateSyncStatus updates status and error message`() = runTest {
        // Given
        val status = SyncStatus.FAILED
        val errorMessage = "Network timeout"
        
        coEvery { 
            runSessionDao.updateSyncStatus(testSessionId, status, null) 
        } just runs
        coEvery { 
            runSessionDao.updateSyncError(testSessionId, errorMessage) 
        } just runs
        
        // When
        val result = repository.updateSyncStatus(testSessionId, status, errorMessage)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerifySequence {
            runSessionDao.updateSyncStatus(testSessionId, status, null)
            runSessionDao.updateSyncError(testSessionId, errorMessage)
        }
    }
    
    @Test
    fun `updateSyncStatus sets timestamp when synced`() = runTest {
        // Given
        val status = SyncStatus.SYNCED
        
        coEvery { 
            runSessionDao.updateSyncStatus(testSessionId, status, any()) 
        } just runs
        
        // When
        val result = repository.updateSyncStatus(testSessionId, status, null)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            runSessionDao.updateSyncStatus(
                testSessionId, 
                status, 
                match { it != null && it > 0 }
            )
        }
    }
    
    @Test
    fun `deleteRun deletes GPS track and session`() = runTest {
        // Given
        coEvery { gpsTrackDao.deleteBySessionId(testSessionId) } just runs
        coEvery { runSessionDao.deleteById(testSessionId) } just runs
        
        // When
        val result = repository.deleteRun(testSessionId)
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerifyOrder {
            gpsTrackDao.deleteBySessionId(testSessionId)
            runSessionDao.deleteById(testSessionId)
        }
    }
    
    @Test
    fun `calculateRunStatistics aggregates data correctly`() = runTest {
        // Given
        val session = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            duration = 1800000L,
            calories = 350,
            steps = 6500
        )
        
        coEvery { runSessionDao.getById(testSessionId) } returns session
        coEvery { gpsTrackDao.getAveragePace(testSessionId) } returns 6.0f
        coEvery { gpsTrackDao.getMaxSpeed(testSessionId) } returns 4.5f
        coEvery { gpsTrackDao.getTotalDistance(testSessionId) } returns 5000f
        coEvery { gpsTrackDao.getAverageHeartRate(testSessionId) } returns 145f
        coEvery { gpsTrackDao.getMaxAltitude(testSessionId) } returns 100.0
        coEvery { gpsTrackDao.getMinAltitude(testSessionId) } returns 50.0
        
        // When
        val result = repository.calculateRunStatistics(testSessionId)
        
        // Then
        assertTrue(result is Result.Success)
        val stats = result.data
        assertEquals(testSessionId, stats.sessionId)
        assertEquals(5000f, stats.totalDistance)
        assertEquals(1800000L, stats.duration)
        assertEquals(6.0f, stats.averagePace)
        assertEquals(4.5f, stats.maxSpeed)
        assertEquals(145, stats.averageHeartRate)
        assertEquals(50f, stats.elevationGain)
        assertEquals(350, stats.calories)
        assertEquals(6500, stats.steps)
    }
    
    @Test
    fun `calculateRunStatistics handles missing session`() = runTest {
        // Given
        coEvery { runSessionDao.getById(testSessionId) } returns null
        
        // When
        val result = repository.calculateRunStatistics(testSessionId)
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
    }
}