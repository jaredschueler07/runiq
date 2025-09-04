package com.runiq.presentation.base

import com.runiq.core.util.Result
import com.runiq.testing.base.BaseViewModelTest as BaseTest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import kotlin.test.*

/**
 * Unit tests for BaseViewModel
 */
class BaseViewModelTest : BaseTest() {

    private lateinit var viewModel: TestBaseViewModel

    @Before
    fun setup() {
        mockkStatic(Timber::class)
        every { Timber.e(any<Throwable>(), any<String>()) } just runs
        viewModel = TestBaseViewModel()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Then
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `executeWithLoading sets and clears loading state`() = runTest {
        // Given
        val initialLoading = viewModel.isLoading.value
        assertFalse(initialLoading)

        // When
        val result = viewModel.testExecuteWithLoading {
            Result.Success("test")
        }

        // Then
        assertTrue(result is Result.Success)
        assertEquals("test", result.data)
        assertFalse(viewModel.isLoading.value) // Should be cleared after execution
    }

    @Test
    fun `executeWithLoading handles successful result`() = runTest {
        // When
        val result = viewModel.testExecuteWithLoading {
            Result.Success(42)
        }

        // Then
        assertTrue(result is Result.Success)
        assertEquals(42, result.data)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `executeWithLoading handles error result`() = runTest {
        // Given
        val exception = Exception("Test error")

        // When
        val result = viewModel.testExecuteWithLoading {
            Result.Error(exception)
        }

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        assertEquals("Test error", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `executeWithLoading handles exception thrown during execution`() = runTest {
        // Given
        val exception = RuntimeException("Execution exception")

        // When
        val result = viewModel.testExecuteWithLoading {
            throw exception
        }

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        assertEquals("Execution exception", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        
        verify { Timber.e(exception, any<String>()) }
    }

    @Test
    fun `executeWithLoading clears previous error`() = runTest {
        // Given - Set an initial error
        viewModel.testShowError("Previous error")
        assertEquals("Previous error", viewModel.error.value)

        // When - Execute successful operation
        val result = viewModel.testExecuteWithLoading {
            Result.Success("success")
        }

        // Then
        assertTrue(result is Result.Success)
        assertNull(viewModel.error.value) // Error should be cleared
    }

    @Test
    fun `clearError clears error state`() = runTest {
        // Given
        viewModel.testShowError("Test error")
        assertEquals("Test error", viewModel.error.value)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }

    @Test
    fun `handleError sets error message from exception`() = runTest {
        // Given
        val exception = Exception("Handle error test")

        // When
        viewModel.testHandleError(exception)

        // Then
        assertEquals("Handle error test", viewModel.error.value)
        verify { Timber.e(exception, any<String>()) }
    }

    @Test
    fun `handleError handles exception with null message`() = runTest {
        // Given
        val exception = Exception(null as String?)

        // When
        viewModel.testHandleError(exception)

        // Then
        assertEquals("An unknown error occurred", viewModel.error.value)
        verify { Timber.e(exception, any<String>()) }
    }

    @Test
    fun `setLoading updates loading state`() = runTest {
        // Given
        assertFalse(viewModel.isLoading.value)

        // When
        viewModel.testSetLoading(true)

        // Then
        assertTrue(viewModel.isLoading.value)

        // When
        viewModel.testSetLoading(false)

        // Then
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `showError sets custom error message`() = runTest {
        // Given
        val customMessage = "Custom error message"

        // When
        viewModel.testShowError(customMessage)

        // Then
        assertEquals(customMessage, viewModel.error.value)
    }

    @Test
    fun `launchWithErrorHandling executes block successfully`() = runTest {
        // Given
        var executed = false

        // When
        viewModel.testLaunchWithErrorHandling {
            executed = true
        }

        // Wait for coroutine to complete
        testScheduler.advanceUntilIdle()

        // Then
        assertTrue(executed)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `launchWithErrorHandling handles exceptions`() = runTest {
        // Given
        val exception = RuntimeException("Launch exception")

        // When
        viewModel.testLaunchWithErrorHandling {
            throw exception
        }

        // Wait for coroutine to complete
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals("Launch exception", viewModel.error.value)
        verify { Timber.e(exception, any<String>()) }
    }

    /**
     * Test implementation of BaseViewModel to access protected methods
     */
    private class TestBaseViewModel : BaseViewModel() {
        
        suspend fun <T> testExecuteWithLoading(action: suspend () -> Result<T>): Result<T> {
            return executeWithLoading(action)
        }
        
        fun testHandleError(throwable: Throwable) {
            handleError(throwable)
        }
        
        fun testSetLoading(loading: Boolean) {
            setLoading(loading)
        }
        
        fun testShowError(message: String) {
            showError(message)
        }
        
        fun testLaunchWithErrorHandling(block: suspend () -> Unit) {
            launchWithErrorHandling(block)
        }
    }
}