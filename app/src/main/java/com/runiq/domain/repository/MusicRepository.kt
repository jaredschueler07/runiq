package com.runiq.domain.repository

import com.runiq.core.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for music-related operations
 */
interface MusicRepository {

    /**
     * Authenticate with Spotify
     */
    suspend fun authenticateSpotify(): Result<SpotifyAuthResult>

    /**
     * Refresh Spotify access token
     */
    suspend fun refreshSpotifyToken(): Result<String>

    /**
     * Check if Spotify is authenticated
     */
    suspend fun isSpotifyAuthenticated(): Result<Boolean>

    /**
     * Search for tracks by BPM range
     */
    suspend fun searchTracksByBpm(
        minBpm: Int,
        maxBpm: Int,
        genres: List<String> = emptyList(),
        limit: Int = 50
    ): Result<List<Track>>

    /**
     * Get recommendations based on current pace and preferences
     */
    suspend fun getRecommendationsForPace(
        currentPace: Float,
        genres: List<String> = emptyList(),
        energy: Float = 0.7f,
        valence: Float = 0.8f
    ): Result<List<Track>>

    /**
     * Get user's playlists
     */
    suspend fun getUserPlaylists(): Result<List<Playlist>>

    /**
     * Get tracks from a playlist with BPM information
     */
    suspend fun getPlaylistTracks(playlistId: String): Result<List<Track>>

    /**
     * Create a workout playlist
     */
    suspend fun createWorkoutPlaylist(
        name: String,
        description: String,
        tracks: List<String>
    ): Result<Playlist>

    /**
     * Get audio features for tracks (including BPM)
     */
    suspend fun getAudioFeatures(trackIds: List<String>): Result<List<AudioFeatures>>

    /**
     * Observe current playing track
     */
    fun observeCurrentTrack(): Flow<Track?>

    /**
     * Control playback
     */
    suspend fun playTrack(trackUri: String): Result<Unit>
    suspend fun pausePlayback(): Result<Unit>
    suspend fun resumePlayback(): Result<Unit>
    suspend fun skipToNext(): Result<Unit>
    suspend fun skipToPrevious(): Result<Unit>
    suspend fun setVolume(volume: Float): Result<Unit>

    /**
     * Get playback state
     */
    suspend fun getPlaybackState(): Result<PlaybackState>

    /**
     * Observe playback state
     */
    fun observePlaybackState(): Flow<PlaybackState>
}

/**
 * Spotify authentication result
 */
data class SpotifyAuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String
)

/**
 * Track data class
 */
data class Track(
    val id: String,
    val name: String,
    val artists: List<String>,
    val album: String,
    val durationMs: Int,
    val popularity: Int,
    val explicit: Boolean,
    val previewUrl: String?,
    val uri: String,
    val bpm: Float? = null,
    val energy: Float? = null,
    val valence: Float? = null,
    val danceability: Float? = null
)

/**
 * Playlist data class
 */
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val trackCount: Int,
    val uri: String,
    val isPublic: Boolean,
    val isCollaborative: Boolean,
    val owner: String
)

/**
 * Audio features data class
 */
data class AudioFeatures(
    val id: String,
    val tempo: Float,
    val energy: Float,
    val danceability: Float,
    val valence: Float,
    val acousticness: Float,
    val instrumentalness: Float,
    val liveness: Float,
    val speechiness: Float,
    val loudness: Float,
    val key: Int,
    val mode: Int,
    val timeSignature: Int,
    val durationMs: Int
)

/**
 * Playback state data class
 */
data class PlaybackState(
    val isPlaying: Boolean,
    val currentTrack: Track?,
    val positionMs: Long,
    val volume: Float,
    val shuffleState: Boolean,
    val repeatState: RepeatMode
)

/**
 * Repeat mode enumeration
 */
enum class RepeatMode {
    OFF,
    TRACK,
    CONTEXT
}