package com.runiq.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Domain model representing a coaching message delivered during a run.
 */
@Entity(tableName = "coaching_messages")
@JsonClass(generateAdapter = true)
data class CoachingMessage(
    @PrimaryKey 
    val id: String = UUID.randomUUID().toString(),
    
    val message: String,
    val category: Category,
    val timestamp: Long = System.currentTimeMillis(),
    val isLLMGenerated: Boolean = false,
    
    // Optional metadata for context
    val runContext: RunContext? = null,
    val audioUrl: String? = null, // URL to generated audio file
    val delivered: Boolean = false
) {
    
    @JsonClass(generateAdapter = false)
    enum class Category {
        @Json(name = "encouragement") ENCOURAGEMENT,
        @Json(name = "pace_guidance") PACE_GUIDANCE,
        @Json(name = "form_tips") FORM_TIPS,
        @Json(name = "milestone") MILESTONE,
        @Json(name = "warning") WARNING,
        @Json(name = "completion") COMPLETION,
        @Json(name = "motivation") MOTIVATION,
        @Json(name = "technique") TECHNIQUE
    }
    
    @JsonClass(generateAdapter = true)
    data class RunContext(
        val currentPace: Float? = null,
        val targetPace: Float? = null,
        val heartRate: Int? = null,
        val distanceCovered: Float? = null,
        val timeElapsed: Long? = null,
        val effortLevel: WorkoutType.EffortLevel? = null
    )
}