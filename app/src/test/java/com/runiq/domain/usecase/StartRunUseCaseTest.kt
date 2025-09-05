package com.runiq.domain.usecase

<<<<<<< HEAD
import com.runiq.core.util.Result
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.data.local.preferences.UserPreferences
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.repository.UserRepository
import com.runiq.testing.base.BaseUnitTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for StartRunUseCase
 */
class StartRunUseCaseTest : BaseUnitTest() {

    private lateinit var useCase: StartRunUseCase
    private lateinit var runRepository: RunRepository
    private lateinit var userRepository: UserRepository

    private val testUserId = "test-user-123"
    private val testCoachId = "coach-456"
    private val testSessionId = "session-789"

    @Before
    fun setup() {
        runRepository = mockk()
        userRepository = mockk()
        useCase = StartRunUseCase(runRepository, userRepository)
    }

    @Test
    fun `invoke starts run successfully when no active run exists`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.EASY_RUN,
            targetPace = 6.0f
        )
        val userPreferences = UserPreferences(
            userId = testUserId,
            selectedCoachId = testCoachId
        )

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Success(null)
        coEvery { userRepository.getUserPreferences() } returns Result.Success(userPreferences)
        coEvery { 
            runRepository.startRun(testUserId, WorkoutType.EASY_RUN, testCoachId, 6.0f) 
        } returns Result.Success(testSessionId)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testSessionId, result.data)

        coVerifySequence {
            runRepository.getActiveRun(testUserId)
            userRepository.getUserPreferences()
            runRepository.startRun(testUserId, WorkoutType.EASY_RUN, testCoachId, 6.0f)
        }
    }

    @Test
    fun `invoke uses default coach when user preferences not available`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.TEMPO_RUN
        )

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Success(null)
        coEvery { userRepository.getUserPreferences() } returns Result.Error(Exception("Preferences error"))
        coEvery { 
            runRepository.startRun(testUserId, WorkoutType.TEMPO_RUN, "default-coach", null) 
        } returns Result.Success(testSessionId)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testSessionId, result.data)

        coVerify(exactly = 1) {
            runRepository.startRun(testUserId, WorkoutType.TEMPO_RUN, "default-coach", null)
        }
    }

    @Test
    fun `invoke uses default coach when selectedCoachId is null`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.INTERVAL_TRAINING
        )
        val userPreferences = UserPreferences(
            userId = testUserId,
            selectedCoachId = null
        )

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Success(null)
        coEvery { userRepository.getUserPreferences() } returns Result.Success(userPreferences)
        coEvery { 
            runRepository.startRun(testUserId, WorkoutType.INTERVAL_TRAINING, "default-coach", null) 
        } returns Result.Success(testSessionId)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Success)

        coVerify(exactly = 1) {
            runRepository.startRun(testUserId, WorkoutType.INTERVAL_TRAINING, "default-coach", null)
        }
    }

    @Test
    fun `invoke fails when active run already exists`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.EASY_RUN
        )
        val activeSession = TestDataFactory.createRunSessionEntity(
            sessionId = "active-session",
            userId = testUserId,
            endTime = null
        )

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Success(activeSession)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalStateException)
        assertEquals("There is already an active run session", result.exception.message)

        coVerify(exactly = 1) { runRepository.getActiveRun(testUserId) }
        coVerify(exactly = 0) { userRepository.getUserPreferences() }
        coVerify(exactly = 0) { runRepository.startRun(any(), any(), any(), any()) }
    }

    @Test
    fun `invoke fails when getActiveRun returns error`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.EASY_RUN
        )
        val repositoryError = Exception("Repository error")

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Error(repositoryError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(repositoryError, result.exception)

        coVerify(exactly = 1) { runRepository.getActiveRun(testUserId) }
        coVerify(exactly = 0) { userRepository.getUserPreferences() }
    }

    @Test
    fun `invoke propagates startRun error`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.LONG_RUN
        )
        val userPreferences = UserPreferences(
            userId = testUserId,
            selectedCoachId = testCoachId
        )
        val startRunError = Exception("Failed to start run")

        coEvery { runRepository.getActiveRun(testUserId) } returns Result.Success(null)
        coEvery { userRepository.getUserPreferences() } returns Result.Success(userPreferences)
        coEvery { 
            runRepository.startRun(testUserId, WorkoutType.LONG_RUN, testCoachId, null) 
        } returns Result.Error(startRunError)

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(startRunError, result.exception)
    }

    @Test
    fun `invoke handles exception in execute method`() = runTest {
        // Given
        val params = StartRunUseCase.Params(
            userId = testUserId,
            workoutType = WorkoutType.EASY_RUN
        )

        coEvery { runRepository.getActiveRun(testUserId) } throws RuntimeException("Unexpected error")

        // When
        val result = useCase(params)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is RuntimeException)
        assertEquals("Unexpected error", result.exception.message)
    }
