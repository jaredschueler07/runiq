package com.runiq.data.repository

import app.cash.turbine.test
import com.runiq.base.BaseRepositoryTest
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.runiq.util.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RunRepositoryImpl.
 * Tests repository operations and data source coordination.
 */
@ExperimentalCoroutinesApi
class RunRepositoryImplTest : BaseRepositoryTest() {
    
    private lateinit var repository: RunRepositoryImpl
    private lateinit var runSessionDao: RunSessionDao
    private lateinit var gpsTrackDao: GpsTrackDao
    
    @Before
    override fun setUp() {
        super.setUp()
        runSessionDao = mockk(relaxed = true)
        gpsTrackDao = mockk(relaxed = true)
        repository = RunRepositoryImpl(runSessionDao, gpsTrackDao)
    }
    
    @Test
    fun `startRun should create session and save to database`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        
        coEvery { runSessionDao.insert(any()) } returns 1L
        
        // When
        val result = repository.startRun(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        
        val session = result.getOrNull()
        assertNotNull("Session should not be null", session)
        assertEquals("User ID should match", userId, session?.userId)
        assertEquals("Workout type should match", workoutType, session?.workoutType)
        assertEquals("Coach ID should match", coachId, session?.coachId)
        assertEquals("Should be pending sync", RunSession.SyncStatus.PENDING, session?.syncStatus)
        
        coVerify { runSessionDao.insert(any()) }
    }
    
    @Test
    fun `endRun should update session with end time`() = runTest {
        // Given
        val sessionId = "session123"
        val activeSession = TestDataFactory.createRunSession(
            sessionId = sessionId,
            endTime = null
        )
        
        coEvery { runSessionDao.getSessionById(sessionId) } returns activeSession
        coEvery { runSessionDao.update(any()) } returns 1
        
        // When
        val result = repository.endRun(sessionId)
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        
        val endedSession = result.getOrNull()
        assertNotNull("Ended session should not be null", endedSession)
        assertNotNull("End time should be set", endedSession?.endTime)
        assertEquals("Should be pending sync", RunSession.SyncStatus.PENDING, endedSession?.syncStatus)
        
        coVerify { runSessionDao.update(any()) }
    }
    
    @Test
    fun `endRun should fail when session not found`() = runTest {
        // Given
        val sessionId = "non_existent_session"
        coEvery { runSessionDao.getSessionById(sessionId) } returns null
        
        // When
        val result = repository.endRun(sessionId)
        
        // Then
        assertTrue("Should return failure", result.isFailure)
        assertTrue("Should be IllegalArgumentException", 
            result.exceptionOrNull() is IllegalArgumentException)
    }
    
    @Test
    fun `observeActiveRun should return active session from flow`() = runTest {
        // Given
        val userId = "user123"
        val activeSession = TestDataFactory.createRunSession(userId = userId, endTime = null)
        val completedSession = TestDataFactory.createRunSession(userId = userId, endTime = System.currentTimeMillis())
        
        every { runSessionDao.observeUserSessions(userId) } returns 
            flowOf(listOf(activeSession, completedSession))
        
        // When & Then
        repository.observeActiveRun(userId).test {
            val result = awaitItem()
            assertNotNull("Should find active session", result)
            assertEquals("Should return the active session", activeSession.sessionId, result?.sessionId)
        }
    }
    
    @Test
    fun `saveGpsTrackPoints should insert all points`() = runTest {
        // Given
        val sessionId = "session123"
        val trackPoints = TestDataFactory.createGpsTrack(sessionId = sessionId, pointCount = 5)
        
        coEvery { gpsTrackDao.insertAll(trackPoints) } returns listOf(1L, 2L, 3L, 4L, 5L)
        
        // When
        val result = repository.saveGpsTrackPoints(sessionId, trackPoints)
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        coVerify { gpsTrackDao.insertAll(trackPoints) }
    }
    
    @Test
    fun `getGpsTrack should return track points for session`() = runTest {
        // Given
        val sessionId = "session123"
        val expectedTrackPoints = TestDataFactory.createGpsTrack(sessionId = sessionId, pointCount = 3)
        
        coEvery { gpsTrackDao.getTrackPoints(sessionId) } returns expectedTrackPoints
        
        // When
        val result = repository.getGpsTrack(sessionId)
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        assertEquals("Should return expected track points", expectedTrackPoints, result.getOrNull())
    }
    
    @Test
    fun `syncPendingSessions should update sync status for all pending sessions`() = runTest {
        // Given
        val pendingSessions = listOf(
            TestDataFactory.createRunSession(syncStatus = RunSession.SyncStatus.PENDING),
            TestDataFactory.createRunSession(syncStatus = RunSession.SyncStatus.PENDING)
        )
        
        coEvery { runSessionDao.getSessionsByStatus(RunSession.SyncStatus.PENDING) } returns pendingSessions
        coEvery { runSessionDao.updateSyncStatus(any(), any(), any()) } returns 1
        
        // When
        val result = repository.syncPendingSessions()
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        assertEquals("Should sync 2 sessions", 2, result.getOrNull())
        
        // Verify each session was updated
        pendingSessions.forEach { session ->
            coVerify { 
                runSessionDao.updateSyncStatus(
                    session.sessionId, 
                    RunSession.SyncStatus.SYNCED, 
                    any()
                ) 
            }
        }
    }
}