package com.runiq.testing.utils

import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Extension functions and utilities for MockK testing
 */

/**
 * Creates a relaxed mock with automatic stubbing of all methods
 */
inline fun <reified T : Any> relaxedMockk(
    name: String? = null,
    block: T.() -> Unit = {}
): T = mockk(name = name, relaxed = true, block = block)

/**
 * Creates a mock that returns default values for all unstubbed calls
 */
inline fun <reified T : Any> mockkWithDefaults(
    name: String? = null,
    crossinline block: T.() -> Unit = {}
): T = mockk(name = name) {
    block()
    every { hashCode() } returns 0
    every { toString() } returns "Mock<${T::class.simpleName}>"
}

/**
 * Verifies that a suspend function was called with specific parameters
 */
suspend inline fun <reified T> coVerifyOnce(
    noinline block: suspend MockKMatcherScope.() -> T
) = coVerify(exactly = 1) { block() }

/**
 * Verifies that a suspend function was never called
 */
suspend inline fun <reified T> coVerifyNever(
    noinline block: suspend MockKMatcherScope.() -> T
) = coVerify(exactly = 0) { block() }

/**
 * Mocks a Flow to return specific values
 */
inline fun <reified T> mockFlow(vararg values: T): Flow<T> = flowOf(*values)

/**
 * Captures all arguments passed to a mocked function
 */
inline fun <reified T : Any> captureAll(): MutableList<T> {
    val slot = mutableListOf<T>()
    every { capture(slot) } answers { callOriginal() }
    return slot
}

/**
 * Resets all mocks in the given scope
 */
fun resetMocks(vararg mocks: Any) {
    mocks.forEach { clearMocks(it) }
}

/**
 * Verifies the order of multiple function calls
 */
fun verifySequence(block: MockKVerificationScope.() -> Unit) {
    io.mockk.verifySequence { block() }
}

/**
 * Helper for mocking repository responses with Result wrapper
 */
inline fun <reified T> mockSuccess(value: T): com.runiq.core.util.Result<T> =
    com.runiq.core.util.Result.Success(value)

inline fun <reified T> mockError(exception: Throwable = Exception("Test error")): com.runiq.core.util.Result<T> =
    com.runiq.core.util.Result.Error(exception)

inline fun <reified T> mockLoading(): com.runiq.core.util.Result<T> =
    com.runiq.core.util.Result.Loading

/**
 * Helper for setting up common mock behaviors
 */
class MockSetup<T : Any>(private val mock: T) {
    
    fun returnsSuccess(value: Any) {
        every { mock } returns value
    }
    
    fun throwsException(exception: Throwable = Exception("Test exception")) {
        every { mock } throws exception
    }
    
    suspend fun coReturnsSuccess(value: Any) {
        coEvery { mock } returns value
    }
    
    suspend fun coThrowsException(exception: Throwable = Exception("Test exception")) {
        coEvery { mock } throws exception
    }
}

/**
 * Extension to easily setup mock behavior
 */
inline fun <reified T : Any> T.setupMock(block: MockSetup<T>.() -> Unit) {
    MockSetup(this).block()
}

/**
 * Captures arguments with type safety
 */
class ArgumentCaptor<T : Any> {
    private val values = mutableListOf<T>()
    
    fun capture(): T {
        val slot = slot<T>()
        values.add(slot.captured)
        return slot.captured
    }
    
    val captured: T
        get() = values.last()
    
    val allValues: List<T>
        get() = values.toList()
    
    fun clear() = values.clear()
}

/**
 * Creates an argument captor for the specified type
 */
inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor()

/**
 * Helper for verifying interactions with specific argument matchers
 */
fun verifyWithMatchers(block: MockKMatcherScope.() -> Unit) {
    verify { block() }
}

/**
 * Excludes recording of specific mock interactions
 */
fun excludeRecords(vararg mocks: Any, block: () -> Unit) {
    excludeRecords { mocks.forEach { it.equals(any()) } }
    block()
}

/**
 * Helper for testing LiveData or StateFlow observers
 */
class TestObserver<T> {
    private val values = mutableListOf<T>()
    
    fun onChanged(value: T) {
        values.add(value)
    }
    
    fun assertValues(vararg expected: T) {
        assert(values == expected.toList()) {
            "Expected ${expected.toList()} but was $values"
        }
    }
    
    fun assertValueCount(count: Int) {
        assert(values.size == count) {
            "Expected $count values but got ${values.size}"
        }
    }
    
    fun values(): List<T> = values.toList()
    
    fun clear() = values.clear()
}