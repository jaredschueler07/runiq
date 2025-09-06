package com.runiq.data.remote.dto

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request DTO for Gemini AI API
 */
@Keep
@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents")
    val contents: List<Content>,
    @Json(name = "generationConfig")
    val generationConfig: GenerationConfig? = null,
    @Json(name = "safetySettings")
    val safetySettings: List<SafetySetting>? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Content(
        @Json(name = "parts")
        val parts: List<Part>
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Part(
        @Json(name = "text")
        val text: String
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class GenerationConfig(
        @Json(name = "temperature")
        val temperature: Float? = null,
        @Json(name = "topK")
        val topK: Int? = null,
        @Json(name = "topP")
        val topP: Float? = null,
        @Json(name = "maxOutputTokens")
        val maxOutputTokens: Int? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class SafetySetting(
        @Json(name = "category")
        val category: String,
        @Json(name = "threshold")
        val threshold: String
    )
}

/**
 * Response DTO for Gemini AI API
 */
@Keep
@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates")
    val candidates: List<Candidate>? = null,
    @Json(name = "promptFeedback")
    val promptFeedback: PromptFeedback? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Candidate(
        @Json(name = "content")
        val content: Content? = null,
        @Json(name = "finishReason")
        val finishReason: String? = null,
        @Json(name = "safetyRatings")
        val safetyRatings: List<SafetyRating>? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Content(
        @Json(name = "parts")
        val parts: List<Part>? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Part(
        @Json(name = "text")
        val text: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class SafetyRating(
        @Json(name = "category")
        val category: String? = null,
        @Json(name = "probability")
        val probability: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class PromptFeedback(
        @Json(name = "safetyRatings")
        val safetyRatings: List<SafetyRating>? = null
    )
}