package com.runiq.core.extensions

import com.runiq.core.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

/**
 * Transform a Flow to emit Result wrapper with proper error handling
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { throwable ->
            Timber.e(throwable, "Flow error")
            emit(Result.Error(throwable))
        }
}

/**
 * Filter out loading and error states, only emit successful data
 */
fun <T> Flow<Result<T>>.onlySuccess(): Flow<T> {
    return this
        .filter { it is Result.Success }
        .map { (it as Result.Success).data }
}

/**
 * Filter out loading and success states, only emit errors
 */
fun <T> Flow<Result<T>>.onlyErrors(): Flow<Throwable> {
    return this
        .filter { it is Result.Error }
        .map { (it as Result.Error).exception }
}

/**
 * Execute an action when the result is successful
 */
fun <T> Flow<Result<T>>.doOnSuccess(action: suspend (T) -> Unit): Flow<Result<T>> {
    return onEach { result ->
        if (result is Result.Success) {
            try {
                action(result.data)
            } catch (e: Exception) {
                Timber.e(e, "Error in doOnSuccess")
            }
        }
    }
}

/**
 * Execute an action when the result is an error
 */
fun <T> Flow<Result<T>>.doOnError(action: suspend (Throwable) -> Unit): Flow<Result<T>> {
    return onEach { result ->
        if (result is Result.Error) {
            try {
                action(result.exception)
            } catch (e: Exception) {
                Timber.e(e, "Error in doOnError")
            }
        }
    }
}

/**
 * Execute an action when the result is loading
 */
fun <T> Flow<Result<T>>.doOnLoading(action: suspend () -> Unit): Flow<Result<T>> {
    return onEach { result ->
        if (result is Result.Loading) {
            try {
                action()
            } catch (e: Exception) {
                Timber.e(e, "Error in doOnLoading")
            }
        }
    }
}

/**
 * Map the data inside a Result without unwrapping
 */
fun <T, R> Flow<Result<T>>.mapSuccess(transform: suspend (T) -> R): Flow<Result<R>> {
    return map { result ->
        when (result) {
            is Result.Success -> {
                try {
                    Result.Success(transform(result.data))
                } catch (e: Exception) {
                    Timber.e(e, "Error in mapSuccess")
                    Result.Error(e)
                }
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Loading -> Result.Loading
        }
    }
}

/**
 * Combine two Result flows
 */
fun <T1, T2, R> combineResults(
    flow1: Flow<Result<T1>>,
    flow2: Flow<Result<T2>>,
    combiner: suspend (T1, T2) -> R
): Flow<Result<R>> {
    return combine(flow1, flow2) { result1, result2 ->
        when {
            result1 is Result.Loading || result2 is Result.Loading -> Result.Loading
            result1 is Result.Error -> result1
            result2 is Result.Error -> result2
            result1 is Result.Success && result2 is Result.Success -> {
                try {
                    Result.Success(combiner(result1.data, result2.data))
                } catch (e: Exception) {
                    Timber.e(e, "Error combining flows")
                    Result.Error(e)
                }
            }
            else -> Result.Error(IllegalStateException("Invalid result combination"))
        }
    }
}

/**
 * Combine three Result flows
 */
fun <T1, T2, T3, R> combineResults(
    flow1: Flow<Result<T1>>,
    flow2: Flow<Result<T2>>,
    flow3: Flow<Result<T3>>,
    combiner: suspend (T1, T2, T3) -> R
): Flow<Result<R>> {
    return combine(flow1, flow2, flow3) { result1, result2, result3 ->
        when {
            result1 is Result.Loading || result2 is Result.Loading || result3 is Result.Loading -> Result.Loading
            result1 is Result.Error -> result1
            result2 is Result.Error -> result2
            result3 is Result.Error -> result3
            result1 is Result.Success && result2 is Result.Success && result3 is Result.Success -> {
                try {
                    Result.Success(combiner(result1.data, result2.data, result3.data))
                } catch (e: Exception) {
                    Timber.e(e, "Error combining three flows")
                    Result.Error(e)
                }
            }
            else -> Result.Error(IllegalStateException("Invalid result combination"))
        }
    }
}

/**
 * Switch to a new flow based on the success data, maintaining Result wrapper
 */
fun <T, R> Flow<Result<T>>.flatMapLatestSuccess(
    transform: suspend (T) -> Flow<Result<R>>
): Flow<Result<R>> {
    return flatMapLatest { result ->
        when (result) {
            is Result.Success -> transform(result.data)
            is Result.Error -> flowOf(Result.Error(result.exception))
            is Result.Loading -> flowOf(Result.Loading)
        }
    }
}

/**
 * Emit only distinct successful values
 */
fun <T> Flow<Result<T>>.distinctUntilChangedSuccess(): Flow<Result<T>> {
    return distinctUntilChanged { old, new ->
        when {
            old is Result.Success && new is Result.Success -> old.data == new.data
            old::class == new::class -> true
            else -> false
        }
    }
}