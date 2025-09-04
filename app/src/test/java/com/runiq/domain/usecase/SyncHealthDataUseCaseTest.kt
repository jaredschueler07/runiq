package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.repository.HealthRepository
import com.runiq.domain.repository.RunRepository
import com.runiq.testing.base.BaseUnitTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for SyncHealthDataUseCase
 */
class SyncHealthDataUseCaseTest : BaseUnitTest() {

    private lateinit var useCase: SyncHealthDataUseCase
    private lateinit var runRepository: RunRepository
    private lateinit var healthRepository: HealthRepository

    private val testUserId = "test-user-123"
    private val testSessionId = "session-456"

    @Before
    fun setup() {
        runRepository = mockk()
        healthRepository = mockk()
        useCase = SyncHealthDataUseCase(runRepository, healthRepository)
    }

    @Test
    fun `invoke fails when Health Connect is not available`() = runTest {
        // Given
        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(false)

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalStateException)
        assertEquals("Health Connect is not available", result.exception.message)

        coVerify(exactly = 1) { healthRepository.isHealthConnectAvailable() }
        coVerify(exactly = 0) { runRepository.getUnsyncedSessions() }
    }

    @Test
    fun `invoke handles Health Connect availability error`() = runTest {
        // Given
        val availabilityError = Exception("Availability check failed")
        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Error(availabilityError)

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        // Error is transformed through mapData
        assertNotNull(result.exception)

        coVerify(exactly = 1) { healthRepository.isHealthConnectAvailable() }
    }

    @Test
    fun `invoke handles permissions check error`() = runTest {
        // Given
        val permissionsError = Exception("Permissions check failed")
        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Error(permissionsError)

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        // Error is transformed through mapData
        assertNotNull(result.exception)

        coVerify(exactly = 1) { healthRepository.checkHealthConnectPermissions() }
    }

    @Test
    fun `invoke handles getUnsyncedSessions error`() = runTest {
        // Given
        val unsyncedError = Exception("Failed to get unsynced sessions")
        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Success(emptyList())
        coEvery { runRepository.getUnsyncedSessions() } returns Result.Error(unsyncedError)

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        // Error is transformed through mapData
        assertNotNull(result.exception)

        coVerify(exactly = 1) { runRepository.getUnsyncedSessions() }
    }

    @Test
    fun `invoke succeeds when no unsynced sessions exist`() = runTest {
        // Given
        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Success(emptyList())
        coEvery { runRepository.getUnsyncedSessions() } returns Result.Success(emptyList())

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Success)

        coVerifySequence {
            healthRepository.isHealthConnectAvailable()
            healthRepository.checkHealthConnectPermissions()
            runRepository.getUnsyncedSessions()
        }
    }

    @Test
    fun `invoke syncs sessions successfully`() = runTest {
        // Given
        val session1 = TestDataFactory.createRunSessionEntity(
            sessionId = "session-1",
            userId = testUserId,
            endTime = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING
        )
        val session2 = TestDataFactory.createRunSessionEntity(
            sessionId = "session-2",
            userId = testUserId,
            endTime = System.currentTimeMillis(),
            syncStatus = SyncStatus.FAILED
        )
        val unsyncedSessions = listOf(session1, session2)
        val gpsTrack1 = TestDataFactory.createGpsTrackList("session-1", 10)
        val gpsTrack2 = TestDataFactory.createGpsTrackList("session-2", 15)

        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Success(emptyList())
        coEvery { runRepository.getUnsyncedSessions() } returns Result.Success(unsyncedSessions)
        coEvery { runRepository.updateSyncStatus("session-1", SyncStatus.PENDING, null) } just runs
        coEvery { runRepository.updateSyncStatus("session-2", SyncStatus.PENDING, null) } just runs
        coEvery { runRepository.getGpsTrack("session-1") } returns Result.Success(gpsTrack1)
        coEvery { runRepository.getGpsTrack("session-2") } returns Result.Success(gpsTrack2)
        
        // Health Connect write will fail (not implemented)
        coEvery { 
            healthRepository.writeRunSessionToHealthConnect(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.Error(NotImplementedError("Health Connect write not yet implemented"))
        
        coEvery { 
            runRepository.updateSyncStatus("session-1", SyncStatus.FAILED, any()) 
        } just runs
        coEvery { 
            runRepository.updateSyncStatus("session-2", SyncStatus.FAILED, any()) 
        } just runs

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception.message!!.contains("2 failures"))

        // Verify sync status was updated to PENDING first, then FAILED
        coVerify(exactly = 1) { runRepository.updateSyncStatus("session-1", SyncStatus.PENDING, null) }
        coVerify(exactly = 1) { runRepository.updateSyncStatus("session-2", SyncStatus.PENDING, null) }
        coVerify(exactly = 1) { runRepository.updateSyncStatus("session-1", SyncStatus.FAILED, any()) }
        coVerify(exactly = 1) { runRepository.updateSyncStatus("session-2", SyncStatus.FAILED, any()) }
    }

    @Test
    fun `invoke skips sessions from other users`() = runTest {
        // Given
        val ownSession = TestDataFactory.createRunSessionEntity(
            sessionId = "own-session",
            userId = testUserId,
            endTime = System.currentTimeMillis()
        )
        val otherSession = TestDataFactory.createRunSessionEntity(
            sessionId = "other-session",
            userId = "other-user",
            endTime = System.currentTimeMillis()
        )
        val unsyncedSessions = listOf(ownSession, otherSession)

        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Success(emptyList())
        coEvery { runRepository.getUnsyncedSessions() } returns Result.Success(unsyncedSessions)
        coEvery { runRepository.updateSyncStatus("own-session", SyncStatus.PENDING, null) } just runs
        coEvery { runRepository.getGpsTrack("own-session") } returns Result.Success(emptyList())
        coEvery { 
            healthRepository.writeRunSessionToHealthConnect(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.Error(NotImplementedError("Health Connect write not yet implemented"))
        coEvery { 
            runRepository.updateSyncStatus("own-session", SyncStatus.FAILED, any()) 
        } just runs

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception.message!!.contains("1 failure"))

        // Verify only own session was processed
        coVerify(exactly = 1) { runRepository.updateSyncStatus("own-session", any(), any()) }
        coVerify(exactly = 0) { runRepository.updateSyncStatus("other-session", any(), any()) }
    }

    @Test
    fun `invoke skips incomplete sessions`() = runTest {
        // Given
        val completeSession = TestDataFactory.createRunSessionEntity(
            sessionId = "complete-session",
            userId = testUserId,
            endTime = System.currentTimeMillis()
        )
        val incompleteSession = TestDataFactory.createRunSessionEntity(
            sessionId = "incomplete-session",
            userId = testUserId,
            endTime = null
        )
        val unsyncedSessions = listOf(completeSession, incompleteSession)

        coEvery { healthRepository.isHealthConnectAvailable() } returns Result.Success(true)
        coEvery { healthRepository.checkHealthConnectPermissions() } returns Result.Success(emptyList())
        coEvery { runRepository.getUnsyncedSessions() } returns Result.Success(unsyncedSessions)
        coEvery { runRepository.updateSyncStatus("complete-session", SyncStatus.PENDING, null) } just runs
        coEvery { runRepository.getGpsTrack("complete-session") } returns Result.Success(emptyList())
        coEvery { 
            healthRepository.writeRunSessionToHealthConnect(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.Error(NotImplementedError("Health Connect write not yet implemented"))
        coEvery { 
            runRepository.updateSyncStatus("complete-session", SyncStatus.FAILED, any()) 
        } just runs

        // When
        val result = useCase(testUserId)

        // Then
        assertTrue(result is Result.Error)

        // Verify only complete session was processed
        coVerify(exactly = 1) { runRepository.updateSyncStatus("complete-session", any(), any()) }
        coVerify(exactly = 0) { runRepository.updateSyncStatus("incomplete-session", any(), any()) }
    }
}