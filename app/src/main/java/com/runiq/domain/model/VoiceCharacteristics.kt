package com.runiq.domain.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
 * Voice characteristics for AI coaches using Eleven Labs
 */
@Keep
@JsonClass(generateAdapter = true)
data class VoiceCharacteristics(
    val voiceId: String,
    val voiceName: String,
    val stability: Float = 0.75f,
    val similarityBoost: Float = 0.75f,
    val style: Float = 0.0f,
    val useSpeakerBoost: Boolean = true
)