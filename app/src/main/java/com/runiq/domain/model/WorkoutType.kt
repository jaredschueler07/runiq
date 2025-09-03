package com.runiq.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Enum representing different types of running workouts.
 * Each type has specific characteristics for coaching and pacing.
 */
@JsonClass(generateAdapter = false)
enum class WorkoutType(
    val displayName: String,
    val description: String,
    val targetEffortLevel: EffortLevel,
    val typicalDurationMinutes: Int
) {
    @Json(name = "easy_run")
    EASY_RUN(
        displayName = "Easy Run",
        description = "Comfortable, conversational pace for building aerobic base",
        targetEffortLevel = EffortLevel.EASY,
        typicalDurationMinutes = 45
    ),
    
    @Json(name = "tempo_run")
    TEMPO_RUN(
        displayName = "Tempo Run",
        description = "Comfortably hard pace, sustainable for 45-60 minutes",
        targetEffortLevel = EffortLevel.MODERATE,
        typicalDurationMinutes = 30
    ),
    
    @Json(name = "interval_training")
    INTERVAL_TRAINING(
        displayName = "Interval Training",
        description = "High-intensity intervals with recovery periods",
        targetEffortLevel = EffortLevel.HARD,
        typicalDurationMinutes = 25
    ),
    
    @Json(name = "long_run")
    LONG_RUN(
        displayName = "Long Run",
        description = "Extended duration run for endurance building",
        targetEffortLevel = EffortLevel.EASY,
        typicalDurationMinutes = 90
    ),
    
    @Json(name = "recovery_run")
    RECOVERY_RUN(
        displayName = "Recovery Run",
        description = "Very easy pace for active recovery",
        targetEffortLevel = EffortLevel.VERY_EASY,
        typicalDurationMinutes = 30
    );
    
    enum class EffortLevel {
        VERY_EASY,
        EASY,
        MODERATE,
        HARD,
        VERY_HARD
    }
}