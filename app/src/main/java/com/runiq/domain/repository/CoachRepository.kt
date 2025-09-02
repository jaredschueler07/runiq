package com.runiq.domain.repository

import com.runiq.core.util.Result
import com.runiq.data.local.entities.Coach
import com.runiq.data.local.entities.CoachTextLine
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for coach-related operations
 */
interface CoachRepository {

    /**
     * Observe all active coaches
     */
    fun observeActiveCoaches(): Flow<List<Coach>>

    /**
     * Get all available coaches
     */
    suspend fun getAllCoaches(): Result<List<Coach>>

    /**
     * Get coach by ID
     */
    suspend fun getCoachById(coachId: String): Result<Coach?>

    /**
     * Observe specific coach
     */
    fun observeCoachById(coachId: String): Flow<Coach?>

    /**
     * Get coaches by category
     */
    suspend fun getCoachesByCategory(category: String): Result<List<Coach>>

    /**
     * Get free coaches
     */
    suspend fun getFreeCoaches(): Result<List<Coach>>

    /**
     * Get premium coaches
     */
    suspend fun getPremiumCoaches(): Result<List<Coach>>

    /**
     * Generate coaching message using AI
     */
    suspend fun generateCoachingMessage(
        coachId: String,
        context: CoachingContext,
        category: String
    ): Result<CoachingMessage>

    /**
     * Get rule-based coaching message
     */
    suspend fun getRuleBasedMessage(
        coachId: String,
        category: String,
        conditions: Map<String, Any>
    ): Result<CoachTextLine?>

    /**
     * Convert text to speech
     */
    suspend fun textToSpeech(
        text: String,
        voiceId: String,
        voiceSettings: VoiceSettings? = null
    ): Result<ByteArray>

    /**
     * Update coach text lines
     */
    suspend fun updateCoachTextLines(
        coachId: String,
        textLines: List<CoachTextLine>
    ): Result<Unit>

    /**
     * Record coaching message usage
     */
    suspend fun recordMessageUsage(textLineId: String): Result<Unit>

    /**
     * Sync coaches from remote
     */
    suspend fun syncCoachesFromRemote(): Result<Unit>
}

/**
 * Coaching context for generating personalized messages
 */
data class CoachingContext(
    val currentPace: Float,
    val targetPace: Float?,
    val elapsedTime: Long,
    val distance: Float,
    val heartRate: Int?,
    val targetHeartRate: Int?,
    val workoutType: com.runiq.data.local.preferences.WorkoutType,
    val isFirstRun: Boolean,
    val recentPerformance: String, // IMPROVING, DECLINING, STABLE
    val weatherConditions: String?,
    val timeOfDay: String, // MORNING, AFTERNOON, EVENING
    val userFitnessLevel: com.runiq.data.local.preferences.FitnessLevel,
    val milestoneReached: Boolean = false,
    val isPaused: Boolean = false
)

/**
 * Generated coaching message
 */
data class CoachingMessage(
    val message: String,
    val category: String,
    val isLLMGenerated: Boolean,
    val voiceEnabled: Boolean,
    val priority: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Voice settings for text-to-speech
 */
data class VoiceSettings(
    val stability: Float = 0.5f,
    val similarityBoost: Float = 0.75f,
    val style: Float = 0.0f,
    val useSpeakerBoost: Boolean = true
)