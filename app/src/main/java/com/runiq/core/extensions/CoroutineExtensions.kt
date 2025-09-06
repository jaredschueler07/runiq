package com.runiq.core.extensions

import com.runiq.core.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Execute multiple suspend functions in parallel and collect results
 */
suspend fun <T1, T2> CoroutineScope.parallelExecute(
    block1: suspend () -> T1,
    block2: suspend () -> T2
): Pair<T1, T2> {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    return Pair(deferred1.await(), deferred2.await())
}

/**
 * Execute multiple suspend functions in parallel and collect results
 */
suspend fun <T1, T2, T3> CoroutineScope.parallelExecute(
    block1: suspend () -> T1,
    block2: suspend () -> T2,
    block3: suspend () -> T3
): Triple<T1, T2, T3> {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    return Triple(deferred1.await(), deferred2.await(), deferred3.await())
}

/**
 * Execute a list of suspend functions in parallel
 */
suspend fun <T> CoroutineScope.parallelMap(
    items: List<T>,
    transform: suspend (T) -> Any
): List<Any> {
    return items.map { item ->
        async { transform(item) }
    }.awaitAll()
}

/**
 * Retry a suspend function with exponential backoff
 */
suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelay: Duration = 1000.milliseconds,
    maxDelay: Duration = 10000.milliseconds,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            Timber.w(e, "Retry attempt ${attempt + 1} failed")
            delay(currentDelay)
            currentDelay = (currentDelay * factor).coerceAtMost(maxDelay)
        }
    }
    return block() // Last attempt without catching exception
}

/**
 * Retry a suspend function that returns Result with exponential backoff
 */
suspend fun <T> retryResultWithBackoff(
    times: Int = 3,
    initialDelay: Duration = 1000.milliseconds,
    maxDelay: Duration = 10000.milliseconds,
    factor: Double = 2.0,
    block: suspend () -> Result<T>
): Result<T> {
    var currentDelay = initialDelay
    repeat(times - 1) { attempt ->
        when (val result = block()) {
            is Result.Success -> return result
            is Result.Error -> {
                Timber.w(result.exception, "Retry attempt ${attempt + 1} failed")
                delay(currentDelay)
                currentDelay = (currentDelay * factor).coerceAtMost(maxDelay)
            }
            is Result.Loading -> {
                // Continue to next attempt
                delay(currentDelay)
                currentDelay = (currentDelay * factor).coerceAtMost(maxDelay)
            }
        }
    }
    return block() // Last attempt
}

/**
 * Safe execution with Result wrapper
 */
suspend fun <T> safeCall(
    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T
): Result<T> {
    return try {
        withContext(dispatcher) {
            Result.Success(block())
        }
    } catch (e: Exception) {
        Timber.e(e, "Safe call failed")
        Result.Error(e)
    }
}

/**
 * Execute with timeout and fallback
 */
suspend fun <T> executeWithTimeout(
    timeoutMs: Long,
    fallback: T,
    block: suspend () -> T
): T {
    return try {
        kotlinx.coroutines.withTimeout(timeoutMs) {
            block()
        }
    } catch (e: Exception) {
        Timber.w(e, "Operation timed out, using fallback")
        fallback
    }
}

/**
 * Create a Flow that emits values at regular intervals
 */
fun tickerFlow(
    period: Duration,
    initialDelay: Duration = Duration.ZERO
): Flow<Long> = flow {
    delay(initialDelay)
    var counter = 0L
    while (true) {
        emit(counter++)
        delay(period)
    }
}

/**
 * Debounce emissions and execute action
 */
fun <T> Flow<T>.debounceAndExecute(
    timeoutMs: Long = 300L,
    action: suspend (T) -> Unit
): Flow<T> = flow {
    var lastEmission = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        lastEmission = currentTime
        
        delay(timeoutMs)
        
        if (System.currentTimeMillis() - lastEmission >= timeoutMs) {
            try {
                action(value)
                emit(value)
            } catch (e: Exception) {
                Timber.e(e, "Error in debounced action")
                emit(value)
            }
        }
    }
}