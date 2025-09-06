package com.runiq.presentation.screens.run

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
    }
}