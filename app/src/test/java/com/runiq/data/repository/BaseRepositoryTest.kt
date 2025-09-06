package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.testing.base.BaseUnitTest
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.*

/**
 * Unit tests for BaseRepository error handling and utility methods
 */
class BaseRepositoryTest : BaseUnitTest() {
    
    private lateinit var testRepository: TestRepository
    
    @Before
    fun setup() {
        mockkStatic(Timber::class)
        every { Timber.e(any<Throwable>(), any<String>()) } just runs
        testRepository = TestRepository()
    }
    
    @Test
    fun `safeNetworkCall handles successful operation`() = runTest {
        // When
        val result = testRepository.testSafeNetworkCall { "Success" }
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals("Success", result.data)
    }
    
    @Test
    fun `safeNetworkCall handles IOException`() = runTest {
        // Given
        val exception = IOException("Connection failed")
        
        // When
        val result = testRepository.testSafeNetworkCall { throw exception }
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is NetworkException)
        assertEquals("Network connection failed", result.exception.message)
        assertEquals(exception, result.exception.cause)
    }
    
    @Test
    fun `safeNetworkCall handles SocketTimeoutException`() = runTest {
        // Given
        val exception = SocketTimeoutException("Request timed out")
        
        // When
        val result = testRepository.testSafeNetworkCall { throw exception }
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is NetworkException)
        assertEquals("Request timed out", result.exception.message)
        assertEquals(exception, result.exception.cause)
    }
    
    @Test
    fun `safeNetworkCall handles UnknownHostException`() = runTest {
        // Given
        val exception = UnknownHostException("Host not found")
        
        // When
        val result = testRepository.testSafeNetworkCall { throw exception }
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is NetworkException)
        assertEquals("Unable to connect to server", result.exception.message)
        assertEquals(exception, result.exception.cause)
    }
    
    @Test
    fun `safeNetworkCall handles generic Exception`() = runTest {
        // Given
        val exception = RuntimeException("Generic error")
        
        // When
        val result = testRepository.testSafeNetworkCall { throw exception }
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
    }
    
    @Test
    fun `safeDatabaseCall handles successful operation`() = runTest {
        // When
        val result = testRepository.testSafeDatabaseCall { 42 }
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(42, result.data)
    }
    
    @Test
    fun `safeDatabaseCall handles database Exception`() = runTest {
        // Given
        val exception = RuntimeException("Database constraint violation")
        
        // When
        val result = testRepository.testSafeDatabaseCall { throw exception }
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
        assertEquals("Database operation failed", result.exception.message)
        assertEquals(exception, result.exception.cause)
    }
    
    @Test
    fun `asResult transforms successful flow`() = runTest {
        // Given
        val flow = flowOf("Test Value")
        
        // When
        val result = testRepository.testAsResult(flow).first()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals("Test Value", result.data)
    }
    
    @Test
    fun `asResult handles flow exception`() = runTest {
        // Given
        val exception = RuntimeException("Flow error")
        val flow = flowOf("Test").map<String, String> { throw exception }
        
        // When
        val result = testRepository.testAsResult(flow).first()
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
    }
    
    @Test
    fun `mapData transforms successful result`() = runTest {
        // Given
        val successResult = Result.Success(10)
        
        // When
        val result = testRepository.testMapData(successResult) { it * 2 }
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(20, result.data)
    }
    
    @Test
    fun `mapData preserves error result`() = runTest {
        // Given
        val exception = RuntimeException("Test error")
        val errorResult = Result.Error(exception)
        
        // When
        val result = testRepository.testMapData(errorResult) { it * 2 }
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
    }
    
    @Test
    fun `mapData preserves loading result`() = runTest {
        // Given
        val loadingResult = Result.Loading
        
        // When
        val result = testRepository.testMapData(loadingResult) { it * 2 }
        
        // Then
        assertTrue(result is Result.Loading)
    }
    
    @Test
    fun `mapData handles transformation exception`() = runTest {
        // Given
        val successResult = Result.Success(10)
        val transformException = RuntimeException("Transform error")
        
        // When
        val result = testRepository.testMapData(successResult) { 
            throw transformException 
        }
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(transformException, result.exception)
    }
    
    @Test
    fun `combineResults combines two success results`() = runTest {
        // Given
        val result1 = Result.Success("Hello")
        val result2 = Result.Success(" World")
        
        // When
        val combined = testRepository.testCombineResults(result1, result2) { a, b -> a + b }
        
        // Then
        assertTrue(combined is Result.Success)
        assertEquals("Hello World", combined.data)
    }
    
    @Test
    fun `combineResults returns loading when first is loading`() = runTest {
        // Given
        val result1 = Result.Loading
        val result2 = Result.Success("World")
        
        // When
        val combined = testRepository.testCombineResults(result1, result2) { a, b -> a + b }
        
        // Then
        assertTrue(combined is Result.Loading)
    }
    
    @Test
    fun `combineResults returns error when first is error`() = runTest {
        // Given
        val exception = RuntimeException("First error")
        val result1 = Result.Error(exception)
        val result2 = Result.Success("World")
        
        // When
        val combined = testRepository.testCombineResults(result1, result2) { a, b -> a + b }
        
        // Then
        assertTrue(combined is Result.Error)
        assertEquals(exception, combined.exception)
    }
    
    @Test
    fun `combineResults handles combiner exception`() = runTest {
        // Given
        val result1 = Result.Success("Hello")
        val result2 = Result.Success("World")
        val combinerException = RuntimeException("Combiner error")
        
        // When
        val combined = testRepository.testCombineResults(result1, result2) { _, _ -> 
            throw combinerException 
        }
        
        // Then
        assertTrue(combined is Result.Error)
        assertEquals(combinerException, combined.exception)
    }
    
    /**
     * Test implementation of BaseRepository to access protected methods
     */
    private class TestRepository : BaseRepository() {
        
        suspend fun <T> testSafeNetworkCall(apiCall: suspend () -> T): Result<T> {
            return safeNetworkCall(apiCall)
        }
        
        suspend fun <T> testSafeDatabaseCall(databaseCall: suspend () -> T): Result<T> {
            return safeDatabaseCall(databaseCall)
        }
        
        fun <T> testAsResult(flow: kotlinx.coroutines.flow.Flow<T>): kotlinx.coroutines.flow.Flow<Result<T>> {
            return flow.asResult()
        }
        
        suspend fun <T, R> testMapData(
            result: Result<T>,
            transform: suspend (T) -> R
        ): Result<R> {
            return result.mapData(transform)
        }
        
        fun <T1, T2, R> testCombineResults(
            result1: Result<T1>,
            result2: Result<T2>,
            combiner: (T1, T2) -> R
        ): Result<R> {
            return combineResults(result1, result2, combiner)
        }
    }
}