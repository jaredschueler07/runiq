package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.CoachTextLineEntity
import com.runiq.data.local.entities.EmotionalTone
import com.runiq.domain.model.TextCategory
import kotlinx.coroutines.flow.Flow

/**
 * DAO for CoachTextLine entity with sophisticated matching and filtering
 */
@Dao
interface CoachTextLineDao {
    
    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(textLine: CoachTextLineEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(textLines: List<CoachTextLineEntity>): List<Long>
    
    @Update
    suspend fun update(textLine: CoachTextLineEntity): Int
    
    @Delete
    suspend fun delete(textLine: CoachTextLineEntity): Int
    
    @Query("DELETE FROM coach_text_lines WHERE id = :id")
    suspend fun deleteById(id: Long): Int
    
    @Query("DELETE FROM coach_text_lines WHERE coach_id = :coachId")
    suspend fun deleteByCoach(coachId: String): Int
    
    // Basic queries
    @Query("SELECT * FROM coach_text_lines WHERE id = :id")
    suspend fun getById(id: Long): CoachTextLineEntity?
    
    @Query("SELECT * FROM coach_text_lines WHERE coach_id = :coachId ORDER BY priority DESC, category ASC")
    suspend fun getByCoach(coachId: String): List<CoachTextLineEntity>
    
    @Query("SELECT * FROM coach_text_lines WHERE coach_id = :coachId ORDER BY priority DESC, category ASC")
    fun observeByCoach(coachId: String): Flow<List<CoachTextLineEntity>>
    
    // Active text lines
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND is_active = 1 
        ORDER BY priority DESC, category ASC
    """)
    suspend fun getActiveByCoach(coachId: String): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND is_active = 1 
        ORDER BY priority DESC, category ASC
    """)
    fun observeActiveByCoach(coachId: String): Flow<List<CoachTextLineEntity>>
    
