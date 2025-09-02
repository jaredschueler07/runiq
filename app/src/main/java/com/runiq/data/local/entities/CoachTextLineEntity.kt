package com.runiq.data.local.entities

import androidx.annotation.Keep
import androidx.room.*
import com.runiq.domain.model.TextCategory

/**
 * Room entity representing pre-written coaching text lines for rule-based coaching
 */
@Keep
@Entity(
    tableName = "coach_text_lines",
    indices = [
        Index(value = ["coachId", "category"]),
        Index(value = ["category", "conditions"]),
        Index(value = ["priority", "isActive"]),
        Index(value = ["coachId", "isActive"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CoachEntity::class,
            parentColumns = ["id"],
            childColumns = ["coachId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CoachTextLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "coach_id")
    val coachId: String,
    
    @ColumnInfo(name = "text")
    val text: String,
    
    @ColumnInfo(name = "category")
    val category: TextCategory,
    
    @ColumnInfo(name = "conditions")
    val conditions: List<String> = emptyList(), // JSON array: ["pace:slower_than_target", "phase:main"]
    
    @ColumnInfo(name = "template_variables")
    val templateVariables: List<String> = emptyList(), // Variables like {pace}, {distance}, {time}
    
    @ColumnInfo(name = "priority")
    val priority: Int = 5, // 1-10, higher number = higher priority
    
    @ColumnInfo(name = "min_interval_seconds")
    val minIntervalSeconds: Int = 60, // Minimum time between uses of this line
    
    @ColumnInfo(name = "max_uses_per_run")
    val maxUsesPerRun: Int = 3, // Maximum times this line can be used in a single run
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0, // How many times this line has been used
    
    @ColumnInfo(name = "effectiveness_score")
    val effectivenessScore: Float? = null, // User feedback on this line's effectiveness
    
    @ColumnInfo(name = "language_code")
    val languageCode: String = "en", // For internationalization
    
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(), // Additional categorization tags
    
    @ColumnInfo(name = "emotional_tone")
    val emotionalTone: EmotionalTone = EmotionalTone.NEUTRAL
)

@Keep
enum class EmotionalTone {
    ENCOURAGING,
    CHALLENGING,
    CALMING,
    ENERGETIC,
    NEUTRAL,
    HUMOROUS,
    SERIOUS
}