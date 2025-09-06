package com.runiq.core.error

/**
 * Base exception class for all RunIQ-specific exceptions
 */
sealed class RunIQException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Network-related exceptions
 */
sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class NoInternetConnection : NetworkException("No internet connection available")
    
    class ServerError(
        val statusCode: Int,
        message: String = "Server error occurred"
    ) : NetworkException("$message (HTTP $statusCode)")
    
    class TimeoutException : NetworkException("Request timed out")
    
    class UnknownHostException : NetworkException("Unable to connect to server")
    
    class ParseException(
        cause: Throwable
    ) : NetworkException("Failed to parse server response", cause)
}

/**
 * Database-related exceptions
 */
sealed class DatabaseException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class InsertException(
        tableName: String,
        cause: Throwable
    ) : DatabaseException("Failed to insert into $tableName", cause)
    
    class UpdateException(
        tableName: String,
        cause: Throwable
    ) : DatabaseException("Failed to update $tableName", cause)
    
    class DeleteException(
        tableName: String,
        cause: Throwable
    ) : DatabaseException("Failed to delete from $tableName", cause)
    
    class QueryException(
        query: String,
        cause: Throwable
    ) : DatabaseException("Failed to execute query: $query", cause)
    
    class MigrationException(
        fromVersion: Int,
        toVersion: Int,
        cause: Throwable
    ) : DatabaseException("Failed to migrate database from $fromVersion to $toVersion", cause)
}

/**
 * Authentication-related exceptions
 */
sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class UserNotAuthenticated : AuthException("User is not authenticated")
    
    class InvalidCredentials : AuthException("Invalid username or password")
    
    class AccountLocked : AuthException("Account has been locked")
    
    class TokenExpired : AuthException("Authentication token has expired")
    
    class PermissionDenied : AuthException("Permission denied for this operation")
}

/**
 * Health Connect related exceptions
 */
sealed class HealthConnectException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class NotAvailable : HealthConnectException("Health Connect is not available on this device")
    
    class PermissionDenied(
        val missingPermissions: List<String>
    ) : HealthConnectException("Missing Health Connect permissions: ${missingPermissions.joinToString()}")
    
    class DataWriteException(
        cause: Throwable
    ) : HealthConnectException("Failed to write data to Health Connect", cause)
    
    class DataReadException(
        cause: Throwable
    ) : HealthConnectException("Failed to read data from Health Connect", cause)
}

/**
 * GPS and location related exceptions
 */
sealed class LocationException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class PermissionDenied : LocationException("Location permission not granted")
    
    class ServiceDisabled : LocationException("Location services are disabled")
    
    class NoLocationProvider : LocationException("No location provider available")
    
    class AccuracyTooLow : LocationException("GPS accuracy is too low for tracking")
    
    class TrackingFailed(
        cause: Throwable
    ) : LocationException("GPS tracking failed", cause)
}

/**
 * AI and coaching related exceptions
 */
sealed class AIException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class ServiceUnavailable : AIException("AI service is currently unavailable")
    
    class QuotaExceeded : AIException("AI service quota exceeded")
    
    class InvalidResponse(
        cause: Throwable
    ) : AIException("Invalid response from AI service", cause)
    
    class VoiceSynthesisFailed(
        cause: Throwable
    ) : AIException("Voice synthesis failed", cause)
}

/**
 * Spotify integration exceptions
 */
sealed class SpotifyException(
    message: String,
    cause: Throwable? = null
) : RunIQException(message, cause) {
    
    class NotAuthenticated : SpotifyException("Spotify authentication required")
    
    class PremiumRequired : SpotifyException("Spotify Premium subscription required")
    
    class PlaybackFailed(
        cause: Throwable
    ) : SpotifyException("Spotify playback failed", cause)
    
    class SearchFailed(
        cause: Throwable
    ) : SpotifyException("Spotify search failed", cause)
}

/**
 * Validation exceptions
 */
sealed class ValidationException(
    message: String,
    val field: String? = null
) : RunIQException(message) {
    
    class InvalidInput(
        field: String,
        value: String
    ) : ValidationException("Invalid value '$value' for field '$field'", field)
    
    class RequiredFieldMissing(
        field: String
    ) : ValidationException("Required field '$field' is missing", field)
    
    class InvalidFormat(
        field: String,
        expectedFormat: String
    ) : ValidationException("Field '$field' must be in format: $expectedFormat", field)
    
    class OutOfRange(
        field: String,
        min: Number,
        max: Number,
        actual: Number
    ) : ValidationException("Field '$field' value $actual is out of range [$min, $max]", field)
}

/**
 * A generic exception for unknown errors that wraps the original cause.
 */
data class UnknownException(
    override val message: String,
    override val cause: Throwable?
) : RunIQException(message, cause)
