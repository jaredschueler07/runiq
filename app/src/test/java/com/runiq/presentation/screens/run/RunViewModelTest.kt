package com.runiq.presentation.screens.run

<<<<<<< HEAD
import com.runiq.core.util.Result
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.usecase.*
import com.runiq.testing.base.BaseViewModelTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for RunViewModel
 */
class RunViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: RunViewModel
    private lateinit var startRunUseCase: StartRunUseCase
    private lateinit var endRunUseCase: EndRunUseCase
    private lateinit var observeActiveRunUseCase: ObserveActiveRunUseCase
    private lateinit var saveGpsTrackPointUseCase: SaveGpsTrackPointUseCase
    private lateinit var getRunHistoryUseCase: GetRunHistoryUseCase

    private val testUserId = "test-user-123"
    private val testSessionId = "session-456"

    @Before
    fun setup() {
        startRunUseCase = mockk()
        endRunUseCase = mockk()
        observeActiveRunUseCase = mockk()
        saveGpsTrackPointUseCase = mockk()
        getRunHistoryUseCase = mockk()

        viewModel = RunViewModel(
            startRunUseCase,
            endRunUseCase,
            observeActiveRunUseCase,
            saveGpsTrackPointUseCase,
            getRunHistoryUseCase
        )
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isRunning)
        assertFalse(uiState.isPaused)
        assertFalse(uiState.isRunCompleted)
        assertNull(uiState.currentSessionId)
        assertNull(uiState.workoutType)
        assertEquals(0f, uiState.totalDistance)
        assertEquals(0L, uiState.elapsedTime)

        assertNull(viewModel.activeRun.value)
        assertTrue(viewModel.gpsPoints.value.isEmpty())
        assertNull(viewModel.runStatistics.value)
    }

    @Test
    fun `setUserId observes active run`() = runTest {
        // Given
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            userId = testUserId,
            endTime = null
        )

        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(activeSession))

        // When
        viewModel.setUserId(testUserId)

        // Wait for flow collection
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(activeSession, viewModel.activeRun.value)
        assertTrue(viewModel.uiState.value.isRunning)

        verify(exactly = 1) { observeActiveRunUseCase(testUserId) }
    }

    @Test
    fun `setUserId handles no active run`() = runTest {
        // Given
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))

        // When
        viewModel.setUserId(testUserId)

        // Wait for flow collection
        testScheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.activeRun.value)
        assertFalse(viewModel.uiState.value.isRunning)
    }

    @Test
    fun `setUserId handles completed run`() = runTest {
        // Given
        val completedSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            userId = testUserId,
            endTime = System.currentTimeMillis()
        )

        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(completedSession))

        // When
        viewModel.setUserId(testUserId)

        // Wait for flow collection
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(completedSession, viewModel.activeRun.value)
        assertFalse(viewModel.uiState.value.isRunning) // Should be false for completed runs
    }

    @Test
    fun `startRun starts new session successfully`() = runTest {
        // Given
        val workoutType = WorkoutType.EASY_RUN
        val targetPace = 6.0f

        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { 
            startRunUseCase(StartRunUseCase.Params(testUserId, workoutType, targetPace))
        } returns Result.Success(testSessionId)

        viewModel.setUserId(testUserId)

        // When
        viewModel.startRun(workoutType, targetPace)

        // Wait for coroutines
        testScheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isRunning)
        assertEquals(testSessionId, uiState.currentSessionId)
        assertEquals(workoutType, uiState.workoutType)
        assertEquals(targetPace, uiState.targetPace)

        coVerify(exactly = 1) {
            startRunUseCase(StartRunUseCase.Params(testUserId, workoutType, targetPace))
        }
    }

    @Test
    fun `startRun handles error from use case`() = runTest {
        // Given
        val workoutType = WorkoutType.TEMPO_RUN
        val error = Exception("Start run failed")

        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { 
            startRunUseCase(any())
        } returns Result.Error(error)

        viewModel.setUserId(testUserId)

        // When
        viewModel.startRun(workoutType)

        // Wait for coroutines
        testScheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isRunning)
        assertEquals("Start run failed", viewModel.error.value)
    }

    @Test
    fun `startRun returns early when no user ID set`() = runTest {
        // Given
        val workoutType = WorkoutType.EASY_RUN

        // When
        viewModel.startRun(workoutType)

        // Wait for coroutines
        testScheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isRunning)
        coVerify(exactly = 0) { startRunUseCase(any()) }
    }

    @Test
    fun `endRun ends session successfully`() = runTest {
        // Given
        val expectedStats = RunStatistics(
            sessionId = testSessionId,
            totalDistance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            maxSpeed = 4.17f,
            averageHeartRate = 145,
            elevationGain = 50f,
            calories = 350,
            steps = 6500
        )

        // Set up initial running state
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)
        coEvery { endRunUseCase(any()) } returns Result.Success(expectedStats)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.endRun()
        testScheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isRunning)
        assertTrue(uiState.isRunCompleted)
        assertEquals(expectedStats, viewModel.runStatistics.value)

        coVerify(exactly = 1) { endRunUseCase(any()) }
    }

    @Test
    fun `endRun returns early when no session ID`() = runTest {
        // When
        viewModel.endRun()

        // Wait for coroutines
        testScheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { endRunUseCase(any()) }
    }

    @Test
    fun `saveGpsPoint saves point successfully`() = runTest {
        // Given
        val latitude = 37.7749
        val longitude = -122.4194
        val speed = 2.78f

        // Set up initial running state
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)
        coEvery { saveGpsTrackPointUseCase(any()) } returns Result.Success(Unit)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.saveGpsPoint(latitude, longitude, speed = speed)
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.gpsPoints.value.size)
        val savedPoint = viewModel.gpsPoints.value.first()
        assertEquals(latitude, savedPoint.latitude)
        assertEquals(longitude, savedPoint.longitude)
        assertEquals(speed, savedPoint.speed)

        // UI state should be updated with metrics
        val uiState = viewModel.uiState.value
        assertEquals(speed, uiState.currentSpeed)

        coVerify(exactly = 1) { saveGpsTrackPointUseCase(any()) }
    }

    @Test
    fun `saveGpsPoint handles error from use case`() = runTest {
        // Given
        val error = Exception("GPS save failed")

        // Set up initial running state
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)
        coEvery { saveGpsTrackPointUseCase(any()) } returns Result.Error(error)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.saveGpsPoint(37.7749, -122.4194)
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals("GPS save failed", viewModel.error.value)
    }

    @Test
    fun `saveGpsPoint returns early when no session ID`() = runTest {
        // When
        viewModel.saveGpsPoint(37.7749, -122.4194)
        testScheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { saveGpsTrackPointUseCase(any()) }
    }

    @Test
    fun `pauseRun updates state`() = runTest {
        // Given
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.pauseRun()

        // Then
        assertTrue(viewModel.uiState.value.isPaused)
        assertTrue(viewModel.uiState.value.isRunning) // Still running, just paused
    }

    @Test
    fun `resumeRun updates state`() = runTest {
        // Given
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        viewModel.pauseRun()
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isPaused)

        // When
        viewModel.resumeRun()

        // Then
        assertFalse(viewModel.uiState.value.isPaused)
    }

    @Test
    fun `resetRunState clears all state`() = runTest {
        // Given - Set up some state
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isRunning)

        // When
        viewModel.resetRunState()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isRunning)
        assertFalse(uiState.isPaused)
        assertFalse(uiState.isRunCompleted)
        assertNull(uiState.currentSessionId)
        assertEquals(0f, uiState.totalDistance)

        assertNull(viewModel.activeRun.value)
        assertTrue(viewModel.gpsPoints.value.isEmpty())
        assertNull(viewModel.runStatistics.value)
    }

    @Test
    fun `calculateTotalDistance computes distance correctly`() = runTest {
        // Given
        val points = listOf(
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                latitude = 37.7749,
                longitude = -122.4194,
                sequenceNumber = 0
            ),
            TestDataFactory.createGpsTrackPointEntity(
                sessionId = testSessionId,
                latitude = 37.7750,
                longitude = -122.4195,
                sequenceNumber = 1
            )
        )

        // Set up running state and save GPS points
        every { observeActiveRunUseCase(testUserId) } returns flowOf(Result.Success(null))
        coEvery { startRunUseCase(any()) } returns Result.Success(testSessionId)
        coEvery { saveGpsTrackPointUseCase(any()) } returns Result.Success(Unit)

        viewModel.setUserId(testUserId)
        viewModel.startRun(WorkoutType.EASY_RUN)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.saveGpsPoint(points[0].latitude, points[0].longitude)
        viewModel.saveGpsPoint(points[1].latitude, points[1].longitude)
        testScheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.totalDistance > 0f)
        assertEquals(2, viewModel.gpsPoints.value.size)
=======
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
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
    }
}