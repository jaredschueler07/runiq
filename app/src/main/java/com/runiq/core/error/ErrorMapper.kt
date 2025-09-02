package com.runiq.core.error

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps various exceptions to user-friendly error messages and RunIQ-specific exceptions
 */
object ErrorMapper {

    /**
     * Map any throwable to a user-friendly error message
     */
    fun mapToUserMessage(throwable: Throwable): String {
        return when (throwable) {
            // RunIQ specific exceptions
            is NetworkException.NoInternetConnection -> "Please check your internet connection"
            is NetworkException.ServerError -> "Server is temporarily unavailable"
            is NetworkException.TimeoutException -> "Request timed out. Please try again"
            is NetworkException.UnknownHostException -> "Unable to connect to server"
            is NetworkException.ParseException -> "Unable to process server response"
            
            is DatabaseException.InsertException -> "Failed to save data"
            is DatabaseException.UpdateException -> "Failed to update data"
            is DatabaseException.DeleteException -> "Failed to delete data"
            is DatabaseException.QueryException -> "Failed to retrieve data"
            is DatabaseException.MigrationException -> "Database upgrade failed"
            
            is AuthException.UserNotAuthenticated -> "Please sign in to continue"
            is AuthException.InvalidCredentials -> "Invalid username or password"
            is AuthException.AccountLocked -> "Account is temporarily locked"
            is AuthException.TokenExpired -> "Session expired. Please sign in again"
            is AuthException.PermissionDenied -> "You don't have permission for this action"
            
            is HealthConnectException.NotAvailable -> "Health Connect is not available"
            is HealthConnectException.PermissionDenied -> "Health Connect permissions required"
            is HealthConnectException.DataWriteException -> "Failed to save health data"
            is HealthConnectException.DataReadException -> "Failed to read health data"
            
            is LocationException.PermissionDenied -> "Location permission required for GPS tracking"
            is LocationException.ServiceDisabled -> "Please enable location services"
            is LocationException.NoLocationProvider -> "GPS is not available"
            is LocationException.AccuracyTooLow -> "GPS signal is too weak"
            is LocationException.TrackingFailed -> "GPS tracking failed"
            
            is AIException.ServiceUnavailable -> "AI coach is temporarily unavailable"
            is AIException.QuotaExceeded -> "AI service limit reached"
            is AIException.InvalidResponse -> "AI coach response error"
            is AIException.VoiceSynthesisFailed -> "Voice coaching unavailable"
            
            is SpotifyException.NotAuthenticated -> "Please connect your Spotify account"
            is SpotifyException.PremiumRequired -> "Spotify Premium required for this feature"
            is SpotifyException.PlaybackFailed -> "Music playback failed"
            is SpotifyException.SearchFailed -> "Music search failed"
            
            is ValidationException.InvalidInput -> "Invalid input: ${throwable.message}"
            is ValidationException.RequiredFieldMissing -> "Required field missing: ${throwable.field}"
            is ValidationException.InvalidFormat -> throwable.message ?: "Invalid format"
            is ValidationException.OutOfRange -> throwable.message ?: "Value out of range"
            
            // Standard exceptions
            is HttpException -> mapHttpException(throwable)
            is IOException -> "Network connection failed"
            is SocketTimeoutException -> "Request timed out"
            is UnknownHostException -> "Unable to connect to server"
            is SecurityException -> "Security error occurred"
            is IllegalArgumentException -> "Invalid input provided"
            is IllegalStateException -> "Operation not allowed at this time"
            
            // Generic fallback
            else -> throwable.message ?: "An unexpected error occurred"
        }
    }

    /**
     * Map HTTP exceptions to RunIQ exceptions
     */
    fun mapToRunIQException(throwable: Throwable): RunIQException {
        return when (throwable) {
            is HttpException -> when (throwable.code()) {
                401 -> AuthException.UserNotAuthenticated()
                403 -> AuthException.PermissionDenied()
                404 -> NetworkException.ServerError(404, "Resource not found")
                408 -> NetworkException.TimeoutException()
                429 -> AIException.QuotaExceeded()
                in 500..599 -> NetworkException.ServerError(throwable.code(), "Server error")
                else -> NetworkException.ServerError(throwable.code(), throwable.message())
            }
            
            is IOException -> NetworkException.NoInternetConnection()
            is SocketTimeoutException -> NetworkException.TimeoutException()
            is UnknownHostException -> NetworkException.UnknownHostException()
            
            is SecurityException -> AuthException.PermissionDenied()
            is IllegalArgumentException -> ValidationException.InvalidInput("unknown", throwable.message ?: "")
            
            is RunIQException -> throwable
            
            else -> object : RunIQException(throwable.message ?: "Unknown error", throwable) {}
        }
    }

    /**
     * Map HTTP status codes to user messages
     */
    private fun mapHttpException(exception: HttpException): String {
        return when (exception.code()) {
            400 -> "Invalid request. Please check your input"
            401 -> "Authentication required. Please sign in"
            403 -> "You don't have permission for this action"
            404 -> "Requested resource not found"
            408 -> "Request timed out. Please try again"
            429 -> "Too many requests. Please try again later"
            in 500..599 -> "Server is temporarily unavailable"
            else -> "Network error occurred (${exception.code()})"
        }
    }

    /**
     * Check if exception indicates no internet connection
     */
    fun isNetworkException(throwable: Throwable): Boolean {
        return when (throwable) {
            is IOException,
            is SocketTimeoutException,
            is UnknownHostException,
            is NetworkException -> true
            else -> false
        }
    }

    /**
     * Check if exception is retryable
     */
    fun isRetryable(throwable: Throwable): Boolean {
        return when (throwable) {
            is NetworkException.TimeoutException,
            is NetworkException.NoInternetConnection,
            is NetworkException.ServerError -> throwable.statusCode in 500..599
            is IOException,
            is SocketTimeoutException -> true
            is HttpException -> throwable.code() in 500..599 || throwable.code() == 408
            else -> false
        }
    }

    /**
     * Get retry delay in milliseconds based on exception type
     */
    fun getRetryDelay(throwable: Throwable, attempt: Int): Long {
        val baseDelay = when (throwable) {
            is NetworkException.TimeoutException -> 2000L
            is NetworkException.ServerError -> 5000L
            is IOException -> 1000L
            else -> 3000L
        }
        
        // Exponential backoff with jitter
        val exponentialDelay = baseDelay * kotlin.math.pow(2.0, attempt.toDouble()).toLong()
        val jitter = (Math.random() * 1000).toLong()
        
        return (exponentialDelay + jitter).coerceAtMost(30000L) // Max 30 seconds
    }
}