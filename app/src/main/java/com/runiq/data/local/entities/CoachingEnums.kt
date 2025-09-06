package com.runiq.data.local.entities

/**
 * Coaching-related enums for database entities
 */

enum class CoachingStyle {
    MOTIVATIONAL,
    ANALYTICAL,
    SUPPORTIVE,
    TOUGH_LOVE,
    EDUCATIONAL,
    ZEN,
    COMPETITIVE,
    ADAPTIVE
}

enum class ExperienceLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    ELITE
}

enum class MotivationStyle {
    POSITIVE_REINFORCEMENT,
    GOAL_ORIENTED,
    CHALLENGE_BASED,
    MINDFULNESS,
    DATA_DRIVEN,
    SOCIAL_COMPARISON,
    PERSONAL_BEST,
    BALANCED
}

enum class MessagePriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class MessageTrigger {
    TIME_BASED,
    DISTANCE_BASED,
    PACE_BASED,
    HEART_RATE_BASED,
    CADENCE_BASED,
    ELEVATION_BASED,
    USER_REQUESTED,
    ACHIEVEMENT,
    WARNING,
    SCHEDULED
}

data class MessageCondition(
    val type: String,
    val operator: String,
    val value: String,
    val unit: String? = null
)