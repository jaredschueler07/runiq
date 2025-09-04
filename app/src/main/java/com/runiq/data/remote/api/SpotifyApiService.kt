package com.runiq.data.remote.api

import com.runiq.data.remote.dto.SpotifyPlaylistResponse
import com.runiq.data.remote.dto.SpotifySearchResponse
import com.runiq.data.remote.dto.SpotifyTrackFeaturesResponse
import com.runiq.data.remote.dto.SpotifyUserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for Spotify Web API
 */
interface SpotifyApiService {

    /**
     * Get current user's profile
     */
    @GET("v1/me")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String
    ): Response<SpotifyUserProfile>

    /**
     * Search for tracks with BPM filtering
     */
    @GET("v1/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 50,
        @Header("Authorization") authorization: String
    ): Response<SpotifySearchResponse>

    /**
     * Get audio features for tracks (includes BPM)
     */
    @GET("v1/audio-features")
    suspend fun getAudioFeatures(
        @Query("ids") trackIds: String,
        @Header("Authorization") authorization: String
    ): Response<SpotifyTrackFeaturesResponse>

    /**
     * Get user's playlists
     */
    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): Response<SpotifyPlaylistResponse>

    /**
     * Get playlist tracks
     */
    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("playlist_id") playlistId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): Response<SpotifyPlaylistResponse.PlaylistTracks>

    /**
     * Get recommendations based on seed tracks and audio features
     */
    @GET("v1/recommendations")
    suspend fun getRecommendations(
        @Query("seed_tracks") seedTracks: String? = null,
        @Query("seed_genres") seedGenres: String? = null,
        @Query("target_tempo") targetTempo: Int? = null,
        @Query("min_tempo") minTempo: Int? = null,
        @Query("max_tempo") maxTempo: Int? = null,
        @Query("target_energy") targetEnergy: Float? = null,
        @Query("target_valence") targetValence: Float? = null,
        @Query("limit") limit: Int = 20,
        @Header("Authorization") authorization: String
    ): Response<SpotifySearchResponse>
}