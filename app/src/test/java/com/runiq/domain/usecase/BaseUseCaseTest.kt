package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.testing.base.BaseUnitTest
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import kotlin.test.*

/**
 * Unit tests for BaseUseCase and its variants
 */
class BaseUseCaseTest : BaseUnitTest() {

    @Before
    fun setup() {
        mockkStatic(Timber::class)
        every { Timber.e(any<Throwable>(), any<String>()) } just runs
    }

    @Test
    fun `BaseUseCase executes successfully`() = runTest {
        // Given
        val useCase = TestSuccessUseCase()
        val params = "test-param"

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)
        assertEquals("SUCCESS: test-param", result.data)
    }

    @Test
    fun `BaseUseCase handles execute method exception`() = runTest {
        // Given
        val useCase = TestExecuteExceptionUseCase()
        val params = "test-param"

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is RuntimeException)
        assertEquals("Execute exception", result.exception.message)
        
        verify { Timber.e(any<Throwable>(), any<String>()) }
    }

    @Test
    fun `BaseUseCase handles result error from execute`() = runTest {
        // Given
        val useCase = TestResultErrorUseCase()
        val params = "test-param"

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalStateException)
        assertEquals("Result error", result.exception.message)
    }

    @Test
    fun `BaseUseCaseNoParams executes successfully`() = runTest {
        // Given
        val useCase = TestSuccessUseCaseNoParams()

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals("SUCCESS", result.data)
    }

    @Test
    fun `BaseUseCaseNoParams handles exception`() = runTest {
        // Given
        val useCase = TestExceptionUseCaseNoParams()

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is RuntimeException)
        assertEquals("No params exception", result.exception.message)
        
        verify { Timber.e(any<Throwable>(), any<String>()) }
    }

    @Test
    fun `FlowUseCase executes successfully`() = runTest {
        // Given
        val useCase = TestSuccessFlowUseCase()
        val params = "flow-param"

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals("FLOW: flow-param", result.data)
    }

    @Test
    fun `FlowUseCase handles exception in flow`() = runTest {
        // Given
        val useCase = TestExceptionFlowUseCase()
        val params = "flow-param"

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is RuntimeException)
        assertEquals("Flow exception", result.exception.message)
        
        verify { Timber.e(any<Throwable>(), any<String>()) }
    }

    @Test
    fun `FlowUseCaseNoParams executes successfully`() = runTest {
        // Given
        val useCase = TestSuccessFlowUseCaseNoParams()

        // When
        val result = useCase().first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals("FLOW_NO_PARAMS", result.data)
    }

    @Test
    fun `FlowUseCaseNoParams handles exception`() = runTest {
        // Given
        val useCase = TestExceptionFlowUseCaseNoParams()

        // When
        val result = useCase().first()

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is RuntimeException)
        assertEquals("Flow no params exception", result.exception.message)
        
        verify { Timber.e(any<Throwable>(), any<String>()) }
    }

    // Test implementations
    private class TestSuccessUseCase : BaseUseCase<String, String>() {
        override suspend fun execute(parameters: String): Result<String> {
            return Result.Success("SUCCESS: $parameters")
        }
    }

    private class TestExecuteExceptionUseCase : BaseUseCase<String, String>() {
        override suspend fun execute(parameters: String): Result<String> {
            throw RuntimeException("Execute exception")
        }
    }

    private class TestResultErrorUseCase : BaseUseCase<String, String>() {
        override suspend fun execute(parameters: String): Result<String> {
            return Result.Error(IllegalStateException("Result error"))
        }
    }

    private class TestSuccessUseCaseNoParams : BaseUseCaseNoParams<String>() {
        override suspend fun execute(): Result<String> {
            return Result.Success("SUCCESS")
        }
    }

    private class TestExceptionUseCaseNoParams : BaseUseCaseNoParams<String>() {
        override suspend fun execute(): Result<String> {
            throw RuntimeException("No params exception")
        }
    }

    private class TestSuccessFlowUseCase : FlowUseCase<String, String>() {
        override fun execute(parameters: String): kotlinx.coroutines.flow.Flow<Result<String>> {
            return flowOf(Result.Success("FLOW: $parameters"))
        }
    }

    private class TestExceptionFlowUseCase : FlowUseCase<String, String>() {
        override fun execute(parameters: String): kotlinx.coroutines.flow.Flow<Result<String>> {
            return flowOf("test").map<String, Result<String>> { 
                throw RuntimeException("Flow exception") 
            }
        }
    }

    private class TestSuccessFlowUseCaseNoParams : FlowUseCaseNoParams<String>() {
        override fun execute(): kotlinx.coroutines.flow.Flow<Result<String>> {
            return flowOf(Result.Success("FLOW_NO_PARAMS"))
        }
    }

    private class TestExceptionFlowUseCaseNoParams : FlowUseCaseNoParams<String>() {
        override fun execute(): kotlinx.coroutines.flow.Flow<Result<String>> {
            return flowOf("test").map<String, Result<String>> { 
                throw RuntimeException("Flow no params exception") 
            }
        }
    }
}