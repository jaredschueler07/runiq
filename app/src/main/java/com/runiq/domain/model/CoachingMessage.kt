package com.runiq.domain.model

/**
 * Represents a coaching message delivered during a run
 */
data class CoachingMessage(
    val id: String,
    val timestamp: Long,
    val message: String,
    val type: MessageType,
    val audioUrl: String? = null,
    val wasPlayed: Boolean = false,
    val userFeedback: UserFeedback? = null
)

enum class MessageType {
    MOTIVATION,
    PACE_GUIDANCE,
    FORM_CORRECTION,
    BREATHING_TIP,
    HYDRATION_REMINDER,
    ACHIEVEMENT,
    WARNING,
    ENCOURAGEMENT,
    TECHNICAL_TIP,
    MILESTONE,
    WORKOUT_UPDATE
}

enum class UserFeedback {
    HELPFUL,
    NOT_HELPFUL,
    NEUTRAL
}