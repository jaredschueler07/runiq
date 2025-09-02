package com.runiq.data.remote.dto

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request DTO for ElevenLabs Text-to-Speech API
 */
@Keep
@JsonClass(generateAdapter = true)
data class ElevenLabsRequest(
    @Json(name = "text")
    val text: String,
    @Json(name = "model_id")
    val modelId: String = "eleven_monolingual_v1",
    @Json(name = "voice_settings")
    val voiceSettings: VoiceSettings? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class VoiceSettings(
        @Json(name = "stability")
        val stability: Float = 0.5f,
        @Json(name = "similarity_boost")
        val similarityBoost: Float = 0.75f,
        @Json(name = "style")
        val style: Float = 0.0f,
        @Json(name = "use_speaker_boost")
        val useSpeakerBoost: Boolean = true
    )
}

/**
 * Response DTO for ElevenLabs voice list
 */
@Keep
@JsonClass(generateAdapter = true)
data class VoiceListResponse(
    @Json(name = "voices")
    val voices: List<Voice>? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Voice(
        @Json(name = "voice_id")
        val voiceId: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "samples")
        val samples: List<Sample>? = null,
        @Json(name = "category")
        val category: String? = null,
        @Json(name = "fine_tuning")
        val fineTuning: FineTuning? = null,
        @Json(name = "labels")
        val labels: Map<String, String>? = null,
        @Json(name = "description")
        val description: String? = null,
        @Json(name = "preview_url")
        val previewUrl: String? = null,
        @Json(name = "available_for_tiers")
        val availableForTiers: List<String>? = null,
        @Json(name = "settings")
        val settings: VoiceSettings? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Sample(
        @Json(name = "sample_id")
        val sampleId: String? = null,
        @Json(name = "file_name")
        val fileName: String? = null,
        @Json(name = "mime_type")
        val mimeType: String? = null,
        @Json(name = "size_bytes")
        val sizeBytes: Int? = null,
        @Json(name = "hash")
        val hash: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class FineTuning(
        @Json(name = "model_id")
        val modelId: String? = null,
        @Json(name = "is_allowed_to_fine_tune")
        val isAllowedToFineTune: Boolean? = null,
        @Json(name = "finetuning_state")
        val finetuningState: String? = null,
        @Json(name = "verification_attempts")
        val verificationAttempts: List<VerificationAttempt>? = null,
        @Json(name = "verification_failures")
        val verificationFailures: List<String>? = null,
        @Json(name = "verification_attempts_count")
        val verificationAttemptsCount: Int? = null,
        @Json(name = "slice_ids")
        val sliceIds: List<String>? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class VerificationAttempt(
        @Json(name = "text")
        val text: String? = null,
        @Json(name = "date_unix")
        val dateUnix: Long? = null,
        @Json(name = "accepted")
        val accepted: Boolean? = null,
        @Json(name = "similarity")
        val similarity: Float? = null,
        @Json(name = "levenshtein_distance")
        val levenshteinDistance: Int? = null,
        @Json(name = "recording")
        val recording: Recording? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Recording(
        @Json(name = "recording_id")
        val recordingId: String? = null,
        @Json(name = "mime_type")
        val mimeType: String? = null,
        @Json(name = "size_bytes")
        val sizeBytes: Int? = null,
        @Json(name = "upload_date_unix")
        val uploadDateUnix: Long? = null,
        @Json(name = "transcription")
        val transcription: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class VoiceSettings(
        @Json(name = "stability")
        val stability: Float? = null,
        @Json(name = "similarity_boost")
        val similarityBoost: Float? = null,
        @Json(name = "style")
        val style: Float? = null,
        @Json(name = "use_speaker_boost")
        val useSpeakerBoost: Boolean? = null
    )
}