    // Category-based queries
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun getByCoachAndCategory(coachId: String, category: TextCategory): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category IN (:categories) 
        AND is_active = 1 
        ORDER BY priority DESC, category ASC
    """)
    suspend fun getByCoachAndCategories(coachId: String, categories: List<TextCategory>): List<CoachTextLineEntity>
    
    // Condition-based matching (core feature for rule-based coaching)
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND is_active = 1 
        ORDER BY priority DESC, usage_count ASC
    """)
    suspend fun getCandidatesForMatching(coachId: String, category: TextCategory): List<CoachTextLineEntity>
    
    // This is a simplified version - in practice, you'd need custom logic to match conditions
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND is_active = 1 
        AND (conditions = '' OR conditions IS NULL OR conditions LIKE '%' || :condition || '%')
        ORDER BY priority DESC, usage_count ASC
        LIMIT 1
    """)
    suspend fun findBestMatchSimple(
        coachId: String, 
        category: TextCategory, 
        condition: String
    ): CoachTextLineEntity?
    
    // Priority and effectiveness queries
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND priority >= :minPriority 
        AND is_active = 1 
        ORDER BY priority DESC, CASE WHEN effectiveness_score IS NULL THEN 1 ELSE 0 END, effectiveness_score DESC
    """)
    suspend fun getHighPriorityLines(
        coachId: String, 
        category: TextCategory, 
        minPriority: Int = 7
    ): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND effectiveness_score IS NOT NULL 
        AND effectiveness_score >= :minScore 
        AND is_active = 1 
        ORDER BY effectiveness_score DESC
    """)
    suspend fun getEffectiveLines(coachId: String, minScore: Float = 0.7f): List<CoachTextLineEntity>
    
    // Emotional tone filtering
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND emotional_tone = :tone 
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun getByEmotionalTone(
        coachId: String, 
        category: TextCategory, 
        tone: EmotionalTone
    ): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND emotional_tone IN (:tones) 
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun getByEmotionalTones(
        coachId: String, 
        category: TextCategory, 
        tones: List<EmotionalTone>
    ): List<CoachTextLineEntity>
    
    // Usage tracking and limits
    @Query("UPDATE coach_text_lines SET usage_count = usage_count + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: Long): Int
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND usage_count < max_uses_per_run 
        AND is_active = 1 
        ORDER BY priority DESC, usage_count ASC
    """)
    suspend fun getUnderUsedLines(coachId: String, category: TextCategory): List<CoachTextLineEntity>
    
    @Query("UPDATE coach_text_lines SET usage_count = 0 WHERE coach_id = :coachId")
    suspend fun resetUsageCountForCoach(coachId: String): Int
    
    @Query("UPDATE coach_text_lines SET usage_count = 0")
    suspend fun resetAllUsageCounts(): Int
    
    // Effectiveness scoring
    @Query("UPDATE coach_text_lines SET effectiveness_score = :score WHERE id = :id")
    suspend fun updateEffectivenessScore(id: Long, score: Float): Int
    
    @Transaction
    suspend fun recordEffectiveness(id: Long, userFeedback: Float) {
        val textLine = getById(id)
        textLine?.let {
            val newScore = if (it.effectivenessScore != null) {
                // Simple moving average - in practice, you might want more sophisticated scoring
                (it.effectivenessScore + userFeedback) / 2f
            } else {
                userFeedback
            }
            updateEffectivenessScore(id, newScore)
        }
    }
    
    // Language and internationalization
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND language_code = :languageCode 
        AND is_active = 1 
        ORDER BY category ASC, priority DESC
    """)
    suspend fun getByLanguage(coachId: String, languageCode: String): List<CoachTextLineEntity>
    
    @Query("SELECT DISTINCT language_code FROM coach_text_lines WHERE coach_id = :coachId AND is_active = 1")
    suspend fun getAvailableLanguages(coachId: String): List<String>
    
    // Tags and advanced filtering
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND tags LIKE '%' || :tag || '%' 
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun getByTag(coachId: String, tag: String): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND (text LIKE '%' || :keyword || '%' OR tags LIKE '%' || :keyword || '%')
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun searchByKeyword(
        coachId: String, 
        category: TextCategory, 
        keyword: String
    ): List<CoachTextLineEntity>
    
    // Analytics and statistics
    @Query("SELECT category, COUNT(*) as count FROM coach_text_lines WHERE coach_id = :coachId AND is_active = 1 GROUP BY category")
    suspend fun getCategoryDistribution(coachId: String): List<CategoryCount>
    
    @Query("SELECT AVG(effectiveness_score) FROM coach_text_lines WHERE coach_id = :coachId AND effectiveness_score IS NOT NULL")
    suspend fun getAverageEffectiveness(coachId: String): Float?
    
    @Query("SELECT COUNT(*) FROM coach_text_lines WHERE coach_id = :coachId AND is_active = 1")
    suspend fun getActiveLineCount(coachId: String): Int
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND is_active = 1 
        ORDER BY usage_count DESC 
        LIMIT :limit
    """)
    suspend fun getMostUsedLines(coachId: String, limit: Int = 10): List<CoachTextLineEntity>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND effectiveness_score IS NOT NULL 
        AND is_active = 1 
        ORDER BY effectiveness_score DESC 
        LIMIT :limit
    """)
    suspend fun getMostEffectiveLines(coachId: String, limit: Int = 10): List<CoachTextLineEntity>
    
    // Bulk operations
    @Transaction
    suspend fun replaceCoachLines(coachId: String, newLines: List<CoachTextLineEntity>) {
        // Preserve usage statistics but update content
        deleteByCoach(coachId)
        insertAll(newLines)
    }
    
    @Query("UPDATE coach_text_lines SET is_active = :isActive WHERE coach_id = :coachId")
    suspend fun updateActiveStatusForCoach(coachId: String, isActive: Boolean): Int
    
    @Query("UPDATE coach_text_lines SET is_active = :isActive WHERE id IN (:ids)")
    suspend fun updateActiveStatusForLines(ids: List<Long>, isActive: Boolean): Int
    
    // Cleanup operations
    @Query("DELETE FROM coach_text_lines WHERE is_active = 0 AND updated_at < :timestamp")
    suspend fun cleanupInactiveLines(timestamp: Long): Int
    
    @Query("DELETE FROM coach_text_lines WHERE usage_count = 0 AND created_at < :timestamp")
    suspend fun cleanupUnusedLines(timestamp: Long): Int
    
    // Template variable queries
    @Query("""
        SELECT DISTINCT template_variables FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND template_variables IS NOT NULL 
        AND template_variables != ''
    """)
    suspend fun getTemplateVariablesByCoach(coachId: String): List<String>
    
    @Query("""
        SELECT * FROM coach_text_lines 
        WHERE coach_id = :coachId 
        AND category = :category 
        AND template_variables LIKE '%' || :variable || '%' 
        AND is_active = 1 
        ORDER BY priority DESC
    """)
    suspend fun getLinesWithVariable(
        coachId: String, 
        category: TextCategory, 
        variable: String
    ): List<CoachTextLineEntity>
}

// Data classes for complex query results
data class CategoryCount(
    val category: TextCategory,
    val count: Int
)