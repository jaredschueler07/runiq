package com.runiq.domain.model

import androidx.annotation.Keep

/**
 * Represents a single GPS tracking point during a run
 */
@Keep
data class GpsTrackPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val timestamp: Long,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val distance: Float = 0f // Cumulative distance from start
)