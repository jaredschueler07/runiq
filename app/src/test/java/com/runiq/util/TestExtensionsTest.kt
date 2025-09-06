package com.runiq.util

import androidx.lifecycle.MutableLiveData
import com.runiq.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeoutException

/**
 * Unit tests for test extension functions.
 * Ensures our testing utilities work correctly.
 */
@ExperimentalCoroutinesApi
class TestExtensionsTest : BaseUnitTest() {
    
    @Test
    fun `getOrAwaitValue should return LiveData value`() {
        // Given
        val liveData = MutableLiveData<String>()
        
        // When
        liveData.value = "test_value"
        val result = liveData.getOrAwaitValue()
        
        // Then
        assertEquals("Should return the set value", "test_value", result)
    }
    
    @Test(expected = TimeoutException::class)
    fun `getOrAwaitValue should timeout when value never set`() {
        // Given
        val liveData = MutableLiveData<String>()
        
        // When & Then - Should throw TimeoutException
        liveData.getOrAwaitValue(time = 1, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
    }
    
    @Test
    fun `testSingleEmission should verify single flow value`() = runTest {
        // Given
        val flow = flowOf("test_value")
        
        // When & Then
        flow.testSingleEmission("test_value")
    }
    
    @Test
    fun `testEmissions should verify multiple flow values`() = runTest {
        // Given
        val values = listOf("value1", "value2", "value3")
        val flow = flowOf(*values.toTypedArray())
        
        // When & Then
        flow.testEmissions(values)
    }
    
    @Test
    fun `testError should verify flow error`() = runTest {
        // Given
        val exception = RuntimeException("Test exception")
        val flow = flow<String> { throw exception }
        
        // When & Then
        flow.testError(RuntimeException::class.java)
    }
    
    @Test
    fun `runTestWithDescription should provide context on failure`() {
        // Given
        val description = "Testing failure scenario"
        
        try {
            // When
            runTestWithDescription(description) {
                throw RuntimeException("Original error")
            }
        } catch (e: AssertionError) {
            // Then
            org.junit.Assert.assertTrue("Should contain description in error", e.message?.contains(description) == true)
            org.junit.Assert.assertTrue("Should contain original error", e.message?.contains("Original error") == true)
        }
    }
}