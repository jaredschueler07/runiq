package com.runiq.domain.model

import com.runiq.base.BaseUnitTest
import com.runiq.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for RunSession domain model.
 * Tests calculated properties and business logic.
 */
@ExperimentalCoroutinesApi
class RunSessionTest : BaseUnitTest() {
    
    @Test
    fun `isActive should return true when endTime is null`() {
        // Given
        val activeSession = TestDataFactory.createRunSession(endTime = null)
        
        // When & Then
        assertTrue("Session with null endTime should be active", activeSession.isActive)
    }
    
    @Test
    fun `isActive should return false when endTime is set`() {
        // Given
        val completedSession = TestDataFactory.createRunSession(endTime = System.currentTimeMillis())
        
        // When & Then
        assertFalse("Session with endTime should not be active", completedSession.isActive)
    }
    
    @Test
    fun `currentDuration should calculate duration for active session`() {
        // Given
        val startTime = System.currentTimeMillis() - 1800000L // 30 minutes ago
        val activeSession = TestDataFactory.createRunSession(
            startTime = startTime,
            endTime = null
        )
        
        // When
        val duration = activeSession.currentDuration
        
        // Then
        assertTrue("Duration should be approximately 30 minutes", 
            duration > 1790000L && duration < 1810000L) // Allow 10 second tolerance
    }
    
    @Test
    fun `currentDuration should use endTime for completed session`() {
        // Given
        val startTime = System.currentTimeMillis() - 1800000L // 30 minutes ago
        val endTime = startTime + 1500000L // 25 minutes later
        val completedSession = TestDataFactory.createRunSession(
            startTime = startTime,
            endTime = endTime
        )
        
        // When
        val duration = completedSession.currentDuration
        
        // Then
        assertEquals("Duration should be exactly 25 minutes", 1500000L, duration)
    }
    
    @Test
    fun `averageSpeed should calculate correctly`() {
        // Given
        val session = TestDataFactory.createRunSession(
            distance = 5000f, // 5km
            duration = 1800000L // 30 minutes
        )
        
        // When
        val averageSpeed = session.averageSpeed
        
        // Then
        val expectedSpeed = 5000f / (1800000L / 1000f) // 5000m / 1800s = 2.78 m/s
        assertEquals("Average speed should be correct", expectedSpeed, averageSpeed, 0.01f)
    }
    
    @Test
    fun `averageSpeed should return zero when duration is zero`() {
        // Given
        val session = TestDataFactory.createRunSession(
            distance = 5000f,
            duration = 0L
        )
        
        // When
        val averageSpeed = session.averageSpeed
        
        // Then
        assertEquals("Average speed should be zero when duration is zero", 0f, averageSpeed, 0.01f)
    }
    
    @Test
    fun `sync status enum should have correct values`() {
        // Test all sync status values exist and can be created
        val pending = RunSession.SyncStatus.PENDING
        val syncing = RunSession.SyncStatus.SYNCING
        val synced = RunSession.SyncStatus.SYNCED
        val failed = RunSession.SyncStatus.FAILED
        
        assertEquals("PENDING should be correct", "PENDING", pending.name)
        assertEquals("SYNCING should be correct", "SYNCING", syncing.name)
        assertEquals("SYNCED should be correct", "SYNCED", synced.name)
        assertEquals("FAILED should be correct", "FAILED", failed.name)
    }
    
    @Test
    fun `session should handle different workout types`() {
        // Test that sessions can be created with all workout types
        WorkoutType.values().forEach { workoutType ->
            val session = TestDataFactory.createRunSession(workoutType = workoutType)
            assertEquals("Workout type should be preserved", workoutType, session.workoutType)
        }
    }
    
    @Test
    fun `session should generate unique IDs by default`() {
        // Given & When
        val session1 = TestDataFactory.createRunSession()
        val session2 = TestDataFactory.createRunSession()
        
        // Then
        assertFalse("Session IDs should be unique", session1.sessionId == session2.sessionId)
        assertTrue("Session IDs should not be empty", session1.sessionId.isNotEmpty())
        assertTrue("Session IDs should not be empty", session2.sessionId.isNotEmpty())
    }
}