package com.runiq.integration

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.repository.RunRepositoryImpl
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.usecase.StartRunUseCase
import com.runiq.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Integration test that tests the complete flow from use case through repository to database.
 * This ensures all layers work together correctly.
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RunSessionIntegrationTest {
    
    private lateinit var database: RunIQDatabase
    private lateinit var repository: RunRepositoryImpl
    private lateinit var startRunUseCase: StartRunUseCase
    
    @Before
    fun setUp() {
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        
        // Create repository with real DAOs
        repository = RunRepositoryImpl(
            runSessionDao = database.runSessionDao(),
            gpsTrackDao = database.gpsTrackDao()
        )
        
        // Create use case with real repository
        startRunUseCase = StartRunUseCase(repository)
    }
    
    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }
    
    @Test
    fun `complete run session flow should work end to end`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        
        // When - Start a run
        val startResult = startRunUseCase(userId, workoutType, coachId)
        
        // Then - Verify run started successfully
        assertTrue("Start run should succeed", startResult.isSuccess)
        val session = startResult.getOrNull()
        assertNotNull("Session should not be null", session)
        assertEquals("User ID should match", userId, session?.userId)
        assertEquals("Workout type should match", workoutType, session?.workoutType)
        assertNull("End time should be null for active session", session?.endTime)
        
        // When - Check active run
        val activeRun = repository.observeActiveRun(userId).first()
        
        // Then - Should find the active session
        assertNotNull("Should have active run", activeRun)
        assertEquals("Active run should match started session", session?.sessionId, activeRun?.sessionId)
        
        // When - Add GPS track points
        val trackPoints = TestDataFactory.createGpsTrack(sessionId = session!!.sessionId, pointCount = 5)
        val saveGpsResult = repository.saveGpsTrackPoints(session.sessionId, trackPoints)
        
        // Then - GPS points should be saved
        assertTrue("GPS save should succeed", saveGpsResult.isSuccess)
        
        val retrievedTrack = repository.getGpsTrack(session.sessionId)
        assertTrue("GPS retrieval should succeed", retrievedTrack.isSuccess)
        assertEquals("Should retrieve all GPS points", 5, retrievedTrack.getOrNull()?.size)
        
        // When - Update session metrics
        val updateResult = repository.updateSessionMetrics(
            sessionId = session.sessionId,
            distance = 5000f,
            averagePace = 6.0f,
            calories = 300,
            averageHeartRate = 150
        )
        
        // Then - Metrics should be updated
        assertTrue("Metrics update should succeed", updateResult.isSuccess)
        
        val updatedSession = repository.getSessionById(session.sessionId).getOrNull()
        assertNotNull("Updated session should exist", updatedSession)
        assertEquals("Distance should be updated", 5000f, updatedSession?.distance)
        assertEquals("Pace should be updated", 6.0f, updatedSession?.averagePace)
        assertEquals("Calories should be updated", 300, updatedSession?.calories)
        assertEquals("Heart rate should be updated", 150, updatedSession?.averageHeartRate)
        
        // When - End the run
        val endResult = repository.endRun(session.sessionId)
        
        // Then - Run should be ended
        assertTrue("End run should succeed", endResult.isSuccess)
        val endedSession = endResult.getOrNull()
        assertNotNull("Ended session should not be null", endedSession)
        assertNotNull("End time should be set", endedSession?.endTime)
        assertFalse("Session should no longer be active", endedSession?.isActive ?: true)
        
        // When - Check no active run
        val noActiveRun = repository.observeActiveRun(userId).first()
        
        // Then - Should not find active session
        assertNull("Should not have active run after ending", noActiveRun)
    }
    
    @Test
    fun `starting run when user has active session should fail`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        
        // Start first run
        val firstRunResult = startRunUseCase(userId, workoutType, coachId)
        assertTrue("First run should succeed", firstRunResult.isSuccess)
        
        // When - Try to start second run
        val secondRunResult = startRunUseCase(userId, WorkoutType.TEMPO_RUN, coachId)
        
        // Then - Second run should fail
        assertTrue("Second run should fail", secondRunResult.isFailure)
        assertTrue("Should be IllegalStateException", 
            secondRunResult.exceptionOrNull() is IllegalStateException)
        assertEquals("Should have correct error message",
            "User already has an active run session",
            secondRunResult.exceptionOrNull()?.message)
    }
    
    @Test
    fun `sync pending sessions should update status correctly`() = runTest {
        // Given
        val sessions = listOf(
            TestDataFactory.createRunSession(syncStatus = com.runiq.domain.model.RunSession.SyncStatus.PENDING),
            TestDataFactory.createRunSession(syncStatus = com.runiq.domain.model.RunSession.SyncStatus.PENDING),
            TestDataFactory.createRunSession(syncStatus = com.runiq.domain.model.RunSession.SyncStatus.SYNCED) // Should not be affected
        )
        
        sessions.forEach { session ->
            database.runSessionDao().insert(session)
        }
        
        // When
        val syncResult = repository.syncPendingSessions()
        
        // Then
        assertTrue("Sync should succeed", syncResult.isSuccess)
        assertEquals("Should sync 2 pending sessions", 2, syncResult.getOrNull())
        
        // Verify status updates
        val pendingSessions = database.runSessionDao().getSessionsByStatus(com.runiq.domain.model.RunSession.SyncStatus.PENDING)
        val syncedSessions = database.runSessionDao().getSessionsByStatus(com.runiq.domain.model.RunSession.SyncStatus.SYNCED)
        
        assertEquals("Should have no pending sessions", 0, pendingSessions.size)
        assertEquals("Should have 3 synced sessions", 3, syncedSessions.size)
    }
}