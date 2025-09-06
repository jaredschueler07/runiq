package com.runiq.base

import com.runiq.util.MockkHelpers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Base class for testing Repository implementations.
 * Provides common mocking patterns for data sources and network calls.
 */
@ExperimentalCoroutinesApi
abstract class BaseRepositoryTest : BaseUnitTest() {
    
    /**
     * Helper function to mock a successful repository operation.
     */
    protected inline fun <reified T : Any> mockRepositorySuccess(
        result: T
    ): Result<T> = Result.success(result)
    
    /**
     * Helper function to mock a failed repository operation.
     */
    protected inline fun <reified T : Any> mockRepositoryFailure(
        exception: Throwable = RuntimeException("Test exception")
    ): Result<T> = Result.failure(exception)
    
    /**
     * Helper function to mock a Flow emission from repository.
     */
    protected fun <T> mockRepositoryFlow(vararg values: T): Flow<T> = flowOf(*values)
    
    /**
     * Helper function to verify repository method was called.
     */
    protected inline fun <reified T : Any> verifyRepositoryCall(
        mock: T,
        noinline verification: suspend T.() -> Unit
    ) {
        coVerify { mock.verification() }
    }
    
    /**
     * Helper function to setup repository mock behavior.
     */
    protected inline fun <reified T : Any, R> setupRepositoryMock(
        mock: T,
        noinline call: suspend T.() -> R,
        result: R
    ) {
        coEvery { mock.call() } returns result
    }
    
    /**
     * Helper function to setup repository mock to throw exception.
     */
    protected inline fun <reified T : Any, R> setupRepositoryException(
        mock: T,
        noinline call: suspend T.() -> R,
        exception: Throwable
    ) {
        coEvery { mock.call() } throws exception
    }
}