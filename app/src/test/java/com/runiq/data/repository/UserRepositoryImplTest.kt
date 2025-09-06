package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.preferences.*
import com.runiq.testing.base.BaseRepositoryTest
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for UserRepositoryImpl
 */
class UserRepositoryImplTest : BaseRepositoryTest() {
    
    private lateinit var repository: UserRepositoryImpl
    private lateinit var preferencesManager: PreferencesManager
    
    private val testUserId = "test-user-123"
    private val testUserName = "Test User"
    private val testEmail = "test@example.com"
    
    @Before
    fun setup() {
        preferencesManager = mockk()
        repository = UserRepositoryImpl(preferencesManager)
    }
    
    @Test
    fun `observeUserPreferences returns flow from preferences manager`() = runTest {
        // Given
        val preferences = UserPreferences(
            userId = testUserId,
            userName = testUserName,
            userEmail = testEmail
        )
        
        every { preferencesManager.userPreferencesFlow } returns flowOf(preferences)
        
        // When
        val result = repository.observeUserPreferences().first()
        
        // Then
        assertEquals(preferences, result)
        
        verify(exactly = 1) { preferencesManager.userPreferencesFlow }
    }
    
    @Test
    fun `getUserPreferences returns preferences successfully`() = runTest {
        // Given
        val preferences = UserPreferences(
            userId = testUserId,
            userName = testUserName,
            userEmail = testEmail
        )
        
        coEvery { preferencesManager.getUserPreferences() } returns Result.Success(preferences)
        
        // When
        val result = repository.getUserPreferences()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(preferences, result.data)
    }
    
