package com.runiq.presentation.screens.run

import app.cash.turbine.test
import com.runiq.base.BaseViewModelTest
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.usecase.StartRunUseCase
import com.runiq.util.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RunViewModel.
 * Tests ViewModel state management and user interactions.
 */
@ExperimentalCoroutinesApi
class RunViewModelTest : BaseViewModelTest() {
    
    private lateinit var viewModel: RunViewModel
    private lateinit var startRunUseCase: StartRunUseCase
    private lateinit var runRepository: RunRepository
    
    @Before
    override fun setUp() {
        super.setUp()
        startRunUseCase = mockk(relaxed = true)
        runRepository = mockk(relaxed = true)
        
        // Default setup - no active run
        every { runRepository.observeActiveRun(any()) } returns flowOf(null)
        
        viewModel = RunViewModel(startRunUseCase, runRepository)
    }
    
    @Test
    fun `initial state should be correct`() = runViewModelTest {
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse("Should not be loading initially", state.isLoading)
            assertFalse("Should not have active run initially", state.isRunActive)
            assertNull("Should not have error initially", state.error)
        }
        
        viewModel.activeSession.test {
            val session = awaitItem()
            assertNull("Should not have active session initially", session)
        }
    }
    
    @Test
    fun `startRun should update state to loading then success`() = runViewModelTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        val expectedSession = TestDataFactory.createRunSession(
            userId = userId,
            workoutType = workoutType,
            coachId = coachId
        )
        
        coEvery { startRunUseCase(userId, workoutType, coachId) } returns Result.success(expectedSession)
        
        // When
        viewModel.startRun(userId, workoutType, coachId)
        
        // Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)
            assertNull("Should not have error", loadingState.error)
            
            // Success state
            val successState = awaitItem()
            assertFalse("Should not be loading", successState.isLoading)
            assertTrue("Should have active run", successState.isRunActive)
            assertNull("Should not have error", successState.error)
        }
        
        coVerify { startRunUseCase(userId, workoutType, coachId) }
    }
    
    @Test
    fun `startRun should update state to loading then error on failure`() = runViewModelTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        val exception = RuntimeException("Failed to start run")
        
        coEvery { startRunUseCase(userId, workoutType, coachId) } returns Result.failure(exception)
        
        // When
        viewModel.startRun(userId, workoutType, coachId)
        
        // Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)
            
            // Error state
            val errorState = awaitItem()
            assertFalse("Should not be loading", errorState.isLoading)
            assertFalse("Should not have active run", errorState.isRunActive)
            assertEquals("Should have error message", "Failed to start run", errorState.error)
        }
    }
    
    @Test
    fun `endRun should update state correctly when session exists`() = runViewModelTest {
        // Given
        val activeSession = TestDataFactory.createRunSession(endTime = null)
        val endedSession = activeSession.copy(endTime = System.currentTimeMillis())
        
        // Set up active session first
        every { runRepository.observeActiveRun(any()) } returns flowOf(activeSession)
        coEvery { runRepository.endRun(activeSession.sessionId) } returns Result.success(endedSession)
        
        // Recreate ViewModel with active session
        viewModel = RunViewModel(startRunUseCase, runRepository)
        
        // When
        viewModel.endRun()
        
        // Then
        viewModel.uiState.test {
            // Skip states until we get to the end result
            skipItems(2) // initial + active session update
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)
            
            // Success state
            val successState = awaitItem()
            assertFalse("Should not be loading", successState.isLoading)
            assertFalse("Should not have active run", successState.isRunActive)
            assertNull("Should not have error", successState.error)
        }
        
        coVerify { runRepository.endRun(activeSession.sessionId) }
    }
    
    @Test
    fun `endRun should set error when no active session`() = runViewModelTest {
        // Given - no active session (default setup)
        
        // When
        viewModel.endRun()
        
        // Then
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            val errorState = awaitItem()
            assertFalse("Should not be loading", errorState.isLoading)
            assertEquals("Should have error message", "No active run to end", errorState.error)
        }
        
        // Verify repository.endRun was never called
        coVerify(exactly = 0) { runRepository.endRun(any()) }
    }
    
    @Test
    fun `clearError should remove error from state`() = runViewModelTest {
        // Given - set an error state first
        viewModel.startRun("", WorkoutType.EASY_RUN, "coach") // This will cause validation error
        
        // Wait a bit for the error to be set
        kotlinx.coroutines.delay(100)
        
        // When
        viewModel.clearError()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull("Error should be cleared", state.error)
        }
    }
    
    @Test
    fun `observeActiveRun should update activeSession state`() = runViewModelTest {
        // Given
        val session = TestDataFactory.createRunSession(endTime = null)
        every { runRepository.observeActiveRun(any()) } returns flowOf(session)
        
        // When - create new ViewModel to trigger observation
        viewModel = RunViewModel(startRunUseCase, runRepository)
        
        // Then
        viewModel.activeSession.test {
            skipItems(1) // Skip initial null
            val activeSession = awaitItem()
            assertNotNull("Should have active session", activeSession)
            assertEquals("Should match expected session", session.sessionId, activeSession?.sessionId)
        }
        
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertTrue("Should show active run", state.isRunActive)
        }
    }
}