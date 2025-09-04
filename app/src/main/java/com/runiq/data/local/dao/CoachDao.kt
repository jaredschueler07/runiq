package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.CoachEntity
import com.runiq.data.local.entities.CoachingStyle
import com.runiq.data.local.entities.ExperienceLevel
import com.runiq.data.local.entities.MotivationStyle
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Coach entity with comprehensive operations
 */
@Dao
interface CoachDao {
    
    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coach: CoachEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coaches: List<CoachEntity>): List<Long>
    
    @Update
    suspend fun update(coach: CoachEntity): Int
    
    @Delete
    suspend fun delete(coach: CoachEntity): Int
    
    @Query("DELETE FROM coaches WHERE id = :coachId")
    suspend fun deleteById(coachId: String): Int
    
    // Basic queries
    @Query("SELECT * FROM coaches WHERE id = :coachId")
    suspend fun getById(coachId: String): CoachEntity?
    
    @Query("SELECT * FROM coaches WHERE id = :coachId")
    fun observeById(coachId: String): Flow<CoachEntity?>
    
    @Query("SELECT * FROM coaches ORDER BY name ASC")
    suspend fun getAll(): List<CoachEntity>
    
    @Query("SELECT * FROM coaches ORDER BY name ASC")
    fun observeAll(): Flow<List<CoachEntity>>
    
    // Active coaches
    @Query("SELECT * FROM coaches WHERE is_active = 1 ORDER BY name ASC")
    suspend fun getActiveCoaches(): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE is_active = 1 ORDER BY name ASC")
    fun observeActiveCoaches(): Flow<List<CoachEntity>>
    
    // Premium/Free filtering
    @Query("SELECT * FROM coaches WHERE is_premium = :isPremium AND is_active = 1 ORDER BY name ASC")
    suspend fun getByPremiumStatus(isPremium: Boolean): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE is_premium = 0 AND is_active = 1 ORDER BY name ASC")
    suspend fun getFreeCoaches(): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE is_premium = 1 AND is_active = 1 ORDER BY name ASC")
    suspend fun getPremiumCoaches(): List<CoachEntity>
    
    // Filter by coaching characteristics
    @Query("SELECT * FROM coaches WHERE coaching_style = :style AND is_active = 1 ORDER BY name ASC")
    suspend fun getByCoachingStyle(style: CoachingStyle): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE coaching_style IN (:styles) AND is_active = 1 ORDER BY name ASC")
    suspend fun getByCoachingStyles(styles: List<CoachingStyle>): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE experience_level = :level AND is_active = 1 ORDER BY name ASC")
    suspend fun getByExperienceLevel(level: ExperienceLevel): List<CoachEntity>
    
    @Query("SELECT * FROM coaches WHERE motivation_style = :style AND is_active = 1 ORDER BY name ASC")
    suspend fun getByMotivationStyle(style: MotivationStyle): List<CoachEntity>
    
    // Search and filtering
    @Query("""
        SELECT * FROM coaches 
        WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') 
        AND is_active = 1 
        ORDER BY name ASC
    """)
    suspend fun searchCoaches(query: String): List<CoachEntity>
    
    @Query("""
        SELECT * FROM coaches 
        WHERE specializations LIKE '%' || :specialization || '%' 
        AND is_active = 1 
        ORDER BY average_rating DESC, name ASC
    """)
    suspend fun getBySpecialization(specialization: String): List<CoachEntity>
    
    // Popularity and ratings
    @Query("""
        SELECT * FROM coaches 
        WHERE is_active = 1 
        AND average_rating IS NOT NULL 
        ORDER BY average_rating DESC, total_ratings DESC 
        LIMIT :limit
    """)
    suspend fun getTopRatedCoaches(limit: Int = 10): List<CoachEntity>
    
    @Query("""
        SELECT * FROM coaches 
        WHERE is_active = 1 
        ORDER BY usage_count DESC, average_rating DESC 
        LIMIT :limit
    """)
    suspend fun getMostPopularCoaches(limit: Int = 10): List<CoachEntity>
    
    @Query("""
        SELECT * FROM coaches 
        WHERE is_active = 1 
        AND average_rating >= :minRating 
        ORDER BY average_rating DESC, total_ratings DESC
    """)
    suspend fun getHighRatedCoaches(minRating: Float = 4.0f): List<CoachEntity>
    
    // Usage statistics
    @Query("UPDATE coaches SET usage_count = usage_count + 1 WHERE id = :coachId")
    suspend fun incrementUsageCount(coachId: String): Int
    
    @Query("""
        UPDATE coaches 
        SET average_rating = :newRating, total_ratings = total_ratings + 1 
        WHERE id = :coachId
    """)
    suspend fun updateRating(coachId: String, newRating: Float): Int
    
    @Transaction
    suspend fun addRating(coachId: String, rating: Float) {
        val coach = getById(coachId)
        coach?.let {
            val totalRatings = it.totalRatings + 1
            val newAverage = if (it.averageRating != null) {
                ((it.averageRating * it.totalRatings) + rating) / totalRatings
            } else {
                rating
            }
            updateRatingTransaction(coachId, newAverage, totalRatings)
        }
    }
    
    @Query("""
        UPDATE coaches 
        SET average_rating = :averageRating, total_ratings = :totalRatings 
        WHERE id = :coachId
    """)
    suspend fun updateRatingTransaction(coachId: String, averageRating: Float, totalRatings: Int): Int
    
    // Version and content management
    @Query("SELECT * FROM coaches WHERE version < :currentVersion")
    suspend fun getOutdatedCoaches(currentVersion: Int): List<CoachEntity>
    
    @Query("UPDATE coaches SET version = :version, updated_at = :timestamp WHERE id = :coachId")
    suspend fun updateVersion(coachId: String, version: Int, timestamp: Long = System.currentTimeMillis()): Int
    
    @Query("UPDATE coaches SET is_active = :isActive WHERE id = :coachId")
    suspend fun updateActiveStatus(coachId: String, isActive: Boolean): Int
    
    // Bulk operations
    @Transaction
    suspend fun replaceAllCoaches(coaches: List<CoachEntity>) {
        // Keep usage statistics but update content
        coaches.forEach { newCoach ->
            val existing = getById(newCoach.id)
            val updatedCoach = if (existing != null) {
                newCoach.copy(
                    usageCount = existing.usageCount,
                    averageRating = existing.averageRating,
                    totalRatings = existing.totalRatings
                )
            } else {
                newCoach
            }
            insert(updatedCoach)
        }
    }
    
    @Query("UPDATE coaches SET is_active = 0 WHERE id NOT IN (:activeCoachIds)")
    suspend fun deactivateCoachesNotIn(activeCoachIds: List<String>): Int
    
    // Recommendations
    @Query("""
        SELECT * FROM coaches 
        WHERE is_active = 1 
        AND (
            coaching_style = :preferredStyle 
            OR motivation_style = :preferredMotivation 
            OR experience_level = :preferredExperience
        )
        ORDER BY 
            CASE WHEN coaching_style = :preferredStyle THEN 3 ELSE 0 END +
            CASE WHEN motivation_style = :preferredMotivation THEN 2 ELSE 0 END +
            CASE WHEN experience_level = :preferredExperience THEN 1 ELSE 0 END DESC,
            average_rating DESC,
            usage_count DESC
        LIMIT :limit
    """)
    suspend fun getRecommendedCoaches(
        preferredStyle: CoachingStyle,
        preferredMotivation: MotivationStyle,
        preferredExperience: ExperienceLevel,
        limit: Int = 5
    ): List<CoachEntity>
    
    // Analytics queries
    @Query("SELECT coaching_style, COUNT(*) as count FROM coaches WHERE is_active = 1 GROUP BY coaching_style")
    suspend fun getCoachingStyleDistribution(): List<CoachingStyleCount>
    
    @Query("SELECT COUNT(*) FROM coaches WHERE is_active = 1")
    suspend fun getActiveCoachCount(): Int
    
    @Query("SELECT COUNT(*) FROM coaches WHERE is_premium = 1 AND is_active = 1")
    suspend fun getPremiumCoachCount(): Int
    
    @Query("SELECT AVG(average_rating) FROM coaches WHERE average_rating IS NOT NULL AND is_active = 1")
    suspend fun getOverallAverageRating(): Float?
    
    // Cleanup operations
    @Query("DELETE FROM coaches WHERE is_active = 0 AND updated_at < :timestamp")
    suspend fun cleanupInactiveCoaches(timestamp: Long): Int
    
    @Query("UPDATE coaches SET updated_at = :timestamp WHERE id = :coachId")
    suspend fun touch(coachId: String, timestamp: Long = System.currentTimeMillis()): Int
}

// Data classes for complex query results
data class CoachingStyleCount(
    val coachingStyle: CoachingStyle,
    val count: Int
)