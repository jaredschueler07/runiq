package com.runiq.data.repository

import com.runiq.core.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Base repository providing common functionality for all repositories.
 * Includes error handling, network operations, and data transformation patterns.
 */
abstract class BaseRepository {

    /**
     * Execute a network call with proper error handling and context switching
     */
    protected suspend fun <T> safeNetworkCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCall()
                Result.Success(result)
            } catch (e: IOException) {
                Timber.e(e, "Network error in ${this::class.simpleName}")
                Result.Error(NetworkException("Network connection failed", e))
            } catch (e: SocketTimeoutException) {
                Timber.e(e, "Timeout error in ${this::class.simpleName}")
                Result.Error(NetworkException("Request timed out", e))
            } catch (e: UnknownHostException) {
                Timber.e(e, "Host error in ${this::class.simpleName}")
                Result.Error(NetworkException("Unable to connect to server", e))
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in ${this::class.simpleName}")
                Result.Error(e)
            }
        }
    }

    /**
     * Execute a database operation with proper error handling
     */
    protected suspend fun <T> safeDatabaseCall(
        databaseCall: suspend () -> T
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val result = databaseCall()
                Result.Success(result)
            } catch (e: Exception) {
                Timber.e(e, "Database error in ${this::class.simpleName}")
                Result.Error(DatabaseException("Database operation failed", e))
            }
        }
    }

    /**
     * Transform a Flow to emit Result wrapper with error handling
     */
    protected fun <T> Flow<T>.asResult(): Flow<Result<T>> {
        return this
            .map<T, Result<T>> { Result.Success(it) }
            .catch { throwable ->
                Timber.e(throwable, "Flow error in ${this@BaseRepository::class.simpleName}")
                emit(Result.Error(throwable))
            }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Transform and map data with error handling
     */
    protected suspend fun <T, R> Result<T>.mapData(
        transform: suspend (T) -> R
    ): Result<R> {
        return when (this) {
            is Result.Success -> {
                try {
                    Result.Success(transform(data))
                } catch (e: Exception) {
                    Timber.e(e, "Data transformation error")
                    Result.Error(e)
                }
            }
            is Result.Error -> Result.Error(exception)
            is Result.Loading -> Result.Loading
        }
    }

    /**
     * Combine multiple results into a single result
     */
    protected fun <T1, T2, R> combineResults(
        result1: Result<T1>,
        result2: Result<T2>,
        combiner: (T1, T2) -> R
    ): Result<R> {
        return when {
            result1 is Result.Loading || result2 is Result.Loading -> Result.Loading
            result1 is Result.Error -> result1
            result2 is Result.Error -> result2
            result1 is Result.Success && result2 is Result.Success -> {
                try {
                    Result.Success(combiner(result1.data, result2.data))
                } catch (e: Exception) {
                    Timber.e(e, "Error combining results")
                    Result.Error(e)
                }
            }
            else -> Result.Error(IllegalStateException("Invalid result combination"))
        }
    }
}

/**
 * Custom exception for network-related errors
 */
class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Custom exception for database-related errors
 */
class DatabaseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)