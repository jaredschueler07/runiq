package com.runiq.data.local.dao

import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.WorkoutType
import com.runiq.testing.base.BaseDaoTest
import com.runiq.testing.utils.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Comprehensive unit tests for RunSessionDao
 */
@ExperimentalCoroutinesApi
class RunSessionDaoTest : BaseDaoTest() {
    
    private lateinit var runSessionDao: RunSessionDao
    private val testUserId = "test-user-123"

    @Before
    fun setupDao() {
        runSessionDao = database.runSessionDao()
    }
    
    @Test
    fun `insert and retrieve run session`() = runTest {
        // Given
        val session = TestDataFactory.createRunSessionEntity(userId = testUserId)

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
    fun `insert multiple sessions`() = runTest {
        // Given
        val sessions = TestDataFactory.createRunSessionList(count = 5).map {
            it.copy(userId = testUserId)
        }

        // When
        runSessionDao.insertAll(sessions)
        val retrieved = runSessionDao.getAllByUser(testUserId).first()

        // Then
        assertEquals(5, retrieved.size)
    }

    @Test
    fun `get active session returns null when no active sessions`() = runTest {
        // Given
        val completedSession = TestDataFactory.createRunSessionEntity(
            userId = testUserId,
            endTime = System.currentTimeMillis()
        )
        runSessionDao.insert(completedSession)

        // When
        val activeSession = runSessionDao.getActiveSession(testUserId)

        // Then
        assertNull(activeSession)
    }

    @Test
    fun `get active session returns current running session`() = runTest {
        // Given
        val activeSession = TestDataFactory.createRunSessionEntity(
            userId = testUserId,
            endTime = null
        )
        runSessionDao.insert(activeSession)

        // When
        val retrieved = runSessionDao.getActiveSession(testUserId)

        // Then
        assertNotNull(retrieved)
        assertEquals(activeSession.sessionId, retrieved.sessionId)
    }

    @Test
    fun `update sync status changes status correctly`() = runTest {
        // Given
        val session = TestDataFactory.createRunSessionEntity(userId = testUserId)
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
        val pendingSession = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "pending1",
            syncStatus = SyncStatus.PENDING
        )
        val failedSession = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "failed1",
            syncStatus = SyncStatus.FAILED
        )
        val syncedSession = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
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
        val easyRun = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "easy1",
            workoutType = WorkoutType.EASY_RUN
        )
        val tempoRun = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "tempo1",
            workoutType = WorkoutType.TEMPO_RUN
        )
        val intervalRun = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "interval1",
            workoutType = WorkoutType.INTERVAL_TRAINING
        )

        runSessionDao.insertAll(listOf(easyRun, tempoRun, intervalRun))

        // When
        val tempoSessions = runSessionDao.getSessionsByWorkoutType(
            testUserId,
            WorkoutType.TEMPO_RUN
        ).first()

        // Then
        assertEquals(1, tempoSessions.size)
        assertEquals("tempo1", tempoSessions[0].sessionId)
    }

    @Test
    fun `calculate total distance sums correctly`() = runTest {
        // Given
        val session1 = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "session1",
            distance = 5000f,
            endTime = System.currentTimeMillis()
        )
        val session2 = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "session2",
            distance = 3000f,
            endTime = System.currentTimeMillis()
        )
        val incompleteSession = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(
            sessionId = "session3",
            distance = 1000f,
            endTime = null
        )

        runSessionDao.insertAll(listOf(session1, session2, incompleteSession))

        // When
        val totalDistance = runSessionDao.getTotalDistance(testUserId)

        // Then
        assertEquals(8000f, totalDistance)
    }

    @Test
    fun `complete run transaction updates all fields`() = runTest {
        // Given
        val session = TestDataFactory.createRunSessionEntity(userId = testUserId).copy(endTime = null)
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
}