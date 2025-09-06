package com.runiq.domain.repository

import com.runiq.core.util.Result
import com.runiq.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations
 */
interface UserRepository {

    /**
     * Observe user preferences
     */
    fun observeUserPreferences(): Flow<UserPreferences>

    /**
     * Get current user preferences
     */
    suspend fun getUserPreferences(): Result<UserPreferences>

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(
        userId: String? = null,
        userName: String? = null,
        userEmail: String? = null,
        profileImageUrl: String? = null
    ): Result<Unit>

    /**
     * Update fitness settings
     */
    suspend fun updateFitnessSettings(
        weightKg: Float? = null,
        heightCm: Int? = null,
        birthYear: Int? = null,
        fitnessLevel: com.runiq.data.local.preferences.FitnessLevel? = null,
        preferredUnits: com.runiq.data.local.preferences.Units? = null
    ): Result<Unit>

    /**
     * Update coaching preferences
     */
    suspend fun updateCoachingPreferences(
        selectedCoachId: String? = null,
        coachingFrequency: com.runiq.data.local.preferences.CoachingFrequency? = null,
        enableVoiceCoaching: Boolean? = null,
        coachingLanguage: String? = null,
        voiceVolume: Float? = null
    ): Result<Unit>

    /**
     * Update music preferences
     */
    suspend fun updateMusicPreferences(
        enableSpotifyIntegration: Boolean? = null,
        spotifyAccessToken: String? = null,
        spotifyRefreshToken: String? = null,
        preferredMusicGenres: Set<String>? = null,
        targetBpm: Int? = null,
        enableBpmMatching: Boolean? = null,
        musicVolume: Float? = null
    ): Result<Unit>

    /**
     * Update permission states
     */
    suspend fun updatePermissions(
        hasLocationPermission: Boolean? = null,
        hasHealthConnectPermission: Boolean? = null,
        hasNotificationPermission: Boolean? = null
    ): Result<Unit>

    /**
     * Update onboarding progress
     */
    suspend fun updateOnboardingProgress(
        isOnboardingCompleted: Boolean? = null,
        isProfileSetupCompleted: Boolean? = null,
        isPermissionsSetupCompleted: Boolean? = null,
        hasSeenFeatureTour: Boolean? = null
    ): Result<Unit>

    /**
     * Update app settings
     */
    suspend fun updateAppSettings(
        themeMode: com.runiq.data.local.preferences.ThemeMode? = null,
        enableDynamicColor: Boolean? = null,
        enableHapticFeedback: Boolean? = null,
        enableNotifications: Boolean? = null,
        notificationSound: Boolean? = null
    ): Result<Unit>

    /**
     * Clear all user data (logout)
     */
    suspend fun clearUserData(): Result<Unit>

    /**
     * Check if user setup is complete
     */
    suspend fun isUserSetupComplete(): Result<Boolean>

    /**
     * Get user's BMI
     */
    suspend fun getUserBmi(): Result<Float>

    /**
     * Get user's age
     */
    suspend fun getUserAge(): Result<Int>
}