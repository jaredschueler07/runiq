package com.runiq.domain.usecase

import com.runiq.core.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Base use case for operations that return a single result
 */
abstract class BaseUseCase<in P, out R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    
    /**
     * Execute the use case with the given parameters
     */
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in ${this::class.simpleName}")
            Result.Error(e)
        }
    }

    /**
     * Abstract method to be implemented by concrete use cases
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): Result<R>
}

/**
 * Base use case for operations that don't require parameters
 */
abstract class BaseUseCaseNoParams<out R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    
    /**
     * Execute the use case without parameters
     */
    suspend operator fun invoke(): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in ${this::class.simpleName}")
            Result.Error(e)
        }
    }

    /**
     * Abstract method to be implemented by concrete use cases
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): Result<R>
}

/**
 * Base use case for operations that return a Flow
 */
abstract class FlowUseCase<in P, out R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    
    /**
     * Execute the use case and return a Flow
     */
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .catch { throwable ->
                Timber.e(throwable, "Error in ${this::class.simpleName}")
                emit(Result.Error(throwable))
            }
            .flowOn(coroutineDispatcher)
    }

    /**
     * Abstract method to be implemented by concrete use cases
     */
    protected abstract fun execute(parameters: P): Flow<Result<R>>
}

/**
 * Base use case for operations that return a Flow without parameters
 */
abstract class FlowUseCaseNoParams<out R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    
    /**
     * Execute the use case and return a Flow
     */
    operator fun invoke(): Flow<Result<R>> {
        return execute()
            .catch { throwable ->
                Timber.e(throwable, "Error in ${this::class.simpleName}")
                emit(Result.Error(throwable))
            }
            .flowOn(coroutineDispatcher)
    }

    /**
     * Abstract method to be implemented by concrete use cases
     */
    protected abstract fun execute(): Flow<Result<R>>
}

/**
 * Transform and map data with error handling
 */
suspend fun <T, R> Result<T>.mapData(
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