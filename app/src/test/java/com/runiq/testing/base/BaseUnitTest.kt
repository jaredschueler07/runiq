package com.runiq.testing.base

import com.runiq.testing.utils.TestCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Base class for all unit tests providing common setup and teardown
 */
@ExperimentalCoroutinesApi
abstract class BaseUnitTest {
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    @Before
    open fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        onSetup()
    }
    
    @After
    open fun tearDown() {
        onTearDown()
        clearAllMocks()
        unmockkAll()
    }
    
    /**
     * Override this method to perform additional setup
     */
    protected open fun onSetup() {}
    
    /**
     * Override this method to perform additional teardown
     */
    protected open fun onTearDown() {}
    
    /**
     * Run a coroutine test with proper dispatchers
     */
    protected fun runTest(block: suspend () -> Unit) = testCoroutineRule.runTest {
        block()
    }
    
    /**
     * Advance test time by specified milliseconds
     */
    protected fun advanceTimeBy(delayMillis: Long) {
        testCoroutineRule.advanceTimeBy(delayMillis)
    }
    
    /**
     * Advance until all coroutines are idle
     */
    protected fun advanceUntilIdle() {
        testCoroutineRule.advanceUntilIdle()
    }
}