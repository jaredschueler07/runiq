package com.runiq.data.remote.api

import com.runiq.data.remote.dto.GeminiRequest
import com.runiq.data.remote.dto.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Gemini AI API
 */
interface GeminiApiService {

    /**
     * Generate coaching content using Gemini AI
     */
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>

    /**
     * Generate coaching content with context
     */
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateCoachingMessage(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>
}