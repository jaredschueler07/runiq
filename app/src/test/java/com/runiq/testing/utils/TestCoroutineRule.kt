package com.runiq.testing.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule to control coroutine execution in tests.
 * Replaces Dispatchers.Main with TestDispatcher and provides
 * utilities for controlling time in tests.
 */
@ExperimentalCoroutinesApi
class TestCoroutineRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
    
    /**
     * Run a test with the test dispatcher
     */
    fun runTest(block: suspend TestScope.() -> Unit) = kotlinx.coroutines.test.runTest(testDispatcher) {
        block()
    }
    
    /**
     * Advance time by the specified amount
     */
    fun advanceTimeBy(delayTimeMillis: Long) {
        (testDispatcher as? TestCoroutineScheduler)?.advanceTimeBy(delayTimeMillis)
    }
    
    /**
     * Advance until all coroutines are idle
     */
    fun advanceUntilIdle() {
        (testDispatcher as? TestCoroutineScheduler)?.advanceUntilIdle()
    }
    
    /**
     * Run all pending tasks
     */
    fun runCurrent() {
        (testDispatcher as? TestCoroutineScheduler)?.runCurrent()
    }
}