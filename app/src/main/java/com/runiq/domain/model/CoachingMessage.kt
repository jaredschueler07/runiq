package com.runiq.domain.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
 * Represents a coaching message delivered during a run
 */
@Keep
@JsonClass(generateAdapter = true)
data class CoachingMessage(
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: TextCategory,
    val isLLMGenerated: Boolean = false,
    val priority: Priority = Priority.NORMAL,
    val triggerConditions: List<String> = emptyList()
) {
    
    enum class Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}

/**
 * Categories for different types of coaching messages
 */
@Keep
enum class TextCategory {
    MOTIVATION,
    PACE_GUIDANCE,
    FORM_TIPS,
    BREATHING,
    HYDRATION,
    MILESTONE,
    WARNING,
    ENCOURAGEMENT,
    TECHNICAL,
    WEATHER,
    SAFETY
}