package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.dao.CoachDao
import com.runiq.data.local.dao.CoachTextLineDao
import com.runiq.data.local.entities.Coach
import com.runiq.data.local.entities.CoachTextLine
import com.runiq.data.remote.api.ElevenLabsApiService
import com.runiq.data.remote.api.GeminiApiService
import com.runiq.domain.repository.CoachingContext
import com.runiq.domain.repository.CoachingMessage
import com.runiq.domain.repository.VoiceSettings
import com.runiq.testing.base.BaseRepositoryTest
import com.runiq.testing.utils.TestDataFactory
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for CoachRepositoryImpl
 */
class CoachRepositoryImplTest : BaseRepositoryTest() {
    
    private lateinit var repository: CoachRepositoryImpl
    private lateinit var coachDao: CoachDao
    private lateinit var coachTextLineDao: CoachTextLineDao
    private lateinit var geminiApiService: GeminiApiService
    private lateinit var elevenLabsApiService: ElevenLabsApiService
    
    private val testCoachId = "coach-123"
    private val testCategory = "motivational"
    
    @Before
    fun setup() {
        coachDao = mockk()
        coachTextLineDao = mockk()
        geminiApiService = mockk()
        elevenLabsApiService = mockk()
        repository = CoachRepositoryImpl(
            coachDao, coachTextLineDao, geminiApiService, elevenLabsApiService
        )
    }
    
    @Test
    fun `observeActiveCoaches returns flow from dao`() = runTest {
        // Given
        val coaches = TestDataFactory.createCoachList(count = 3)
        every { coachDao.observeActiveCoaches() } returns flowOf(coaches)
        
        // When
        val result = repository.observeActiveCoaches().first()
        
        // Then
        assertEquals(3, result.size)
        assertEquals(coaches, result)
        
        verify(exactly = 1) { coachDao.observeActiveCoaches() }
    }
    
    @Test
    fun `getAllCoaches returns all coaches successfully`() = runTest {
        // Given
        val coaches = TestDataFactory.createCoachList(count = 5)
        coEvery { coachDao.getAll() } returns coaches
        
        // When
        val result = repository.getAllCoaches()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(5, result.data.size)
        assertEquals(coaches, result.data)
    }
    
    @Test
    fun `getAllCoaches handles database error`() = runTest {
        // Given
        coEvery { coachDao.getAll() } throws Exception("Database error")
        
        // When
        val result = repository.getAllCoaches()
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is DatabaseException)
    }
    
    @Test
    fun `getCoachById returns coach successfully`() = runTest {
        // Given
        val coach = TestDataFactory.createCoachEntity(id = testCoachId)
        coEvery { coachDao.getById(testCoachId) } returns coach
        
        // When
        val result = repository.getCoachById(testCoachId)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(coach, result.data)
    }
    
    @Test
    fun `getCoachById returns null when not found`() = runTest {
        // Given
        coEvery { coachDao.getById(testCoachId) } returns null
        
        // When
        val result = repository.getCoachById(testCoachId)
        
        // Then
        assertTrue(result is Result.Success)
        assertNull(result.data)
    }
    
    @Test
    fun `observeCoachById returns flow from dao`() = runTest {
        // Given
        val coach = TestDataFactory.createCoachEntity(id = testCoachId)
        every { coachDao.observeById(testCoachId) } returns flowOf(coach)
        
        // When
        val result = repository.observeCoachById(testCoachId).first()
        
        // Then
        assertEquals(coach, result)
        
        verify(exactly = 1) { coachDao.observeById(testCoachId) }
    }
    
    // Note: Additional tests would depend on the actual implementation
    // of the remaining methods in CoachRepositoryImpl, which would need
    // to be read to understand the full interface and implementation
}