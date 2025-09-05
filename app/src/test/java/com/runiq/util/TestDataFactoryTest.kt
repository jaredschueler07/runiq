package com.runiq.util

import com.runiq.base.BaseUnitTest
import com.runiq.domain.model.WorkoutType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for TestDataFactory.
 * Ensures test data creation works correctly and provides valid data.
 */
@ExperimentalCoroutinesApi
class TestDataFactoryTest : BaseUnitTest() {
    
    @Test
    fun `createRunSession should create valid session with defaults`() {
        // When
        val session = TestDataFactory.createRunSession()
        
        // Then
        assertNotNull("Session ID should not be null", session.sessionId)
        assertTrue("Session ID should not be empty", session.sessionId.isNotEmpty())
        assertEquals("Should have default user ID", "test_user_123", session.userId)
        assertEquals("Should have default workout type", WorkoutType.EASY_RUN, session.workoutType)
        assertEquals("Should have default coach", "coach_sarah", session.coachId)
        assertTrue("Should have positive distance", session.distance > 0)
        assertTrue("Should have positive duration", session.duration > 0)
    }
    
    @Test
    fun `createRunSession should accept custom parameters`() {
        // Given
        val customUserId = "custom_user"
        val customWorkoutType = WorkoutType.INTERVAL_TRAINING
        val customDistance = 10000f
        
        // When
        val session = TestDataFactory.createRunSession(
            userId = customUserId,
            workoutType = customWorkoutType,
            distance = customDistance
        )
        
        // Then
        assertEquals("Should use custom user ID", customUserId, session.userId)
        assertEquals("Should use custom workout type", customWorkoutType, session.workoutType)
        assertEquals("Should use custom distance", customDistance, session.distance)
    }
    
    @Test
    fun `createGpsTrackPoint should create valid GPS point`() {
        // When
        val trackPoint = TestDataFactory.createGpsTrackPoint()
        
        // Then
        assertNotNull("Track point ID should not be null", trackPoint.id)
        assertTrue("Latitude should be valid", trackPoint.latitude >= -90 && trackPoint.latitude <= 90)
        assertTrue("Longitude should be valid", trackPoint.longitude >= -180 && trackPoint.longitude <= 180)
        assertTrue("Accuracy should be positive", trackPoint.accuracy > 0)
        assertTrue("Speed should be non-negative", trackPoint.speed >= 0)
    }
    
    @Test
    fun `createGpsTrack should create multiple connected points`() {
        // Given
        val sessionId = "session123"
        val pointCount = 5
        
        // When
        val track = TestDataFactory.createGpsTrack(
            sessionId = sessionId,
            pointCount = pointCount
        )
        
        // Then
        assertEquals("Should create correct number of points", pointCount, track.size)
        assertTrue("All points should have same session ID", 
            track.all { it.sessionId == sessionId })
        
        // Verify timestamps are in order
        for (i in 0 until track.size - 1) {
            assertTrue("Timestamps should be in ascending order",
                track[i].timestamp < track[i + 1].timestamp)
        }
        
        // Verify points form a realistic path
        for (i in 0 until track.size - 1) {
            val distance = track[i].distanceTo(track[i + 1])
            assertTrue("Points should be reasonably close together (< 100m)", distance < 100f)
        }
    }
    
    @Test
    fun `createCoach should create valid coach with defaults`() {
        // When
        val coach = TestDataFactory.createCoach()
        
        // Then
        assertNotNull("Coach ID should not be null", coach.id)
        assertNotNull("Coach name should not be null", coach.name)
        assertNotNull("Voice characteristics should not be null", coach.voiceCharacteristics)
        assertTrue("Should be active by default", coach.isActive)
        assertTrue("Stability should be valid", 
            coach.voiceCharacteristics.stability >= 0f && coach.voiceCharacteristics.stability <= 1f)
        assertTrue("Clarity should be valid", 
            coach.voiceCharacteristics.clarity >= 0f && coach.voiceCharacteristics.clarity <= 1f)
    }
    
    @Test
    fun `createCoachingMessage should create valid message`() {
        // When
        val message = TestDataFactory.createCoachingMessage()
        
        // Then
        assertNotNull("Message should not be null", message.message)
        assertTrue("Message should not be empty", message.message.isNotEmpty())
        assertNotNull("Category should not be null", message.category)
        assertTrue("Timestamp should be recent", 
            message.timestamp > System.currentTimeMillis() - 5000) // Within last 5 seconds
    }
    
    @Test
    fun `createWorkoutTypes should return all workout types`() {
        // When
        val workoutTypes = TestDataFactory.createWorkoutTypes()
        
        // Then
        assertEquals("Should return all workout types", 
            WorkoutType.values().size, workoutTypes.size)
        assertTrue("Should contain all workout types", 
            workoutTypes.containsAll(WorkoutType.values().toList()))
    }
    
    @Test
    fun `multiple calls should create unique objects`() {
        // When
        val session1 = TestDataFactory.createRunSession()
        val session2 = TestDataFactory.createRunSession()
        val trackPoint1 = TestDataFactory.createGpsTrackPoint()
        val trackPoint2 = TestDataFactory.createGpsTrackPoint()
        
        // Then
        assertFalse("Sessions should have unique IDs", session1.sessionId == session2.sessionId)
        assertFalse("Track points should have unique IDs", trackPoint1.id == trackPoint2.id)
        assertTrue("Track points should have different timestamps", 
            trackPoint1.timestamp != trackPoint2.timestamp)
    }
}