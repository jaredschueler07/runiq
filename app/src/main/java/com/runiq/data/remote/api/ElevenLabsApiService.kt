package com.runiq.data.remote.api

import com.runiq.data.remote.dto.ElevenLabsRequest
import com.runiq.data.remote.dto.VoiceListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service interface for ElevenLabs Text-to-Speech API
 */
interface ElevenLabsApiService {

    /**
     * Convert text to speech using specified voice
     */
    @POST("v1/text-to-speech/{voice_id}")
    suspend fun textToSpeech(
        @Path("voice_id") voiceId: String,
        @Body request: ElevenLabsRequest,
        @Header("xi-api-key") apiKey: String
    ): Response<ResponseBody>

    /**
     * Get list of available voices
     */
    @GET("v1/voices")
    suspend fun getVoices(
        @Header("xi-api-key") apiKey: String
    ): Response<VoiceListResponse>

    /**
     * Get voice details by ID
     */
    @GET("v1/voices/{voice_id}")
    suspend fun getVoiceById(
        @Path("voice_id") voiceId: String,
        @Header("xi-api-key") apiKey: String
    ): Response<VoiceListResponse.Voice>
}