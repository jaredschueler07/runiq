package com.runiq.testing.base

import com.runiq.core.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Base class for repository tests with common patterns and helpers
 */
@ExperimentalCoroutinesApi
abstract class BaseRepositoryTest : BaseUnitTest() {
    
    /**
     * Helper to test successful repository operations
     */
    protected suspend fun <T> testSuccessCase(
        operation: suspend () -> Result<T>,
        expectedValue: T
    ) {
        val result = operation()
        assertTrue(result is Result.Success)
        assertEquals(expectedValue, (result as Result.Success).data)
    }
    
    /**
     * Helper to test error cases in repository operations
     */
    protected suspend fun <T> testErrorCase(
        operation: suspend () -> Result<T>,
        expectedError: Class<out Throwable>? = null
    ) {
        val result = operation()
        assertTrue(result is Result.Error)
        if (expectedError != null) {
            assertTrue(expectedError.isInstance((result as Result.Error).exception))
        }
    }
    
    /**
     * Helper to test Flow emissions from repository
     */
    protected suspend fun <T> testFlowEmission(
        flow: Flow<T>,
        expectedValues: List<T>
    ) {
        val actualValues = mutableListOf<T>()
        flow.collect { actualValues.add(it) }
        assertEquals(expectedValues, actualValues)
    }
    
    /**
     * Helper to mock DAO responses
     */
    protected inline fun <reified T : Any> mockDao(
        relaxed: Boolean = true
    ): T = mockk(relaxed = relaxed)
    
    /**
     * Helper to mock successful DAO operation
     */
    protected suspend fun <T> mockDaoSuccess(
        mock: Any,
        methodCall: suspend () -> T,
        returnValue: T
    ) {
        coEvery { methodCall() } returns returnValue
    }
    
    /**
     * Helper to mock DAO exception
     */
    protected suspend fun <T> mockDaoError(
        mock: Any,
        methodCall: suspend () -> T,
        exception: Throwable = Exception("Database error")
    ) {
        coEvery { methodCall() } throws exception
    }
    
    /**
     * Helper to mock Flow from DAO
     */
    protected fun <T> mockDaoFlow(
        vararg values: T
    ): Flow<T> = flowOf(*values)
    
    /**
     * Verify that proper error handling is in place
     */
    protected suspend fun verifyErrorHandling(
        operation: suspend () -> Result<*>,
        setupError: suspend () -> Unit
    ) {
        setupError()
        val result = operation()
        assertTrue(result is Result.Error)
    }
    
    /**
     * Test that repository properly handles null responses
     */
    protected suspend fun <T> testNullHandling(
        operation: suspend () -> Result<T?>,
        setupNull: suspend () -> Unit
    ) {
        setupNull()
        val result = operation()
        when (result) {
            is Result.Success -> assertEquals(null, result.data)
            is Result.Error -> {} // Also acceptable for null handling
            else -> throw AssertionError("Unexpected result type: $result")
        }
    }
}