    @Test
    fun `getUserPreferences handles error from preferences manager`() = runTest {
        // Given
        val error = Exception("Preferences error")
        coEvery { preferencesManager.getUserPreferences() } returns Result.Error(error)
        
        // When
        val result = repository.getUserPreferences()
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, result.exception)
    }
    
    @Test
    fun `updateUserProfile updates profile successfully`() = runTest {
        // Given
        val profileImageUrl = "https://example.com/profile.jpg"
        
        coEvery { 
            preferencesManager.updateUserProfile(
                userId = testUserId,
                userName = testUserName,
                userEmail = testEmail,
                profileImageUrl = profileImageUrl
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateUserProfile(
            userId = testUserId,
            userName = testUserName,
            userEmail = testEmail,
            profileImageUrl = profileImageUrl
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateUserProfile(
                userId = testUserId,
                userName = testUserName,
                userEmail = testEmail,
                profileImageUrl = profileImageUrl
            )
        }
    }
    
    @Test
    fun `updateFitnessSettings updates settings successfully`() = runTest {
        // Given
        val weightKg = 70.5f
        val heightCm = 175
        val birthYear = 1990
        val fitnessLevel = FitnessLevel.INTERMEDIATE
        val preferredUnits = Units.METRIC
        
        coEvery { 
            preferencesManager.updateFitnessSettings(
                weightKg = weightKg,
                heightCm = heightCm,
                birthYear = birthYear,
                fitnessLevel = fitnessLevel,
                preferredUnits = preferredUnits
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateFitnessSettings(
            weightKg = weightKg,
            heightCm = heightCm,
            birthYear = birthYear,
            fitnessLevel = fitnessLevel,
            preferredUnits = preferredUnits
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateFitnessSettings(
                weightKg = weightKg,
                heightCm = heightCm,
                birthYear = birthYear,
                fitnessLevel = fitnessLevel,
                preferredUnits = preferredUnits
            )
        }
    }
    
    @Test
    fun `updateCoachingPreferences updates preferences successfully`() = runTest {
        // Given
        val selectedCoachId = "coach-456"
        val coachingFrequency = CoachingFrequency.NORMAL
        val enableVoiceCoaching = true
        val coachingLanguage = "en"
        val voiceVolume = 0.8f
        
        coEvery { 
            preferencesManager.updateCoachingSettings(
                selectedCoachId = selectedCoachId,
                coachingFrequency = coachingFrequency,
                enableVoiceCoaching = enableVoiceCoaching,
                coachingLanguage = coachingLanguage,
                voiceVolume = voiceVolume
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateCoachingPreferences(
            selectedCoachId = selectedCoachId,
            coachingFrequency = coachingFrequency,
            enableVoiceCoaching = enableVoiceCoaching,
            coachingLanguage = coachingLanguage,
            voiceVolume = voiceVolume
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateCoachingSettings(
                selectedCoachId = selectedCoachId,
                coachingFrequency = coachingFrequency,
                enableVoiceCoaching = enableVoiceCoaching,
                coachingLanguage = coachingLanguage,
                voiceVolume = voiceVolume
            )
        }
    }
    
    @Test
    fun `updateMusicPreferences updates music settings successfully`() = runTest {
        // Given
        val enableSpotifyIntegration = true
        val spotifyAccessToken = "access_token_123"
        val spotifyRefreshToken = "refresh_token_456"
        val preferredMusicGenres = setOf("rock", "electronic", "pop")
        val targetBpm = 140
        val enableBpmMatching = true
        val musicVolume = 0.7f
        
        coEvery { 
            preferencesManager.updateSpotifySettings(
                enableSpotifyIntegration = enableSpotifyIntegration,
                spotifyAccessToken = spotifyAccessToken,
                spotifyRefreshToken = spotifyRefreshToken,
                preferredMusicGenres = preferredMusicGenres,
                targetBpm = targetBpm,
                enableBpmMatching = enableBpmMatching,
                musicVolume = musicVolume
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateMusicPreferences(
            enableSpotifyIntegration = enableSpotifyIntegration,
            spotifyAccessToken = spotifyAccessToken,
            spotifyRefreshToken = spotifyRefreshToken,
            preferredMusicGenres = preferredMusicGenres,
            targetBpm = targetBpm,
            enableBpmMatching = enableBpmMatching,
            musicVolume = musicVolume
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateSpotifySettings(
                enableSpotifyIntegration = enableSpotifyIntegration,
                spotifyAccessToken = spotifyAccessToken,
                spotifyRefreshToken = spotifyRefreshToken,
                preferredMusicGenres = preferredMusicGenres,
                targetBpm = targetBpm,
                enableBpmMatching = enableBpmMatching,
                musicVolume = musicVolume
            )
        }
    }
    
    @Test
    fun `updateUserProfile handles null parameters correctly`() = runTest {
        // Given
        coEvery { 
            preferencesManager.updateUserProfile(
                userId = null,
                userName = testUserName,
                userEmail = null,
                profileImageUrl = null
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateUserProfile(
            userId = null,
            userName = testUserName,
            userEmail = null,
            profileImageUrl = null
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateUserProfile(
                userId = null,
                userName = testUserName,
                userEmail = null,
                profileImageUrl = null
            )
        }
    }
    
    @Test
    fun `updateFitnessSettings handles partial updates`() = runTest {
        // Given
        val weightKg = 72.0f
        
        coEvery { 
            preferencesManager.updateFitnessSettings(
                weightKg = weightKg,
                heightCm = null,
                birthYear = null,
                fitnessLevel = null,
                preferredUnits = null
            )
        } returns Result.Success(Unit)
        
        // When
        val result = repository.updateFitnessSettings(
            weightKg = weightKg,
            heightCm = null,
            birthYear = null,
            fitnessLevel = null,
            preferredUnits = null
        )
        
        // Then
        assertTrue(result is Result.Success)
        
        coVerify(exactly = 1) {
            preferencesManager.updateFitnessSettings(
                weightKg = weightKg,
                heightCm = null,
                birthYear = null,
                fitnessLevel = null,
                preferredUnits = null
            )
        }
    }
}