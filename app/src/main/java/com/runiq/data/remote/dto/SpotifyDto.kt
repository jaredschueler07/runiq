package com.runiq.data.remote.dto

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Spotify user profile DTO
 */
@Keep
@JsonClass(generateAdapter = true)
data class SpotifyUserProfile(
    @Json(name = "id")
    val id: String? = null,
    @Json(name = "display_name")
    val displayName: String? = null,
    @Json(name = "email")
    val email: String? = null,
    @Json(name = "country")
    val country: String? = null,
    @Json(name = "product")
    val product: String? = null,
    @Json(name = "images")
    val images: List<Image>? = null,
    @Json(name = "followers")
    val followers: Followers? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Image(
        @Json(name = "url")
        val url: String? = null,
        @Json(name = "height")
        val height: Int? = null,
        @Json(name = "width")
        val width: Int? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Followers(
        @Json(name = "total")
        val total: Int? = null
    )
}

/**
 * Spotify search response DTO
 */
@Keep
@JsonClass(generateAdapter = true)
data class SpotifySearchResponse(
    @Json(name = "tracks")
    val tracks: Tracks? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Tracks(
        @Json(name = "items")
        val items: List<Track>? = null,
        @Json(name = "total")
        val total: Int? = null,
        @Json(name = "limit")
        val limit: Int? = null,
        @Json(name = "offset")
        val offset: Int? = null,
        @Json(name = "next")
        val next: String? = null,
        @Json(name = "previous")
        val previous: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Track(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "artists")
        val artists: List<Artist>? = null,
        @Json(name = "album")
        val album: Album? = null,
        @Json(name = "duration_ms")
        val durationMs: Int? = null,
        @Json(name = "popularity")
        val popularity: Int? = null,
        @Json(name = "explicit")
        val explicit: Boolean? = null,
        @Json(name = "external_urls")
        val externalUrls: ExternalUrls? = null,
        @Json(name = "preview_url")
        val previewUrl: String? = null,
        @Json(name = "uri")
        val uri: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Artist(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "external_urls")
        val externalUrls: ExternalUrls? = null,
        @Json(name = "uri")
        val uri: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Album(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "images")
        val images: List<SpotifyUserProfile.Image>? = null,
        @Json(name = "release_date")
        val releaseDate: String? = null,
        @Json(name = "total_tracks")
        val totalTracks: Int? = null,
        @Json(name = "external_urls")
        val externalUrls: ExternalUrls? = null,
        @Json(name = "uri")
        val uri: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class ExternalUrls(
        @Json(name = "spotify")
        val spotify: String? = null
    )
}

/**
 * Spotify playlist response DTO
 */
@Keep
@JsonClass(generateAdapter = true)
data class SpotifyPlaylistResponse(
    @Json(name = "items")
    val items: List<Playlist>? = null,
    @Json(name = "total")
    val total: Int? = null,
    @Json(name = "limit")
    val limit: Int? = null,
    @Json(name = "offset")
    val offset: Int? = null,
    @Json(name = "next")
    val next: String? = null,
    @Json(name = "previous")
    val previous: String? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Playlist(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "description")
        val description: String? = null,
        @Json(name = "images")
        val images: List<SpotifyUserProfile.Image>? = null,
        @Json(name = "tracks")
        val tracks: PlaylistTracks? = null,
        @Json(name = "external_urls")
        val externalUrls: SpotifySearchResponse.ExternalUrls? = null,
        @Json(name = "uri")
        val uri: String? = null,
        @Json(name = "public")
        val public: Boolean? = null,
        @Json(name = "collaborative")
        val collaborative: Boolean? = null,
        @Json(name = "owner")
        val owner: Owner? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class PlaylistTracks(
        @Json(name = "items")
        val items: List<PlaylistTrackItem>? = null,
        @Json(name = "total")
        val total: Int? = null,
        @Json(name = "limit")
        val limit: Int? = null,
        @Json(name = "offset")
        val offset: Int? = null,
        @Json(name = "next")
        val next: String? = null,
        @Json(name = "previous")
        val previous: String? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class PlaylistTrackItem(
        @Json(name = "track")
        val track: SpotifySearchResponse.Track? = null,
        @Json(name = "added_at")
        val addedAt: String? = null,
        @Json(name = "added_by")
        val addedBy: Owner? = null
    )
    
    @Keep
    @JsonClass(generateAdapter = true)
    data class Owner(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "display_name")
        val displayName: String? = null,
        @Json(name = "external_urls")
        val externalUrls: SpotifySearchResponse.ExternalUrls? = null,
        @Json(name = "uri")
        val uri: String? = null
    )
}

/**
 * Spotify track audio features response DTO
 */
@Keep
@JsonClass(generateAdapter = true)
data class SpotifyTrackFeaturesResponse(
    @Json(name = "audio_features")
    val audioFeatures: List<AudioFeatures>? = null
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class AudioFeatures(
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "tempo")
        val tempo: Float? = null,
        @Json(name = "energy")
        val energy: Float? = null,
        @Json(name = "danceability")
        val danceability: Float? = null,
        @Json(name = "valence")
        val valence: Float? = null,
        @Json(name = "acousticness")
        val acousticness: Float? = null,
        @Json(name = "instrumentalness")
        val instrumentalness: Float? = null,
        @Json(name = "liveness")
        val liveness: Float? = null,
        @Json(name = "speechiness")
        val speechiness: Float? = null,
        @Json(name = "loudness")
        val loudness: Float? = null,
        @Json(name = "key")
        val key: Int? = null,
        @Json(name = "mode")
        val mode: Int? = null,
        @Json(name = "time_signature")
        val timeSignature: Int? = null,
        @Json(name = "duration_ms")
        val durationMs: Int? = null,
        @Json(name = "analysis_url")
        val analysisUrl: String? = null,
        @Json(name = "track_href")
        val trackHref: String? = null,
        @Json(name = "type")
        val type: String? = null,
        @Json(name = "uri")
        val uri: String? = null
    )
}