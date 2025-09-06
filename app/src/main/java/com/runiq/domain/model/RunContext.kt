package com.runiq.domain.model

import androidx.annotation.Keep

/**
 * Context information for generating appropriate coaching messages
 */
@Keep
data class RunContext(
    val currentPace: Float, // minutes per km
    val targetPace: Float? = null,
    val currentHeartRate: Int? = null,
    val targetHeartRateZone: HeartRateZone? = null,
    val elapsedTime: Long, // milliseconds
    val distance: Float, // meters
    val workoutType: WorkoutType,
    val weatherCondition: WeatherCondition? = null,
    val isFirstKm: Boolean = false,
    val isLastKm: Boolean = false,
    val isPaceSlowing: Boolean = false,
    val isPaceIncreasing: Boolean = false
) {
    
    /**
     * Convert context to conditions for template matching
     */
    fun toConditions(): List<String> = buildList {
        add("workout_type:${workoutType.name}")
        
        targetPace?.let { target ->
            when {
                currentPace < target * 0.95f -> add("pace:faster_than_target")
                currentPace > target * 1.05f -> add("pace:slower_than_target")
                else -> add("pace:on_target")
            }
        }
        
        targetHeartRateZone?.let { zone ->
            currentHeartRate?.let { hr ->
                when {
                    hr < zone.minBpm -> add("hr:below_zone")
                    hr > zone.maxBpm -> add("hr:above_zone")
                    else -> add("hr:in_zone")
                }
            }
        }
        
        when {
            elapsedTime < 300000 -> add("phase:warmup") // first 5 minutes
            distance > 0 && distance % 1000 < 50 -> add("phase:kilometer_marker")
            isFirstKm -> add("phase:first_km")
            isLastKm -> add("phase:final_km")
            else -> add("phase:main")
        }
        
        if (isPaceSlowing) add("trend:slowing")
        if (isPaceIncreasing) add("trend:speeding_up")
        
        weatherCondition?.let { add("weather:${it.name.lowercase()}") }
    }
}

@Keep
enum class HeartRateZone(val zoneName: String, val minBpm: Int, val maxBpm: Int) {
    RECOVERY("Recovery", 100, 130),
    AEROBIC("Aerobic", 130, 150),
    THRESHOLD("Threshold", 150, 170),
    ANAEROBIC("Anaerobic", 170, 185),
    NEUROMUSCULAR("Neuromuscular", 185, 220)
}

@Keep
enum class WeatherCondition {
    SUNNY, CLOUDY, RAINY, SNOWY, WINDY, HOT, COLD, HUMID
}