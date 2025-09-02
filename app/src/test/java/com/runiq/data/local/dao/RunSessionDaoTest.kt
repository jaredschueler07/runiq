package com.runiq.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.model.SyncStatus
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
class RunSessionDaoTest {
    
    private lateinit var database: RunIQDatabase
    private lateinit var runSessionDao: RunSessionDao
    
    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        ).allowMainThreadQueries().build()
        
        runSessionDao = database.runSessionDao()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetRunSession() = runTest {
        val session = createTestRunSession("user1", "coach1")
        
        val insertedId = runSessionDao.insert(session)
        assertTrue("Insert should return positive ID", insertedId > 0)
        
        val retrieved = runSessionDao.getById(session.sessionId)
        assertNotNull("Retrieved session should not be null", retrieved)
        assertEquals("Session IDs should match", session.sessionId, retrieved!!.sessionId)
        assertEquals("User IDs should match", session.userId, retrieved.userId)
        assertEquals("Workout types should match", session.workoutType, retrieved.workoutType)
    }
    
    @Test
    fun observeRunSessionUpdates() = runTest {
        val session = createTestRunSession("user1", "coach1")
        runSessionDao.insert(session)
        
        val flow = runSessionDao.observeById(session.sessionId)
        val initialValue = flow.first()
        
        assertNotNull("Initial value should not be null", initialValue)
        assertEquals("Initial distance should be 0", 0f, initialValue!!.distance, 0.01f)
        
        // Update the session
        val updatedSession = session.copy(distance = 1000f, duration = 300000L)
        runSessionDao.update(updatedSession)
        
        val updatedValue = flow.first()
        assertEquals("Updated distance should be 1000", 1000f, updatedValue!!.distance, 0.01f)
        assertEquals("Updated duration should be 300000", 300000L, updatedValue.duration)
    }
    
    @Test
    fun getAllByUserOrderedByStartTime() = runTest {
        val user1Sessions = listOf(
            createTestRunSession("user1", "coach1", startTime = 1000L),
            createTestRunSession("user1", "coach2", startTime = 2000L),
            createTestRunSession("user1", "coach1", startTime = 3000L)
        )
        val user2Sessions = listOf(
            createTestRunSession("user2", "coach1", startTime = 1500L)
        )
        
        runSessionDao.insertAll(user1Sessions + user2Sessions)
        
        val retrievedUser1 = runSessionDao.getAllByUser("user1")
        assertEquals("Should have 3 sessions for user1", 3, retrievedUser1.size)
        
        // Check ordering (newest first)
        assertEquals("First session should have latest start time", 3000L, retrievedUser1[0].startTime)
        assertEquals("Second session should have middle start time", 2000L, retrievedUser1[1].startTime)
        assertEquals("Third session should have earliest start time", 1000L, retrievedUser1[2].startTime)
        
        val retrievedUser2 = runSessionDao.getAllByUser("user2")
        assertEquals("Should have 1 session for user2", 1, retrievedUser2.size)
    }
    
    @Test
    fun getByWorkoutType() = runTest {
        val sessions = listOf(
            createTestRunSession("user1", "coach1", workoutType = WorkoutType.EASY_RUN),
            createTestRunSession("user1", "coach1", workoutType = WorkoutType.INTERVAL_RUN),
            createTestRunSession("user1", "coach1", workoutType = WorkoutType.EASY_RUN),
            createTestRunSession("user1", "coach1", workoutType = WorkoutType.TEMPO_RUN)
        )
        
        runSessionDao.insertAll(sessions)
        
        val easyRuns = runSessionDao.getByWorkoutType("user1", WorkoutType.EASY_RUN)
        assertEquals("Should have 2 easy runs", 2, easyRuns.size)
        
        val intervalRuns = runSessionDao.getByWorkoutType("user1", WorkoutType.INTERVAL_RUN)
        assertEquals("Should have 1 interval run", 1, intervalRuns.size)
        
        val longRuns = runSessionDao.getByWorkoutType("user1", WorkoutType.LONG_RUN)
        assertEquals("Should have 0 long runs", 0, longRuns.size)
    }
    
    @Test
    fun getByDateRange() = runTest {
        val sessions = listOf(
            createTestRunSession("user1", "coach1", startTime = 1000L),
            createTestRunSession("user1", "coach1", startTime = 2000L),
            createTestRunSession("user1", "coach1", startTime = 3000L),
            createTestRunSession("user1", "coach1", startTime = 4000L)
        )
        
        runSessionDao.insertAll(sessions)
        
        val rangeResults = runSessionDao.getByDateRange("user1", 1500L, 3500L)
        assertEquals("Should have 2 sessions in range", 2, rangeResults.size)
        assertTrue("Should include session at 2000L", 
            rangeResults.any { it.startTime == 2000L })
        assertTrue("Should include session at 3000L", 
            rangeResults.any { it.startTime == 3000L })
    }
    
    @Test
    fun syncStatusOperations() = runTest {
        val sessions = listOf(
            createTestRunSession("user1", "coach1", syncStatus = SyncStatus.PENDING),
            createTestRunSession("user1", "coach1", syncStatus = SyncStatus.SYNCED),
            createTestRunSession("user1", "coach1", syncStatus = SyncStatus.FAILED)
        )
        
        runSessionDao.insertAll(sessions)
        
        val pendingSessions = runSessionDao.getBySyncStatus(SyncStatus.PENDING)
        assertEquals("Should have 1 pending session", 1, pendingSessions.size)
        
        val pendingSyncSessions = runSessionDao.getPendingSyncSessions()
        assertEquals("Should have 2 sessions needing sync (PENDING + FAILED)", 2, pendingSyncSessions.size)
        
        // Update sync status
        val sessionToUpdate = sessions[0]
        runSessionDao.updateSyncStatus(sessionToUpdate.sessionId, SyncStatus.SYNCED)
        
        val updatedSession = runSessionDao.getById(sessionToUpdate.sessionId)
        assertEquals("Sync status should be updated", SyncStatus.SYNCED, updatedSession!!.syncStatus)
    }
    
    @Test
    fun statisticsQueries() = runTest {
        val sessions = listOf(
            createTestRunSession("user1", "coach1", 
                distance = 5000f, duration = 1800000L, averagePace = 6.0f, startTime = 1000L),
            createTestRunSession("user1", "coach1", 
                distance = 3000f, duration = 1200000L, averagePace = 6.5f, startTime = 2000L),
            createTestRunSession("user1", "coach1", 
                distance = 10000f, duration = 3000000L, averagePace = 5.5f, startTime = 3000L),
            createTestRunSession("user2", "coach1", 
                distance = 2000f, duration = 900000L, averagePace = 7.0f, startTime = 1500L)
        )
        
        // Mark sessions as completed
        val completedSessions = sessions.map { it.copy(endTime = it.startTime + it.duration) }
        runSessionDao.insertAll(completedSessions)
        
        val completedCount = runSessionDao.getCompletedRunCount("user1")
        assertEquals("Should have 3 completed runs for user1", 3, completedCount)
        
        val totalDistance = runSessionDao.getTotalDistance("user1", 0L)
        assertEquals("Total distance should be 18000m", 18000f, totalDistance!!, 0.01f)
        
        val totalDuration = runSessionDao.getTotalDuration("user1", 0L)
        assertEquals("Total duration should be 6000000ms", 6000000L, totalDuration!!)
        
        val averagePace = runSessionDao.getAveragePace("user1", 0L)
        assertEquals("Average pace should be 6.0", 6.0f, averagePace!!, 0.1f)
        
        val longestRun = runSessionDao.getLongestRun("user1", 0L)
        assertEquals("Longest run should be 10000m", 10000f, longestRun!!, 0.01f)
    }
    
    @Test
    fun activeSessionManagement() = runTest {
        val activeSession = createTestRunSession("user1", "coach1", endTime = null)
        val completedSession = createTestRunSession("user1", "coach1", 
            endTime = System.currentTimeMillis())
        
        runSessionDao.insertAll(listOf(activeSession, completedSession))
        
        val retrievedActive = runSessionDao.getActiveSession("user1")
        assertNotNull("Should have an active session", retrievedActive)
        assertEquals("Active session should match", activeSession.sessionId, retrievedActive!!.sessionId)
        assertNull("Active session should have null end time", retrievedActive.endTime)
        
        // Complete the active session
        val completionTime = System.currentTimeMillis()
        val duration = completionTime - activeSession.startTime
        runSessionDao.completeSession(activeSession.sessionId, completionTime, duration)
        
        val noLongerActive = runSessionDao.getActiveSession("user1")
        assertNull("Should have no active session after completion", noLongerActive)
    }
    
    @Test
    fun updateSessionMetrics() = runTest {
        val session = createTestRunSession("user1", "coach1")
        runSessionDao.insert(session)
        
        runSessionDao.updateSessionMetrics(
            sessionId = session.sessionId,
            distance = 2500f,
            duration = 900000L,
            averagePace = 6.0f,
            calories = 250
        )
        
        val updated = runSessionDao.getById(session.sessionId)
        assertNotNull("Updated session should not be null", updated)
        assertEquals("Distance should be updated", 2500f, updated!!.distance, 0.01f)
        assertEquals("Duration should be updated", 900000L, updated.duration)
        assertEquals("Average pace should be updated", 6.0f, updated.averagePace, 0.01f)
        assertEquals("Calories should be updated", 250, updated.calories)
    }
    
    @Test
    fun cleanupOperations() = runTest {
        val currentTime = System.currentTimeMillis()
        val oldTime = currentTime - (7 * 24 * 60 * 60 * 1000L) // 7 days ago
        val veryOldTime = currentTime - (30 * 24 * 60 * 60 * 1000L) // 30 days ago
        
        val sessions = listOf(
            createTestRunSession("user1", "coach1", startTime = currentTime),
            createTestRunSession("user1", "coach1", startTime = oldTime),
            createTestRunSession("user1", "coach1", startTime = veryOldTime)
        )
        
        runSessionDao.insertAll(sessions)
        
        val deletedCount = runSessionDao.deleteOlderThan(oldTime)
        assertEquals("Should delete 1 very old session", 1, deletedCount)
        
        val remainingSessions = runSessionDao.getAllByUser("user1")
        assertEquals("Should have 2 remaining sessions", 2, remainingSessions.size)
        assertTrue("Should not contain very old session", 
            remainingSessions.none { it.startTime == veryOldTime })
    }
    
    private fun createTestRunSession(
        userId: String,
        coachId: String,
        workoutType: WorkoutType = WorkoutType.EASY_RUN,
        startTime: Long = System.currentTimeMillis(),
        endTime: Long? = null,
        distance: Float = 0f,
        duration: Long = 0L,
        averagePace: Float = 0f,
        syncStatus: SyncStatus = SyncStatus.PENDING
    ): RunSessionEntity {
        return RunSessionEntity(
            userId = userId,
            startTime = startTime,
            endTime = endTime,
            workoutType = workoutType,
            distance = distance,
            duration = duration,
            averagePace = averagePace,
            coachId = coachId,
            syncStatus = syncStatus
        )
    }
}