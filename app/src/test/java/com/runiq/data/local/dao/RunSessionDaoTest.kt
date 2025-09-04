package com.runiq.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for RunSessionDao
 */
@RunWith(RobolectricTestRunner::class)
class RunSessionDaoTest {
    
    private lateinit var database: RunIQDatabase
    private lateinit var runSessionDao: RunSessionDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        ).allowMainThreadQueries().build()
        
        runSessionDao = database.runSessionDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `insert and retrieve run session`() = runTest {
        // Given
        val session = createTestSession()
        
        // When
        val id = runSessionDao.insert(session)
        val retrieved = runSessionDao.getById(session.sessionId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(session.sessionId, retrieved.sessionId)
        assertEquals(session.userId, retrieved.userId)
        assertEquals(session.workoutType, retrieved.workoutType)
    }
    
    @Test
    fun `get active session returns null when no active sessions`() = runTest {
        // Given
        val completedSession = createTestSession().copy(
            endTime = System.currentTimeMillis()
        )
        runSessionDao.insert(completedSession)
        
        // When
        val activeSession = runSessionDao.getActiveSession("user123")
        
        // Then
        assertNull(activeSession)
    }
    
    @Test
    fun `get active session returns current running session`() = runTest {
        // Given
        val activeSession = createTestSession().copy(
            endTime = null
        )
        runSessionDao.insert(activeSession)
        
        // When
        val retrieved = runSessionDao.getActiveSession("user123")
        
        // Then
        assertNotNull(retrieved)
        assertEquals(activeSession.sessionId, retrieved.sessionId)
    }
    
    @Test
    fun `update sync status changes status correctly`() = runTest {
        // Given
        val session = createTestSession()
        runSessionDao.insert(session)
        
        // When
        runSessionDao.updateSyncStatus(
            session.sessionId, 
            SyncStatus.SYNCED, 
            System.currentTimeMillis()
        )
        val updated = runSessionDao.getById(session.sessionId)
        
        // Then
        assertNotNull(updated)
        assertEquals(SyncStatus.SYNCED, updated.syncStatus)
        assertNotNull(updated.lastSyncedAt)
    }
    
    @Test
    fun `get unsynced sessions returns pending and failed sessions`() = runTest {
        // Given
        val pendingSession = createTestSession().copy(
            sessionId = "pending1",
            syncStatus = SyncStatus.PENDING
        )
        val failedSession = createTestSession().copy(
            sessionId = "failed1",
            syncStatus = SyncStatus.FAILED
        )
        val syncedSession = createTestSession().copy(
            sessionId = "synced1",
            syncStatus = SyncStatus.SYNCED
        )
        
        runSessionDao.insertAll(listOf(pendingSession, failedSession, syncedSession))
        
        // When
        val unsyncedSessions = runSessionDao.getUnsyncedSessions()
        
        // Then
        assertEquals(2, unsyncedSessions.size)
        assertTrue(unsyncedSessions.any { it.sessionId == "pending1" })
        assertTrue(unsyncedSessions.any { it.sessionId == "failed1" })
    }
    
    @Test
    fun `get sessions by workout type filters correctly`() = runTest {
        // Given
        val easyRun = createTestSession().copy(
            sessionId = "easy1",
            workoutType = WorkoutType.EASY_RUN
        )
        val tempoRun = createTestSession().copy(
            sessionId = "tempo1",
            workoutType = WorkoutType.TEMPO_RUN
        )
        val intervalRun = createTestSession().copy(
            sessionId = "interval1",
            workoutType = WorkoutType.INTERVAL_TRAINING
        )
        
        runSessionDao.insertAll(listOf(easyRun, tempoRun, intervalRun))
        
        // When
        val tempoSessions = runSessionDao.getSessionsByWorkoutType(
            "user123",
            WorkoutType.TEMPO_RUN
        ).first()
        
        // Then
        assertEquals(1, tempoSessions.size)
        assertEquals("tempo1", tempoSessions[0].sessionId)
    }
    
    @Test
    fun `calculate total distance sums correctly`() = runTest {
        // Given
        val session1 = createTestSession().copy(
            sessionId = "session1",
            distance = 5000f,
            endTime = System.currentTimeMillis()
        )
        val session2 = createTestSession().copy(
            sessionId = "session2",
            distance = 3000f,
            endTime = System.currentTimeMillis()
        )
        val incompleteSession = createTestSession().copy(
            sessionId = "session3",
            distance = 1000f,
            endTime = null
        )
        
        runSessionDao.insertAll(listOf(session1, session2, incompleteSession))
        
        // When
        val totalDistance = runSessionDao.getTotalDistance("user123")
        
        // Then
        assertEquals(8000f, totalDistance)
    }
    
    @Test
    fun `complete run transaction updates all fields`() = runTest {
        // Given
        val session = createTestSession().copy(endTime = null)
        runSessionDao.insert(session)
        
        // When
        val endTime = System.currentTimeMillis()
        runSessionDao.completeRun(
            sessionId = session.sessionId,
            endTime = endTime,
            distance = 10000f,
            duration = 3600000L,
            averagePace = 6.0f,
            calories = 500,
            steps = 8000
        )
        
        // Then
        val updated = runSessionDao.getById(session.sessionId)
        assertNotNull(updated)
        assertEquals(endTime, updated.endTime)
        assertEquals(10000f, updated.distance)
        assertEquals(3600000L, updated.duration)
        assertEquals(6.0f, updated.averagePace)
        assertEquals(500, updated.calories)
        assertEquals(8000, updated.steps)
    }
    
    private fun createTestSession(): RunSessionEntity {
        return RunSessionEntity(
            sessionId = "test-session-${System.currentTimeMillis()}",
            userId = "user123",
            startTime = System.currentTimeMillis(),
            workoutType = WorkoutType.EASY_RUN,
            coachId = "coach123",
            syncStatus = SyncStatus.PENDING
        )
    }
}