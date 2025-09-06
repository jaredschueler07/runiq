package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.CoachDao
import com.runiq.data.local.dao.CoachTextLineDao
import com.runiq.data.local.entities.CoachingStyle
import com.runiq.data.local.entities.ExperienceLevel
import com.runiq.data.local.entities.MotivationStyle
import com.runiq.data.mapper.toDomain
import com.runiq.data.mapper.toEntity
import com.runiq.data.remote.api.GeminiApiService
import com.runiq.data.remote.api.ElevenLabsApiService
import com.runiq.domain.model.*
import com.runiq.domain.repository.CoachRepository
import com.runiq.domain.repository.CoachingTextRepository
import com.runiq.domain.repository.CoachingTextLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
) : BaseRepository(), CoachRepository, CoachingTextRepository {

    // --- CoachRepository Implementation ---

    override suspend fun getAllCoaches(): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getAll().toDomain() }
    }

    override fun observeAllCoaches(): Flow<List<Coach>> {
        return coachDao.observeAll().map { it.toDomain() }
    }

    override suspend fun getActiveCoaches(): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getActiveCoaches().toDomain() }
    }

    override fun observeActiveCoaches(): Flow<List<Coach>> {
        return coachDao.observeActiveCoaches().map { it.toDomain() }
    }

    override suspend fun getCoachById(coachId: String): Result<Coach?> {
        return safeDatabaseCall { coachDao.getById(coachId)?.toDomain() }
    }

    override fun observeCoachById(coachId: String): Flow<Coach?> {
        return coachDao.observeById(coachId).map { it?.toDomain() }
    }

    override suspend fun getFreeCoaches(): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getFreeCoaches().toDomain() }
    }

    override suspend fun getPremiumCoaches(): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getPremiumCoaches().toDomain() }
    }

    override suspend fun getCoachesByStyle(style: CoachingStyle): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getByCoachingStyle(style).toDomain() }
    }

    override suspend fun getCoachesByExperience(level: ExperienceLevel): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getByExperienceLevel(level).toDomain() }
    }

    override suspend fun searchCoaches(query: String): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.searchCoaches(query).toDomain() }
    }

    override suspend fun getRecommendedCoaches(
        preferredStyle: CoachingStyle,
        motivationStyle: MotivationStyle,
        experienceLevel: ExperienceLevel,
        workoutType: WorkoutType?
    ): Result<List<Coach>> {
        return safeDatabaseCall {
            coachDao.getRecommendedCoaches(preferredStyle, motivationStyle, experienceLevel).toDomain()
        }
    }

    override suspend fun getTopRatedCoaches(limit: Int): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getTopRatedCoaches(limit).toDomain() }
    }

    override suspend fun getMostPopularCoaches(limit: Int): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getMostPopularCoaches(limit).toDomain() }
    }

    override suspend fun recordCoachUsage(coachId: String): Result<Unit> {
        return safeDatabaseCall { coachDao.incrementUsageCount(coachId) }
    }

    override suspend fun rateCoach(coachId: String, rating: Float): Result<Unit> {
        return safeDatabaseCall { coachDao.addRating(coachId, rating) }
    }

    override suspend fun getCoachEffectiveness(coachId: String): Result<Float?> {
        // This would likely be a more complex calculation involving RunSessionDao
        return Result.Error(NotImplementedError("getCoachEffectiveness not implemented"))
    }

    override suspend fun updateCoachContent(coaches: List<Coach>): Result<Unit> {
        return safeDatabaseCall { coachDao.replaceAllCoaches(coaches.map { it.toEntity() }) }
    }

    override suspend fun getOutdatedCoaches(currentVersion: Int): Result<List<Coach>> {
        return safeDatabaseCall { coachDao.getOutdatedCoaches(currentVersion).toDomain() }
    }

    override suspend fun syncCoachContent(): Result<Int> {
        // TODO: Implement remote coach sync logic
        return Result.Success(0)
    }

    // --- CoachingTextRepository Implementation ---

    override suspend fun getTextLinesByCoach(coachId: String): Result<List<CoachingTextLine>> {
        return safeDatabaseCall { coachTextLineDao.getByCoach(coachId).map { it.toDomain() } }
    }

    override suspend fun getTextLinesByCategory(coachId: String, category: TextCategory): Result<List<CoachingTextLine>> {
        return safeDatabaseCall {
            coachTextLineDao.getByCoachAndCategory(coachId, category).map { it.toDomain() }
        }
    }

    override suspend fun findBestMatch(
        coachId: String,
        category: TextCategory,
        conditions: List<String>
    ): Result<CoachingTextLine?> {
        return safeDatabaseCall {
            val candidates = coachTextLineDao.getCandidatesForMatching(coachId, category)
            // This is where more complex matching logic would go.
            // For now, we find the first one that matches any condition.
            candidates.find { it.toDomain().matchesConditions(conditions) }?.toDomain()
        }
    }

    override suspend fun getContextualMessages(
        coachId: String,
        context: RunContext,
        category: TextCategory
    ): Result<List<CoachingTextLine>> {
        // TODO: Implement contextual message logic based on RunContext
        return getTextLinesByCategory(coachId, category)
    }

    override suspend fun recordTextLineUsage(textLineId: Long): Result<Unit> {
        return safeDatabaseCall { coachTextLineDao.incrementUsageCount(textLineId) }
    }

    override suspend fun recordEffectiveness(textLineId: Long, effectiveness: Float): Result<Unit> {
        return safeDatabaseCall { coachTextLineDao.recordEffectiveness(textLineId, effectiveness) }
    }

    override suspend fun resetUsageCountsForRun(coachId: String): Result<Unit> {
        return safeDatabaseCall { coachTextLineDao.resetUsageCountForCoach(coachId) }
    }

    override suspend fun updateTextLines(coachId: String, textLines: List<CoachingTextLine>): Result<Unit> {
        return safeDatabaseCall {
            coachTextLineDao.replaceCoachLines(coachId, textLines.map { it.toEntity() })
        }
    }

    override suspend fun syncTextLineContent(): Result<Int> {
        // TODO: Implement remote text line sync logic
        return Result.Success(0)
    }
}