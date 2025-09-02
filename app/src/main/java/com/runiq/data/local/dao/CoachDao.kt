package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.runiq.data.local.entities.Coach
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Coach entity operations
 */
@Dao
abstract class CoachDao : BaseIdDao<Coach, String>() {

    @Query("SELECT * FROM coaches ORDER BY name ASC")
    abstract override fun observeAll(): Flow<List<Coach>>

    @Query("SELECT * FROM coaches ORDER BY name ASC")
    abstract override suspend fun getAll(): List<Coach>

    @Query("SELECT COUNT(*) FROM coaches")
    abstract override suspend fun getCount(): Int

    @Query("DELETE FROM coaches")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT * FROM coaches WHERE id = :id")
    abstract override suspend fun getById(id: String): Coach?

    @Query("SELECT * FROM coaches WHERE id = :id")
    abstract override fun observeById(id: String): Flow<Coach?>

    @Query("DELETE FROM coaches WHERE id = :id")
    abstract override suspend fun deleteById(id: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM coaches WHERE id = :id)")
    abstract override suspend fun existsById(id: String): Boolean

    @Query("SELECT * FROM coaches WHERE id IN (:ids)")
    abstract override suspend fun getByIds(ids: List<String>): List<Coach>

    @Query("DELETE FROM coaches WHERE id IN (:ids)")
    abstract override suspend fun deleteByIds(ids: List<String>): Int

    // Custom queries specific to Coach
    
    @Query("SELECT * FROM coaches WHERE isActive = 1 ORDER BY name ASC")
    abstract fun observeActiveCoaches(): Flow<List<Coach>>

    @Query("SELECT * FROM coaches WHERE isActive = 1 ORDER BY name ASC")
    abstract suspend fun getActiveCoaches(): List<Coach>

    @Query("SELECT * FROM coaches WHERE category = :category AND isActive = 1 ORDER BY name ASC")
    abstract suspend fun getByCategory(category: String): List<Coach>

    @Query("SELECT * FROM coaches WHERE isPremium = 0 AND isActive = 1 ORDER BY name ASC")
    abstract suspend fun getFreeCoaches(): List<Coach>

    @Query("SELECT * FROM coaches WHERE isPremium = 1 AND isActive = 1 ORDER BY name ASC")
    abstract suspend fun getPremiumCoaches(): List<Coach>

    @Query("SELECT * FROM coaches WHERE isCustom = 1 ORDER BY name ASC")
    abstract suspend fun getCustomCoaches(): List<Coach>

    @Query("UPDATE coaches SET isActive = :isActive WHERE id = :id")
    abstract suspend fun updateActiveStatus(id: String, isActive: Boolean)

    @Query("SELECT * FROM coaches WHERE experienceLevel = :level AND isActive = 1 ORDER BY name ASC")
    abstract suspend fun getByExperienceLevel(level: String): List<Coach>

    @Query("SELECT * FROM coaches WHERE voiceId IS NOT NULL AND isActive = 1 ORDER BY name ASC")
    abstract suspend fun getCoachesWithVoice(): List<Coach>
}

/**
 * DAO for CoachTextLine entity operations
 */
@Dao
abstract class CoachTextLineDao : BaseIdDao<CoachTextLine, String>() {

    @Query("SELECT * FROM coach_text_lines ORDER BY priority DESC, usageCount ASC")
    abstract override fun observeAll(): Flow<List<CoachTextLine>>

    @Query("SELECT * FROM coach_text_lines ORDER BY priority DESC, usageCount ASC")
    abstract override suspend fun getAll(): List<CoachTextLine>

    @Query("SELECT COUNT(*) FROM coach_text_lines")
    abstract override suspend fun getCount(): Int

    @Query("DELETE FROM coach_text_lines")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT * FROM coach_text_lines WHERE id = :id")
    abstract override suspend fun getById(id: String): CoachTextLine?

    @Query("SELECT * FROM coach_text_lines WHERE id = :id")
    abstract override fun observeById(id: String): Flow<CoachTextLine?>

    @Query("DELETE FROM coach_text_lines WHERE id = :id")
    abstract override suspend fun deleteById(id: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM coach_text_lines WHERE id = :id)")
    abstract override suspend fun existsById(id: String): Boolean

    @Query("SELECT * FROM coach_text_lines WHERE id IN (:ids)")
    abstract override suspend fun getByIds(ids: List<String>): List<CoachTextLine>

    @Query("DELETE FROM coach_text_lines WHERE id IN (:ids)")
    abstract override suspend fun deleteByIds(ids: List<String>): Int

    // Custom queries specific to CoachTextLine
    
    @Query("SELECT * FROM coach_text_lines WHERE coachId = :coachId AND isActive = 1 ORDER BY priority DESC, usageCount ASC")
    abstract suspend fun getByCoachId(coachId: String): List<CoachTextLine>

    @Query("SELECT * FROM coach_text_lines WHERE category = :category AND isActive = 1 ORDER BY priority DESC, usageCount ASC")
    abstract suspend fun getByCategory(category: String): List<CoachTextLine>

    @Query("SELECT * FROM coach_text_lines WHERE coachId = :coachId AND category = :category AND isActive = 1 ORDER BY priority DESC, usageCount ASC LIMIT 1")
    abstract suspend fun findBestMatch(coachId: String, category: String): CoachTextLine?

    @Query("UPDATE coach_text_lines SET usageCount = usageCount + 1, lastUsed = :timestamp WHERE id = :id")
    abstract suspend fun incrementUsage(id: String, timestamp: Long)

    @Query("SELECT * FROM coach_text_lines WHERE coachId = :coachId AND category = :category AND isActive = 1 ORDER BY RANDOM() LIMIT 1")
    abstract suspend fun getRandomByCoachAndCategory(coachId: String, category: String): CoachTextLine?

    @Query("DELETE FROM coach_text_lines WHERE coachId = :coachId")
    abstract suspend fun deleteByCoachId(coachId: String): Int

    @Query("UPDATE coach_text_lines SET isActive = :isActive WHERE coachId = :coachId")
    abstract suspend fun updateActiveStatusByCoachId(coachId: String, isActive: Boolean)
}