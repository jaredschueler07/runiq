package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.domain.repository.RunRepository
import com.runiq.testing.base.BaseUnitTest
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for SaveGpsTrackPointUseCase
 */
class SaveGpsTrackPointUseCaseTest : BaseUnitTest() {

    private lateinit var useCase: SaveGpsTrackPointUseCase
    private lateinit var runRepository: RunRepository

    private val testSessionId = "session-123"
    private val testLatitude = 37.7749
    private val testLongitude = -122.4194

    @Before
    fun setup() {
        runRepository = mockk()
        useCase = SaveGpsTrackPointUseCase(runRepository)
    }

    @Test
    fun `invoke saves GPS point successfully with good accuracy`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            altitude = 50.0,
            speed = 2.78f,
            accuracy = 10f,
            timestamp = System.currentTimeMillis()
        )

        coEvery { 
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = 50.0,
                speed = 2.78f,
                accuracy = 10f,
                timestamp = params.timestamp
            )
        } returns Result.Success(Unit)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = 50.0,
                speed = 2.78f,
                accuracy = 10f,
                timestamp = params.timestamp
            )
        }
    }

    @Test
    fun `invoke saves GPS point with null optional parameters`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude
        )

        coEvery { 
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = null,
                speed = null,
                accuracy = null,
                timestamp = params.timestamp
            )
        } returns Result.Success(Unit)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = null,
                speed = null,
                accuracy = null,
                timestamp = params.timestamp
            )
        }
    }

    @Test
    fun `invoke rejects GPS point with poor accuracy`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            accuracy = 100f // Above 50m threshold
        )

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
        assertEquals("GPS accuracy too low: 100.0m", result.exception.message)

        coVerify(exactly = 0) {
            runRepository.saveGpsTrackPoint(any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `invoke allows GPS point at accuracy threshold`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            accuracy = 50f // Exactly at threshold
        )

        coEvery { 
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = null,
                speed = null,
                accuracy = 50f,
                timestamp = params.timestamp
            )
        } returns Result.Success(Unit)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = null,
                speed = null,
                accuracy = 50f,
                timestamp = params.timestamp
            )
        }
    }

    @Test
    fun `invoke allows GPS point with null accuracy`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            accuracy = null
        )

        coEvery { 
            runRepository.saveGpsTrackPoint(
                sessionId = testSessionId,
                latitude = testLatitude,
                longitude = testLongitude,
                altitude = null,
                speed = null,
                accuracy = null,
                timestamp = params.timestamp
            )
        } returns Result.Success(Unit)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            runRepository.saveGpsTrackPoint(any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `invoke propagates repository error`() = runTest {
        // Given
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            accuracy = 5f
        )
        val repositoryError = Exception("Repository error")

        coEvery { 
            runRepository.saveGpsTrackPoint(any(), any(), any(), any(), any(), any(), any())
        } returns Result.Error(repositoryError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(repositoryError, result.exception)
    }

    @Test
    fun `invoke handles exception in accuracy validation`() = runTest {
        // Given - Use a very large accuracy value to test edge cases
        val params = SaveGpsTrackPointUseCase.Params(
            sessionId = testSessionId,
            latitude = testLatitude,
            longitude = testLongitude,
            accuracy = 1000f
        )

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
        assertTrue(result.exception.message!!.contains("GPS accuracy too low"))
    }
}