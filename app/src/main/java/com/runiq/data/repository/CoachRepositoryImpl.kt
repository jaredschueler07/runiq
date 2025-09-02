package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.CoachDao
import com.runiq.data.local.dao.CoachTextLineDao
import com.runiq.data.local.entities.Coach
import com.runiq.data.local.entities.CoachTextLine
import com.runiq.data.remote.api.GeminiApiService
import com.runiq.data.remote.api.ElevenLabsApiService
import com.runiq.domain.repository.CoachRepository
import com.runiq.domain.repository.CoachingContext
import com.runiq.domain.repository.CoachingMessage
import com.runiq.domain.repository.VoiceSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CoachRepository
 * Manages AI coaches, text-to-speech, and coaching messages
 */
@Singleton
class CoachRepositoryImpl @Inject constructor(
    private val coachDao: CoachDao,
    private val coachTextLineDao: CoachTextLineDao,
    private val geminiApiService: GeminiApiService,
    private val elevenLabsApiService: ElevenLabsApiService
) : BaseRepository(), CoachRepository {

    override fun observeActiveCoaches(): Flow<List<Coach>> {
        return coachDao.observeActiveCoaches()
    }

    override suspend fun getAllCoaches(): Result<List<Coach>> {
        return safeDatabaseCall {
            coachDao.getAll()
        }
    }

    override suspend fun getCoachById(coachId: String): Result<Coach?> {
        return safeDatabaseCall {
            coachDao.getById(coachId)
        }
    }

    override fun observeCoachById(coachId: String): Flow<Coach?> {
        return coachDao.observeById(coachId)
    }

    override suspend fun getCoachesByCategory(category: String): Result<List<Coach>> {
        return safeDatabaseCall {
            coachDao.getByCategory(category)
        }
    }

    override suspend fun getFreeCoaches(): Result<List<Coach>> {
        return safeDatabaseCall {
            coachDao.getFreeCoaches()
        }
    }

    override suspend fun getPremiumCoaches(): Result<List<Coach>> {
        return safeDatabaseCall {
            coachDao.getPremiumCoaches()
        }
    }

    override suspend fun generateCoachingMessage(
        coachId: String,
        context: CoachingContext,
        category: String
    ): Result<CoachingMessage> {
        // TODO: Implement AI message generation
        // 1. Try rule-based first
        // 2. Fall back to LLM if needed
        // 3. Return generic message as last resort
        
        return getRuleBasedMessage(coachId, category, mapOf()).mapData { textLine ->
            CoachingMessage(
                message = textLine?.text ?: "Keep it up! You're doing great!",
                category = category,
                isLLMGenerated = false,
                voiceEnabled = true
            )
        }
    }

    override suspend fun getRuleBasedMessage(
        coachId: String,
        category: String,
        conditions: Map<String, Any>
    ): Result<CoachTextLine?> {
        return safeDatabaseCall {
            coachTextLineDao.findBestMatch(coachId, category)
        }
    }

    override suspend fun textToSpeech(
        text: String,
        voiceId: String,
        voiceSettings: VoiceSettings?
    ): Result<ByteArray> {
        // TODO: Implement ElevenLabs TTS
        return Result.Error(NotImplementedError("Text-to-speech not yet implemented"))
    }

    override suspend fun updateCoachTextLines(
        coachId: String,
        textLines: List<CoachTextLine>
    ): Result<Unit> {
        return safeDatabaseCall {
            // Delete existing lines for this coach
            coachTextLineDao.deleteByCoachId(coachId)
            
            // Insert new lines
            coachTextLineDao.insertAll(textLines)
            Unit
        }
    }

    override suspend fun recordMessageUsage(textLineId: String): Result<Unit> {
        return safeDatabaseCall {
            coachTextLineDao.incrementUsage(textLineId, System.currentTimeMillis())
        }
    }

    override suspend fun syncCoachesFromRemote(): Result<Unit> {
        // TODO: Implement remote coach sync
        return Result.Success(Unit)
    }
}