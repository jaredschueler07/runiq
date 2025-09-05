package com.runiq.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain model representing an AI running coach.
 * Each coach has unique personality, voice characteristics, and specializations.
 */
@Entity(tableName = "coaches")
@JsonClass(generateAdapter = true)
data class Coach(
    @PrimaryKey 
    val id: String,
    
    val name: String,
    val personality: Personality,
    val specialization: Specialization,
    
    @Embedded
    val voiceCharacteristics: VoiceCharacteristics,
    
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    
    @JsonClass(generateAdapter = false)
    enum class Personality {
        @Json(name = "motivational") MOTIVATIONAL,
        @Json(name = "analytical") ANALYTICAL,
        @Json(name = "supportive") SUPPORTIVE,
        @Json(name = "competitive") COMPETITIVE,
        @Json(name = "zen") ZEN
    }
    
    @JsonClass(generateAdapter = false)
    enum class Specialization {
        @Json(name = "endurance") ENDURANCE,
        @Json(name = "speed") SPEED,
        @Json(name = "strength") STRENGTH,
        @Json(name = "beginner") BEGINNER,
        @Json(name = "advanced") ADVANCED
    }
    
    @JsonClass(generateAdapter = true)
    data class VoiceCharacteristics(
        val voiceId: String, // Eleven Labs voice ID
        val stability: Float = 0.8f, // 0.0 to 1.0
        val clarity: Float = 0.9f, // 0.0 to 1.0
        val style: Float = 0.5f, // 0.0 to 1.0
        val speakingRate: Float = 1.0f // 0.5 to 2.0
    )
}