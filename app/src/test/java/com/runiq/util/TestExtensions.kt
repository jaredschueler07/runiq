package com.runiq.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Extension functions to make testing easier and more readable.
 */

/**
 * Observes a LiveData for testing purposes with timeout support.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

/**
 * Extension function to test Flow emissions with Turbine.
 */
suspend fun <T> Flow<T>.testEmissions(
    expectedValues: List<T>
) = test {
    expectedValues.forEach { expected ->
        val actual = awaitItem()
        assertEquals(expected, actual)
    }
    awaitComplete()
}

/**
 * Extension function to test single Flow emission.
 */
suspend fun <T> Flow<T>.testSingleEmission(
    expectedValue: T
) = test {
    val actual = awaitItem()
    assertEquals(expectedValue, actual)
    awaitComplete()
}

/**
 * Extension function to test Flow error.
 */
suspend fun <T> Flow<T>.testError(
    expectedErrorClass: Class<out Throwable>
) = test {
    val error = awaitError()
    assertEquals(expectedErrorClass, error::class.java)
}

/**
 * Runs a test with proper exception handling and descriptive error messages.
 */
fun runTestWithDescription(
    description: String,
    testBody: suspend () -> Unit
) = runTest {
    try {
        testBody()
    } catch (e: Exception) {
        throw AssertionError("Test failed: $description. Error: ${e.message}", e)
    }
}