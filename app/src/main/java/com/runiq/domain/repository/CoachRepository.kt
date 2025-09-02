package com.runiq.domain.repository

import com.runiq.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AI coach operations
 */
interface CoachRepository {
    
    // Coach management
    suspend fun getAllCoaches(): Result<List<Coach>>
    fun observeAllCoaches(): Flow<List<Coach>>
    suspend fun getActiveCoaches(): Result<List<Coach>>
    fun observeActiveCoaches(): Flow<List<Coach>>
    suspend fun getCoachById(coachId: String): Result<Coach?>
    fun observeCoachById(coachId: String): Flow<Coach?>
    
    // Coach filtering and search
    suspend fun getFreeCoaches(): Result<List<Coach>>
    suspend fun getPremiumCoaches(): Result<List<Coach>>
    suspend fun getCoachesByStyle(style: CoachingStyle): Result<List<Coach>>
    suspend fun getCoachesByExperience(level: ExperienceLevel): Result<List<Coach>>
    suspend fun searchCoaches(query: String): Result<List<Coach>>
    
    // Coach recommendations
    suspend fun getRecommendedCoaches(
        preferredStyle: CoachingStyle,
        motivationStyle: MotivationStyle,
        experienceLevel: ExperienceLevel,
        workoutType: WorkoutType? = null
    ): Result<List<Coach>>
    
    suspend fun getTopRatedCoaches(limit: Int = 10): Result<List<Coach>>
    suspend fun getMostPopularCoaches(limit: Int = 10): Result<List<Coach>>
    
    // Coach usage and ratings
    suspend fun recordCoachUsage(coachId: String): Result<Unit>
    suspend fun rateCoach(coachId: String, rating: Float): Result<Unit>
    suspend fun getCoachEffectiveness(coachId: String): Result<Float?>
    
    // Coach content management
    suspend fun updateCoachContent(coaches: List<Coach>): Result<Unit>
    suspend fun getOutdatedCoaches(currentVersion: Int): Result<List<Coach>>
    suspend fun syncCoachContent(): Result<Int>
}

/**
 * Repository interface for coaching text lines
 */
interface CoachingTextRepository {
    
    // Text line management
    suspend fun getTextLinesByCoach(coachId: String): Result<List<CoachingTextLine>>
    suspend fun getTextLinesByCategory(coachId: String, category: TextCategory): Result<List<CoachingTextLine>>
    
    // Rule-based coaching
    suspend fun findBestMatch(
        coachId: String,
        category: TextCategory,
        conditions: List<String>
    ): Result<CoachingTextLine?>
    
    suspend fun getContextualMessages(
        coachId: String,
        context: RunContext,
        category: TextCategory
    ): Result<List<CoachingTextLine>>
    
    // Usage tracking
    suspend fun recordTextLineUsage(textLineId: Long): Result<Unit>
    suspend fun recordEffectiveness(textLineId: Long, effectiveness: Float): Result<Unit>
    suspend fun resetUsageCountsForRun(coachId: String): Result<Unit>
    
    // Content updates
    suspend fun updateTextLines(coachId: String, textLines: List<CoachingTextLine>): Result<Unit>
    suspend fun syncTextLineContent(): Result<Int>
}

/**
 * Domain model for coaching text lines
 */
data class CoachingTextLine(
    val id: Long = 0,
    val coachId: String,
    val text: String,
    val category: TextCategory,
    val conditions: List<String> = emptyList(),
    val templateVariables: List<String> = emptyList(),
    val priority: Int = 5,
    val minIntervalSeconds: Int = 60,
    val maxUsesPerRun: Int = 3,
    val isActive: Boolean = true,
    val usageCount: Int = 0,
    val effectivenessScore: Float? = null,
    val languageCode: String = "en",
    val tags: List<String> = emptyList(),
    val emotionalTone: EmotionalTone = EmotionalTone.NEUTRAL
) {
    
    /**
     * Check if this text line matches the given conditions
     */
    fun matchesConditions(targetConditions: List<String>): Boolean {
        if (conditions.isEmpty()) return true // Universal match
        
        return conditions.any { condition ->
            targetConditions.any { target ->
                target.contains(condition, ignoreCase = true)
            }
        }
    }
    
    /**
     * Fill template variables in the text
     */
    fun fillTemplate(variables: Map<String, String>): String {
        var filledText = text
        templateVariables.forEach { variable ->
            val value = variables[variable] ?: ""
            filledText = filledText.replace("{$variable}", value)
        }
        return filledText
    }
    
    /**
     * Check if this line can be used (not exceeded max uses)
     */
    fun canBeUsed(): Boolean {
        return isActive && usageCount < maxUsesPerRun
    }
}

enum class EmotionalTone {
    ENCOURAGING,
    CHALLENGING,
    CALMING,
    ENERGETIC,
    NEUTRAL,
    HUMOROUS,
    SERIOUS
}