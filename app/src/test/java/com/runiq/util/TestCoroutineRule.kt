package com.runiq.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Extended test rule that provides a complete coroutine testing environment.
 * Includes test scope, scheduler, and dispatcher management.
 */
@ExperimentalCoroutinesApi
class TestCoroutineRule(
    private val testScheduler: TestCoroutineScheduler = TestCoroutineScheduler()
) : TestWatcher() {
    
    val testDispatcher: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
    val testScope: TestScope = TestScope(testDispatcher)
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
    
    /**
     * Runs a test with the test scope, automatically handling dispatcher setup
     */
    fun runTest(block: suspend TestScope.() -> Unit) = testScope.runTest {
        block()
    }
}