package com.runiq.domain.model

import androidx.annotation.Keep

/**
 * Domain model representing an AI running coach
 */
@Keep
data class Coach(
    val id: String,
    val name: String,
    val description: String,
    val coachingStyle: CoachingStyle,
    val personalityTraits: List<String>,
    val voiceCharacteristics: VoiceCharacteristics,
    val specializations: List<String> = emptyList(),
    val experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val motivationStyle: MotivationStyle = MotivationStyle.BALANCED,
    val isActive: Boolean = true,
    val isPremium: Boolean = false,
    val avatarUrl: String? = null,
    val backgroundStory: String? = null,
    val version: Int = 1,
    val usageCount: Int = 0,
    val averageRating: Float? = null,
    val totalRatings: Int = 0
) {
    
    /**
     * Check if this coach is suitable for a specific workout type
     */
    fun isSuitableFor(workoutType: WorkoutType): Boolean {
        return when (workoutType) {
            WorkoutType.EASY_RUN -> specializations.contains("beginner") || 
                                   coachingStyle == CoachingStyle.ENCOURAGING
            WorkoutType.INTERVAL_RUN -> specializations.contains("speed") || 
                                       coachingStyle == CoachingStyle.CHALLENGING
            WorkoutType.TEMPO_RUN -> specializations.contains("performance") || 
                                    coachingStyle == CoachingStyle.ANALYTICAL
            WorkoutType.LONG_RUN -> specializations.contains("endurance") || 
                                   motivationStyle == MotivationStyle.GENTLE
            else -> true // All coaches can handle other workout types
        }
    }
    
    /**
     * Get display rating with fallback
     */
    fun getDisplayRating(): String {
        return averageRating?.let { String.format("%.1f", it) } ?: "New"
    }
    
    /**
     * Check if coach has good ratings
     */
    fun isHighlyRated(threshold: Float = 4.0f): Boolean {
        return averageRating != null && averageRating >= threshold && totalRatings >= 5
    }
    
    /**
     * Get experience level description
     */
    fun getExperienceDescription(): String {
        return when (experienceLevel) {
            ExperienceLevel.BEGINNER -> "Perfect for new runners"
            ExperienceLevel.INTERMEDIATE -> "Great for regular runners"
            ExperienceLevel.ADVANCED -> "Ideal for experienced athletes"
            ExperienceLevel.ELITE -> "For competitive runners"
        }
    }
    
    /**
     * Get coaching style description
     */
    fun getCoachingStyleDescription(): String {
        return when (coachingStyle) {
            CoachingStyle.ENCOURAGING -> "Positive and supportive approach"
            CoachingStyle.CHALLENGING -> "Pushes you to exceed your limits"
            CoachingStyle.ANALYTICAL -> "Data-driven and technical guidance"
            CoachingStyle.MINDFUL -> "Focus on form and breathing"
            CoachingStyle.HUMOROUS -> "Light-hearted and fun coaching"
            CoachingStyle.PROFESSIONAL -> "Serious and structured approach"
        }
    }
}

@Keep
enum class CoachingStyle {
    ENCOURAGING,    // Positive, supportive
    CHALLENGING,    // Push harder, competitive
    ANALYTICAL,     // Data-focused, technical
    MINDFUL,        // Focus on form and breathing
    HUMOROUS,       // Light-hearted, fun
    PROFESSIONAL    // Serious, structured
}

@Keep
enum class ExperienceLevel {
    BEGINNER,       // New to running
    INTERMEDIATE,   // Regular runner
    ADVANCED,       // Experienced athlete
    ELITE          // Professional level
}

@Keep
enum class MotivationStyle {
    GENTLE,         // Soft encouragement
    BALANCED,       // Mix of push and support
    INTENSE,        // High energy, demanding
    TACTICAL        // Strategy and technique focused
}