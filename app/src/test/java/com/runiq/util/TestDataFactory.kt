package com.runiq.util

import com.runiq.domain.model.Coach
import com.runiq.domain.model.CoachingMessage
import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import java.util.UUID

/**
 * Factory for creating test data objects with sensible defaults.
 * Provides consistent test data across all test files.
 */
object TestDataFactory {
    
    fun createRunSession(
        sessionId: String = UUID.randomUUID().toString(),
        userId: String = "test_user_123",
        startTime: Long = System.currentTimeMillis() - 3600000L, // 1 hour ago
        endTime: Long? = System.currentTimeMillis(),
        distance: Float = 5000f, // 5km
        duration: Long = 1800000L, // 30 minutes
        workoutType: WorkoutType = WorkoutType.EASY_RUN,
        averagePace: Float = 6.0f, // 6 min/km
        averageHeartRate: Int? = 150,
        calories: Int = 300,
        coachId: String = "coach_sarah",
        healthConnectId: String? = "hc_${UUID.randomUUID()}",
        syncStatus: RunSession.SyncStatus = RunSession.SyncStatus.SYNCED
    ): RunSession {
        return RunSession(
            sessionId = sessionId,
            userId = userId,
            startTime = startTime,
            endTime = endTime,
            distance = distance,
            duration = duration,
            workoutType = workoutType,
            averagePace = averagePace,
            averageHeartRate = averageHeartRate,
            calories = calories,
            coachId = coachId,
            healthConnectId = healthConnectId,
            syncStatus = syncStatus,
            coachingMessages = listOf(createCoachingMessage()),
            lastSyncedAt = endTime
        )
    }
    
    fun createGpsTrackPoint(
        sessionId: String = UUID.randomUUID().toString(),
        timestamp: Long = System.currentTimeMillis() + kotlin.random.Random.nextLong(0, 1000), // Add small random offset
        latitude: Double = 37.7749, // San Francisco
        longitude: Double = -122.4194,
        altitude: Double = 50.0,
        accuracy: Float = 5.0f,
        speed: Float = 3.33f, // ~12 km/h
        bearing: Float = 45.0f
    ): GpsTrackPoint {
        return GpsTrackPoint(
            sessionId = sessionId,
            timestamp = timestamp,
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            accuracy = accuracy,
            speed = speed,
            bearing = bearing
        )
    }
    
    fun createCoach(
        id: String = "coach_sarah",
        name: String = "Sarah",
        personality: Coach.Personality = Coach.Personality.MOTIVATIONAL,
        specialization: Coach.Specialization = Coach.Specialization.ENDURANCE,
        voiceId: String = "voice_123",
        isActive: Boolean = true
    ): Coach {
        return Coach(
            id = id,
            name = name,
            personality = personality,
            specialization = specialization,
            voiceCharacteristics = Coach.VoiceCharacteristics(
                voiceId = voiceId,
                stability = 0.8f,
                clarity = 0.9f
            ),
            isActive = isActive
        )
    }
    
    fun createCoachingMessage(
        message: String = "Great pace! Keep it up!",
        category: CoachingMessage.Category = CoachingMessage.Category.ENCOURAGEMENT,
        timestamp: Long = System.currentTimeMillis(),
        isLLMGenerated: Boolean = false
    ): CoachingMessage {
        return CoachingMessage(
            message = message,
            category = category,
            timestamp = timestamp,
            isLLMGenerated = isLLMGenerated
        )
    }
    
    fun createGpsTrack(
        sessionId: String = UUID.randomUUID().toString(),
        pointCount: Int = 10,
        startLatitude: Double = 37.7749,
        startLongitude: Double = -122.4194
    ): List<GpsTrackPoint> {
        return (0 until pointCount).map { index ->
            createGpsTrackPoint(
                sessionId = sessionId,
                timestamp = System.currentTimeMillis() + (index * 10000L), // 10 second intervals
                latitude = startLatitude + (index * 0.0001), // Small increments for realistic movement
                longitude = startLongitude + (index * 0.0001),
                speed = 3.33f + (index * 0.1f) // Gradually increasing speed
            )
        }
    }
    
    fun createWorkoutTypes(): List<WorkoutType> {
        return listOf(
            WorkoutType.EASY_RUN,
            WorkoutType.TEMPO_RUN,
            WorkoutType.INTERVAL_TRAINING,
            WorkoutType.LONG_RUN,
            WorkoutType.RECOVERY_RUN
        )
    }
}