=======
import com.runiq.base.BaseUnitTest
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.util.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StartRunUseCase.
 * Tests business logic for starting run sessions.
 */
@ExperimentalCoroutinesApi
class StartRunUseCaseTest : BaseUnitTest() {
    
    private lateinit var useCase: StartRunUseCase
    private lateinit var runRepository: RunRepository
    
    @Before
    override fun setUp() {
        super.setUp()
        runRepository = mockk(relaxed = true)
        useCase = StartRunUseCase(runRepository)
    }
    
    @Test
    fun `invoke should start run successfully when valid inputs provided`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        val expectedSession = TestDataFactory.createRunSession(
            userId = userId,
            workoutType = workoutType,
            coachId = coachId
        )
        
        io.mockk.every { runRepository.observeActiveRun(userId) } returns flowOf(null)
        coEvery { runRepository.startRun(userId, workoutType, coachId) } returns Result.success(expectedSession)
        
        // When
        val result = useCase(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return success", result.isSuccess)
        assertEquals("Should return expected session", expectedSession, result.getOrNull())
        
        coVerify { runRepository.startRun(userId, workoutType, coachId) }
    }
    
    @Test
    fun `invoke should fail when userId is blank`() = runTest {
        // Given
        val userId = ""
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        
        // When
        val result = useCase(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return failure", result.isFailure)
        assertTrue("Should be IllegalArgumentException", 
            result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Should have correct error message", 
            "User ID cannot be blank", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `invoke should fail when coachId is blank`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "   " // Blank with spaces
        
        // When
        val result = useCase(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return failure", result.isFailure)
        assertTrue("Should be IllegalArgumentException", 
            result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Should have correct error message", 
            "Coach ID cannot be blank", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `invoke should fail when user already has active run`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.EASY_RUN
        val coachId = "coach_sarah"
        val activeSession = TestDataFactory.createRunSession(userId = userId, endTime = null)
        
        io.mockk.every { runRepository.observeActiveRun(userId) } returns flowOf(activeSession)
        
        // When
        val result = useCase(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return failure", result.isFailure)
        assertTrue("Should be IllegalStateException", 
            result.exceptionOrNull() is IllegalStateException)
        assertEquals("Should have correct error message", 
            "User already has an active run session", result.exceptionOrNull()?.message)
        
        // Verify repository.startRun was never called
        coVerify(exactly = 0) { runRepository.startRun(any(), any(), any()) }
    }
    
    @Test
    fun `invoke should propagate repository errors`() = runTest {
        // Given
        val userId = "user123"
        val workoutType = WorkoutType.TEMPO_RUN
        val coachId = "coach_sarah"
        val repositoryException = RuntimeException("Database error")
        
        io.mockk.every { runRepository.observeActiveRun(userId) } returns flowOf(null)
        coEvery { runRepository.startRun(userId, workoutType, coachId) } returns Result.failure(repositoryException)
        
        // When
        val result = useCase(userId, workoutType, coachId)
        
        // Then
        assertTrue("Should return failure", result.isFailure)
        assertEquals("Should propagate repository exception", repositoryException, result.exceptionOrNull())
    }
    
    @Test
    fun `invoke should work with all workout types`() = runTest {
        // Given
        val userId = "user123"
        val coachId = "coach_sarah"
        
        io.mockk.every { runRepository.observeActiveRun(userId) } returns flowOf(null)
        
        // Test each workout type
        WorkoutType.values().forEach { workoutType ->
            val expectedSession = TestDataFactory.createRunSession(workoutType = workoutType)
            coEvery { runRepository.startRun(userId, workoutType, coachId) } returns Result.success(expectedSession)
            
            // When
            val result = useCase(userId, workoutType, coachId)
            
            // Then
            assertTrue("Should succeed for $workoutType", result.isSuccess)
            assertEquals("Should return session with correct workout type", 
                workoutType, result.getOrNull()?.workoutType)
        }
    }
>>>>>>> cursor/RUN-40-setup-ai-agent-testing-foundation-8dda
}