package com.runiq.core.util

/**
 * A generic wrapper for handling success and error states throughout the application.
 * Provides consistent error handling and loading states for async operations.
 */
sealed class Result<out T> {
    /**
     * Represents a successful result with data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Represents an error result with exception information
     */
    data class Error(val exception: Throwable) : Result<Nothing>()
    
    /**
     * Represents a loading state
     */
    data object Loading : Result<Nothing>()
}

/**
 * Returns true if this is a Success result
 */
val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Returns true if this is an Error result
 */
val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

/**
 * Returns true if this is a Loading result
 */
val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

/**
 * Returns the data if this is a Success result, null otherwise
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

/**
 * Returns the data if this is a Success result, throws the exception if Error
 */
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw exception
    is Result.Loading -> throw IllegalStateException("Result is still loading")
}

/**
 * Returns the data if this is a Success result, or the default value otherwise
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T = when (this) {
    is Result.Success -> data
    else -> defaultValue
}

/**
 * Maps the data if this is a Success result
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(exception)
    is Result.Loading -> Result.Loading
}

/**
 * Flat maps the data if this is a Success result
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> Result.Error(exception)
    is Result.Loading -> Result.Loading
}

/**
 * Executes the given action if this is a Success result
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Executes the given action if this is an Error result
 */
inline fun <T> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

/**
 * Executes the given action if this is a Loading result
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}