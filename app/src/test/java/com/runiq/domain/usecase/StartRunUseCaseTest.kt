package com.runiq.domain.usecase

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
}