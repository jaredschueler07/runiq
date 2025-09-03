package com.runiq.data.local.dao

import app.cash.turbine.test
import com.runiq.base.BaseDaoTest
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.runiq.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for RunSessionDao.
 * Tests all database operations for run sessions.
 */
@ExperimentalCoroutinesApi
class RunSessionDaoTest : BaseDaoTest() {
    
    private lateinit var runSessionDao: RunSessionDao
    
    override fun setUp() {
        super.setUp()
        runSessionDao = database.runSessionDao()
    }
    
    @Test
    fun `insert run session should save successfully`() = runTest {
        // Given
        val session = TestDataFactory.createRunSession(userId = "user123")
        
        // When
        val insertId = runSessionDao.insert(session)
        
        // Then
        assertTrue("Insert should return positive ID", insertId > 0)
        
        val retrievedSession = runSessionDao.getSessionById(session.sessionId)
        assertNotNull("Session should be retrievable", retrievedSession)
        assertEquals("Session should match", session, retrievedSession)
    }
    
    @Test
    fun `observeUserSessions should emit sessions in descending order by start time`() = runTest {
        // Given
        val userId = "user123"
        val oldSession = TestDataFactory.createRunSession(
            userId = userId,
            startTime = System.currentTimeMillis() - 7200000L // 2 hours ago
        )
        val newSession = TestDataFactory.createRunSession(
            userId = userId,
            startTime = System.currentTimeMillis() - 3600000L // 1 hour ago
        )
        
        // When
        runSessionDao.insert(oldSession)
        runSessionDao.insert(newSession)
        
        // Then
        runSessionDao.observeUserSessions(userId).test {
            val sessions = awaitItem()
            assertEquals("Should have 2 sessions", 2, sessions.size)
            assertEquals("Newest session should be first", newSession.sessionId, sessions[0].sessionId)
            assertEquals("Oldest session should be second", oldSession.sessionId, sessions[1].sessionId)
        }
    }
    
    @Test
    fun `getActiveSession should return session with null endTime`() = runTest {
        // Given
        val userId = "user123"
        val activeSession = TestDataFactory.createRunSession(
            userId = userId,
            endTime = null // Active session
        )
        val completedSession = TestDataFactory.createRunSession(
            userId = userId,
            endTime = System.currentTimeMillis() // Completed session
        )
        
        // When
        runSessionDao.insert(activeSession)
        runSessionDao.insert(completedSession)
        
        // Then
        val result = runSessionDao.getActiveSession(userId)
        assertNotNull("Should find active session", result)
        assertEquals("Should return the active session", activeSession.sessionId, result?.sessionId)
        assertNull("Active session should have null endTime", result?.endTime)
    }
    
    @Test
    fun `updateHealthConnectId should update only the specified session`() = runTest {
        // Given
        val session = TestDataFactory.createRunSession(healthConnectId = null)
        runSessionDao.insert(session)
        val healthConnectId = "hc_123456"
        
        // When
        val updateCount = runSessionDao.updateHealthConnectId(session.sessionId, healthConnectId)
        
        // Then
        assertEquals("Should update exactly one row", 1, updateCount)
        
        val updatedSession = runSessionDao.getSessionById(session.sessionId)
        assertNotNull("Session should still exist", updatedSession)
        assertEquals("Health Connect ID should be updated", healthConnectId, updatedSession?.healthConnectId)
    }
    
    @Test
    fun `getSessionsByStatus should filter by sync status`() = runTest {
        // Given
        val pendingSession = TestDataFactory.createRunSession(syncStatus = RunSession.SyncStatus.PENDING)
        val syncedSession = TestDataFactory.createRunSession(syncStatus = RunSession.SyncStatus.SYNCED)
        val failedSession = TestDataFactory.createRunSession(syncStatus = RunSession.SyncStatus.FAILED)
        
        runSessionDao.insertAll(listOf(pendingSession, syncedSession, failedSession))
        
        // When
        val pendingSessions = runSessionDao.getSessionsByStatus(RunSession.SyncStatus.PENDING)
        val syncedSessions = runSessionDao.getSessionsByStatus(RunSession.SyncStatus.SYNCED)
        
        // Then
        assertEquals("Should have 1 pending session", 1, pendingSessions.size)
        assertEquals("Should have 1 synced session", 1, syncedSessions.size)
        assertEquals("Pending session should match", pendingSession.sessionId, pendingSessions[0].sessionId)
        assertEquals("Synced session should match", syncedSession.sessionId, syncedSessions[0].sessionId)
    }
    
    @Test
    fun `getTotalDistanceForUser should sum completed sessions only`() = runTest {
        // Given
        val userId = "user123"
        val completedSession1 = TestDataFactory.createRunSession(
            userId = userId,
            distance = 5000f,
            endTime = System.currentTimeMillis()
        )
        val completedSession2 = TestDataFactory.createRunSession(
            userId = userId,
            distance = 3000f,
            endTime = System.currentTimeMillis()
        )
        val activeSession = TestDataFactory.createRunSession(
            userId = userId,
            distance = 2000f,
            endTime = null // Should not be included
        )
        
        runSessionDao.insertAll(listOf(completedSession1, completedSession2, activeSession))
        
        // When
        val totalDistance = runSessionDao.getTotalDistanceForUser(userId)
        
        // Then
        assertNotNull("Total distance should not be null", totalDistance)
        assertEquals("Should sum only completed sessions", 8000f, totalDistance!!, 0.01f)
    }
    
    @Test
    fun `deleteById should remove session`() = runTest {
        // Given
        val session = TestDataFactory.createRunSession()
        runSessionDao.insert(session)
        
        // When
        val deleteCount = runSessionDao.deleteById(session.sessionId)
        
        // Then
        assertEquals("Should delete exactly one row", 1, deleteCount)
        
        val retrievedSession = runSessionDao.getSessionById(session.sessionId)
        assertNull("Session should be deleted", retrievedSession)
    }
    
    @Test
    fun `getRecentSessions should respect limit parameter`() = runTest {
        // Given
        val userId = "user123"
        val sessions = (1..15).map { index ->
            TestDataFactory.createRunSession(
                userId = userId,
                startTime = System.currentTimeMillis() - (index * 3600000L) // Each hour apart
            )
        }
        
        runSessionDao.insertAll(sessions)
        
        // When
        val recentSessions = runSessionDao.getRecentSessions(userId, limit = 5)
        
        // Then
        assertEquals("Should return exactly 5 sessions", 5, recentSessions.size)
        
        // Verify they are in descending order by start time
        for (i in 0 until recentSessions.size - 1) {
            assertTrue(
                "Sessions should be ordered by start time descending",
                recentSessions[i].startTime > recentSessions[i + 1].startTime
            )
        }
    }
}