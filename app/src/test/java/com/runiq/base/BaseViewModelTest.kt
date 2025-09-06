package com.runiq.base

import androidx.lifecycle.ViewModel
import com.runiq.util.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

/**
 * Base class for testing ViewModels.
 * Provides coroutine testing setup and common ViewModel testing utilities.
 */
@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : BaseUnitTest() {
    
    /**
     * Extended coroutine rule with test scope for ViewModel testing.
     */
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    /**
     * Helper function to test ViewModel initialization.
     */
    protected fun <T : ViewModel> T.testInitialization(
        verify: T.() -> Unit
    ) {
        // ViewModel is created, now verify initial state
        verify()
    }
    
    /**
     * Helper function to run tests in the test scope.
     */
    protected fun runViewModelTest(
        testBody: suspend () -> Unit
    ) = testCoroutineRule.runTest {
        testBody()
    }
}