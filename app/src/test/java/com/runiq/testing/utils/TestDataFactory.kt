package com.runiq.testing.utils

import com.runiq.data.local.entities.*
import com.runiq.domain.model.*
import java.util.UUID
import kotlin.random.Random

/**
 * Factory for creating test data objects with sensible defaults
 * that can be overridden as needed for specific tests.
 */
object TestDataFactory {
    
    // RunSession Factory
    fun createRunSessionEntity(
        sessionId: String = UUID.randomUUID().toString(),
        userId: String = "test-user-${Random.nextInt(1000)}",
        startTime: Long = System.currentTimeMillis() - 3600000, // 1 hour ago
        endTime: Long? = System.currentTimeMillis(),
        distance: Float = 5000f, // 5km
        duration: Long = 1800000L, // 30 minutes
        workoutType: WorkoutType = WorkoutType.EASY_RUN,
        averagePace: Float = 6.0f, // 6 min/km
        calories: Int = 350,
        coachId: String = "coach-${Random.nextInt(100)}",
        syncStatus: SyncStatus = SyncStatus.PENDING
    ): RunSessionEntity {
        return RunSessionEntity(
            sessionId = sessionId,
            userId = userId,
            startTime = startTime,
            endTime = endTime,
            distance = distance,
            duration = duration,
            workoutType = workoutType,
            averagePace = averagePace,
            calories = calories,
            coachId = coachId,
            syncStatus = syncStatus,
            averageHeartRate = 145,
            maxHeartRate = 165,
            steps = (distance * 1.3).toInt(),
            elevationGain = 50f,
            elevationLoss = 45f
        )
    }
    
    // GPS Track Point Factory
    fun createGpsTrackPointEntity(
        sessionId: String = UUID.randomUUID().toString(),
        latitude: Double = 37.7749 + Random.nextDouble() * 0.01,
        longitude: Double = -122.4194 + Random.nextDouble() * 0.01,
        timestamp: Long = System.currentTimeMillis(),
        sequenceNumber: Int = 0,
        speed: Float? = 2.78f, // 10 km/h in m/s
        altitude: Double? = 50.0,
        accuracy: Float? = 5.0f,
        distance: Float = 0f,
        pace: Float? = 6.0f,
        heartRate: Int? = 145
    ): GpsTrackPointEntity {
        return GpsTrackPointEntity(
            sessionId = sessionId,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            sequenceNumber = sequenceNumber,
            speed = speed,
            altitude = altitude,
            accuracy = accuracy,
            distance = distance,
            pace = pace,
            heartRate = heartRate,
            isPausePoint = false
        )
    }
    
    // Coach Factory
    fun createCoachEntity(
        id: String = "coach-${UUID.randomUUID()}",
        name: String = "Test Coach ${Random.nextInt(100)}",
        description: String = "A test coach for unit testing",
        coachingStyle: CoachingStyle = CoachingStyle.MOTIVATIONAL,
        experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
        motivationStyle: MotivationStyle = MotivationStyle.POSITIVE_REINFORCEMENT,
        isPremium: Boolean = false,
        isActive: Boolean = true
    ): CoachEntity {
        return CoachEntity(
            id = id,
            name = name,
            description = description,
            imageUrl = "https://example.com/coach.jpg",
            coachingStyle = coachingStyle,
            experienceLevel = experienceLevel,
            motivationStyle = motivationStyle,
            voiceCharacteristics = VoiceCharacteristics(
                voiceId = "voice-123",
                pitch = 1.0f,
                speed = 1.0f,
                energy = "medium"
            ),
            specializations = listOf("5K", "10K", "Marathon"),
            typicalPhrases = listOf("Great job!", "Keep pushing!", "You've got this!"),
            cadenceRange = 160..180,
            isPremium = isPremium,
            isActive = isActive,
            usageCount = Random.nextInt(0, 1000),
            averageRating = if (Random.nextBoolean()) Random.nextFloat() * 5 else null,
            totalRatings = Random.nextInt(0, 100),
            version = 1,
            createdAt = System.currentTimeMillis() - 86400000L, // 1 day ago
            updatedAt = System.currentTimeMillis()
        )
    }
    
    // Health Metric Cache Factory
    fun createHealthMetricCacheEntity(
        id: Long = 0,
        userId: String = "test-user-${Random.nextInt(1000)}",
        metricType: String = "heart_rate",
        value: Float = 145f,
        unit: String = "bpm",
        timestamp: Long = System.currentTimeMillis(),
        source: String = "Health Connect"
    ): HealthMetricCacheEntity {
        return HealthMetricCacheEntity(
            id = id,
            userId = userId,
            metricType = metricType,
            value = value,
            unit = unit,
            timestamp = timestamp,
            source = source,
            metadata = mapOf("device" to "Test Device"),
            syncedAt = null,
            expiresAt = timestamp + 3600000L // 1 hour later
        )
    }
    
    // Coaching Message Factory
    fun createCoachingMessage(
        id: String = UUID.randomUUID().toString(),
        timestamp: Long = System.currentTimeMillis(),
        message: String = "Great pace! Keep it steady.",
        type: MessageType = MessageType.PACE_GUIDANCE
    ): CoachingMessage {
        return CoachingMessage(
            id = id,
            timestamp = timestamp,
            message = message,
            type = type,
            audioUrl = null,
            wasPlayed = false,
            userFeedback = null
        )
    }
    
    // Batch creation helpers
    fun createRunSessionList(count: Int = 5): List<RunSessionEntity> {
        return (1..count).map { index ->
            createRunSessionEntity(
                sessionId = "session-$index",
                startTime = System.currentTimeMillis() - (index * 86400000L), // Each day earlier
                distance = 3000f + (index * 1000f),
                workoutType = WorkoutType.values()[index % WorkoutType.values().size]
            )
        }
    }
    
    fun createGpsTrackList(
        sessionId: String,
        pointCount: Int = 100,
        startTime: Long = System.currentTimeMillis()
    ): List<GpsTrackPointEntity> {
        var cumulativeDistance = 0f
        val baseLatitude = 37.7749
        val baseLongitude = -122.4194
        
        return (0 until pointCount).map { index ->
            cumulativeDistance += Random.nextFloat() * 20 // Add 0-20m per point
            
            createGpsTrackPointEntity(
                sessionId = sessionId,
                latitude = baseLatitude + (index * 0.0001),
                longitude = baseLongitude + (index * 0.0001),
                timestamp = startTime + (index * 1000L), // 1 second intervals
                sequenceNumber = index,
                distance = cumulativeDistance,
                speed = 2.5f + Random.nextFloat(), // 2.5-3.5 m/s
                heartRate = 140 + Random.nextInt(20) // 140-160 bpm
            )
        }
    }
    
    fun createCoachList(count: Int = 3): List<CoachEntity> {
        val styles = CoachingStyle.values()
        return (1..count).map { index ->
            createCoachEntity(
                id = "coach-$index",
                name = "Coach #$index",
                coachingStyle = styles[index % styles.size],
                isPremium = index % 2 == 0
            )
        }
    }
}