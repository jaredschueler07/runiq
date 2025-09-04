package com.runiq.testing.base

import androidx.lifecycle.SavedStateHandle
import com.runiq.testing.utils.TestObserver
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

/**
 * Base class for ViewModel tests with common patterns
 */
@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : BaseUnitTest() {
    
    /**
     * Create a mock SavedStateHandle with initial values
     */
    protected fun mockSavedStateHandle(
        values: Map<String, Any?> = emptyMap()
    ): SavedStateHandle {
        val handle = mockk<SavedStateHandle>(relaxed = true)
        values.forEach { (key, value) ->
            every { handle.get<Any?>(key) } returns value
            every { handle[key] = any<Any?>() } answers { }
            every { handle.contains(key) } returns true
        }
        return handle
    }
    
    /**
     * Helper to observe StateFlow values in tests
     */
    protected fun <T> StateFlow<T>.testObserver(): TestObserver<T> {
        val observer = TestObserver<T>()
        runTest {
            collect { observer.onChanged(it) }
        }
        return observer
    }
    
    /**
     * Assert that a StateFlow has a specific value
     */
    protected fun <T> StateFlow<T>.assertValue(expected: T) {
        assertEquals(expected, value)
    }
    
    /**
     * Test loading state pattern
     */
    protected suspend fun testLoadingState(
        action: suspend () -> Unit,
        isLoadingState: () -> Boolean
    ) {
        assert(!isLoadingState()) { "Should not be loading initially" }
        
        action()
        
        // Note: Loading might be too fast to catch in tests
        // Consider adding delay or using TestCoroutineDispatcher
        advanceUntilIdle()
        
        assert(!isLoadingState()) { "Should not be loading after completion" }
    }
    
    /**
     * Test error state handling
     */
    protected suspend fun testErrorState(
        triggerError: suspend () -> Unit,
        getErrorState: () -> Throwable?,
        expectedError: String? = null
    ) {
        assertNull(getErrorState()) { "Should have no error initially" }
        
        triggerError()
        advanceUntilIdle()
        
        val error = getErrorState()
        assertNotNull(error) { "Should have error after trigger" }
        
        if (expectedError != null) {
            assertEquals(expectedError, error.message)
        }
    }
    
    /**
     * Helper to test ViewModel state transitions
     */
    protected suspend fun <T> testStateTransition(
        initialState: T,
        action: suspend () -> Unit,
        expectedState: T,
        getState: () -> T
    ) {
        assertEquals(initialState, getState())
        action()
        advanceUntilIdle()
        assertEquals(expectedState, getState())
    }
    
    /**
     * Helper assertions
     */
    protected fun assertNull(value: Any?, message: () -> String = { "Expected null but was $value" }) {
        if (value != null) throw AssertionError(message())
    }
    
    protected fun assertNotNull(value: Any?, message: () -> String = { "Expected non-null value" }) {
        if (value == null) throw AssertionError(message())
    }
    
    protected fun <T> assertEquals(expected: T, actual: T, message: String = "Values don't match") {
        if (expected != actual) {
            throw AssertionError("$message. Expected: $expected, Actual: $actual")
        }
    }
}