package com.runiq.domain.model

import androidx.annotation.Keep

/**
 * Represents different types of running workouts
 */
@Keep
enum class WorkoutType(val displayName: String, val description: String) {
    EASY_RUN("Easy Run", "Comfortable pace, conversational effort"),
    TEMPO_RUN("Tempo Run", "Comfortably hard pace, sustained effort"),
    INTERVAL_RUN("Interval Run", "High intensity with recovery periods"),
    LONG_RUN("Long Run", "Extended distance at easy pace"),
    FARTLEK("Fartlek", "Speed play with varied pace"),
    RECOVERY_RUN("Recovery Run", "Very easy pace for active recovery"),
    RACE_PACE("Race Pace", "Target race pace training"),
    HILL_RUN("Hill Run", "Uphill focused training"),
    TRACK_WORKOUT("Track Workout", "Structured track-based intervals"),
    CUSTOM("Custom", "User-defined workout parameters")
}