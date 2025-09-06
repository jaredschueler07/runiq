package com.runiq.util

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before

/**
 * Base class that provides MockK setup and common mocking utilities.
 * Extend this class to get automatic MockK initialization and cleanup.
 */
abstract class MockkTestBase {
    
    @Before
    open fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }
    
    @After
    open fun tearDown() {
        clearAllMocks()
    }
}

/**
 * Helper functions for common MockK patterns in the RunIQ app.
 */
object MockkHelpers {
    
    /**
     * Creates a relaxed mock that returns sensible defaults for all methods.
     */
    inline fun <reified T : Any> relaxedMock(): T = mockk(relaxed = true)
    
    /**
     * Creates a mock Flow that emits the given values.
     */
    fun <T> mockFlow(vararg values: T): Flow<T> = flowOf(*values)
    
    /**
     * Sets up a mock to return a successful Result.
     */
    inline fun <reified T : Any> mockSuccessResult(data: T) {
        every { any<Result<T>>().isSuccess } returns true
        every { any<Result<T>>().getOrNull() } returns data
        every { any<Result<T>>().getOrThrow() } returns data
    }
    
    /**
     * Sets up a mock to return a failed Result.
     */
    inline fun <reified T : Any> mockFailureResult(exception: Throwable) {
        every { any<Result<T>>().isSuccess } returns false
        every { any<Result<T>>().isFailure } returns true
        every { any<Result<T>>().getOrNull() } returns null
        every { any<Result<T>>().exceptionOrNull() } returns exception
    }
    
    /**
     * Captures the argument passed to a suspend function.
     */
    inline fun <reified T : Any> captureCoArgument(): T {
        val slot = slot<T>()
        return slot.captured
    }
    
    /**
     * Verifies that a suspend function was called with specific arguments.
     */
    inline fun <reified T : Any> verifySuspendCall(
        mock: T,
        noinline verification: suspend T.() -> Unit
    ) {
        coVerify { mock.verification() }
    }
    
    /**
     * Sets up a suspend function to return a value.
     */
    inline fun <reified T : Any, R> setupSuspendReturn(
        mock: T,
        noinline call: suspend T.() -> R,
        returnValue: R
    ) {
        coEvery { mock.call() } returns returnValue
    }
    
    /**
     * Sets up a suspend function to throw an exception.
     */
    inline fun <reified T : Any, R> setupSuspendThrow(
        mock: T,
        noinline call: suspend T.() -> R,
        exception: Throwable
    ) {
        coEvery { mock.call() } throws exception
    }
}

/**
 * DSL for setting up mock behaviors in a readable way.
 */
class MockSetup<T : Any>(private val mock: T) {
    
    fun whenCalling(setup: T.() -> Unit): MockSetup<T> {
        setup(mock)
        return this
    }
    
    suspend fun whenCallingSuspend(setup: suspend T.() -> Unit): MockSetup<T> {
        setup(mock)
        return this
    }
    
    fun thenReturn(value: Any): MockSetup<T> {
        // This would be used in conjunction with every {} blocks
        return this
    }
}

/**
 * Creates a mock setup DSL for readable test configuration.
 */
fun <T : Any> T.setup(): MockSetup<T> = MockSetup(this)