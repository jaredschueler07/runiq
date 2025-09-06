package com.runiq.data.local.entities

import androidx.annotation.Keep
import androidx.room.*
import com.runiq.domain.model.VoiceCharacteristics

/**
 * Room entity representing an AI running coach
 */
@Keep
@Entity(
    tableName = "coaches",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["is_active"]),
        Index(value = ["coaching_style"])
    ]
)
data class CoachEntity(
    @PrimaryKey 
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "coaching_style")
    val coachingStyle: CoachingStyle,
    
    @ColumnInfo(name = "personality_traits")
    val personalityTraits: List<String>, // JSON array via TypeConverter
    
    @ColumnInfo(name = "voice_characteristics")
    val voiceCharacteristics: VoiceCharacteristics, // JSON via TypeConverter
    
    @ColumnInfo(name = "specializations")
    val specializations: List<String> = emptyList(), // e.g., ["marathon", "speed", "beginner"]
    
    @ColumnInfo(name = "experience_level")
    val experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    
    @ColumnInfo(name = "motivation_style")
    val motivationStyle: MotivationStyle = MotivationStyle.BALANCED,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = false,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,
    
    @ColumnInfo(name = "background_story")
    val backgroundStory: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "version")
    val version: Int = 1, // For coach content updates
    
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0, // How many times this coach has been used
    
    @ColumnInfo(name = "average_rating")
    val averageRating: Float? = null, // User ratings 1.0-5.0
    
    @ColumnInfo(name = "total_ratings")
    val totalRatings: Int = 0
)

