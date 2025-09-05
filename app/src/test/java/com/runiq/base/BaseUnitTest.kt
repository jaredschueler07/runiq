package com.runiq.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.runiq.util.MockkTestBase
import com.runiq.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

/**
 * Base class for all unit tests in the RunIQ app.
 * Provides common setup for coroutines, LiveData, and MockK.
 */
@ExperimentalCoroutinesApi
abstract class BaseUnitTest : MockkTestBase() {
    
    /**
     * Rule that swaps the background executor used by the Architecture Components
     * with a different one which executes each task synchronously.
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    /**
     * Rule that replaces the main dispatcher with a test dispatcher.
     */
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()
}