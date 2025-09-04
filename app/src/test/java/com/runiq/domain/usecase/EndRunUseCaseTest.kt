package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.repository.RunRepository
import com.runiq.testing.base.BaseUnitTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for EndRunUseCase
 */
class EndRunUseCaseTest : BaseUnitTest() {

    private lateinit var useCase: EndRunUseCase
    private lateinit var runRepository: RunRepository

    private val testSessionId = "session-123"

    @Before
    fun setup() {
        runRepository = mockk()
        useCase = EndRunUseCase(runRepository)
    }

    @Test
    fun `invoke ends run successfully and returns statistics`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            endTime = null
        )
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

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Success(activeSession)
        coEvery { 
            runRepository.endRun(testSessionId, 5000f, 1800000L, 6.0f, 350, 6500)
        } returns Result.Success(Unit)
        coEvery { runRepository.calculateRunStatistics(testSessionId) } returns Result.Success(expectedStats)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedStats, result.data)

        coVerifySequence {
            runRepository.getRunById(testSessionId)
            runRepository.endRun(testSessionId, 5000f, 1800000L, 6.0f, 350, 6500)
            runRepository.calculateRunStatistics(testSessionId)
        }
    }

    @Test
    fun `invoke fails when run session not found`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Success(null)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
        assertEquals("Run session not found", result.exception.message)

        coVerify(exactly = 1) { runRepository.getRunById(testSessionId) }
        coVerify(exactly = 0) { runRepository.endRun(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke fails when run is already ended`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )
        val endedSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            endTime = System.currentTimeMillis()
        )

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Success(endedSession)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalStateException)
        assertEquals("Run session is already ended", result.exception.message)

        coVerify(exactly = 1) { runRepository.getRunById(testSessionId) }
        coVerify(exactly = 0) { runRepository.endRun(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke fails when getRunById returns error`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )
        val repositoryError = Exception("Repository error")

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Error(repositoryError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        // The error should be transformed through mapData
        assertNotNull(result.exception)

        coVerify(exactly = 1) { runRepository.getRunById(testSessionId) }
        coVerify(exactly = 0) { runRepository.endRun(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke fails when endRun returns error`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            endTime = null
        )
        val endRunError = Exception("Failed to end run")

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Success(activeSession)
        coEvery { 
            runRepository.endRun(testSessionId, 5000f, 1800000L, 6.0f, 350, 6500)
        } returns Result.Error(endRunError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        // The error should be transformed through mapData
        assertNotNull(result.exception)

        coVerify(exactly = 1) { runRepository.getRunById(testSessionId) }
        coVerify(exactly = 1) { runRepository.endRun(testSessionId, 5000f, 1800000L, 6.0f, 350, 6500) }
        coVerify(exactly = 0) { runRepository.calculateRunStatistics(any()) }
    }

    @Test
    fun `invoke fails when calculateRunStatistics returns error`() = runTest {
        // Given
        val params = EndRunUseCaseTest.Params(
            sessionId = testSessionId,
            distance = 5000f,
            duration = 1800000L,
            averagePace = 6.0f,
            calories = 350,
            steps = 6500
        )
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = testSessionId,
            endTime = null
        )
        val statsError = Exception("Failed to calculate statistics")

        coEvery { runRepository.getRunById(testSessionId) } returns Result.Success(activeSession)
        coEvery { 
            runRepository.endRun(testSessionId, 5000f, 1800000L, 6.0f, 350, 6500)
        } returns Result.Success(Unit)
        coEvery { runRepository.calculateRunStatistics(testSessionId) } returns Result.Error(statsError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(statsError, result.exception)
    }

    // Create a test-specific Params class since EndRunUseCase.Params might not be accessible in test scope
    private data class Params(
        val sessionId: String,
        val distance: Float,
        val duration: Long,
        val averagePace: Float,
        val calories: Int,
        val steps: Int?
    )

    // Helper method to convert test params to use case params
    private fun Params.toUseCaseParams() = EndRunUseCase.Params(
        sessionId = sessionId,
        distance = distance,
        duration = duration,
        averagePace = averagePace,
        calories = calories,
        steps = steps
    )

    // Update the invoke calls to use the conversion
    private suspend fun useCase(params: Params): Result<RunStatistics> {
        return useCase.invoke(params.toUseCaseParams())
    }
}