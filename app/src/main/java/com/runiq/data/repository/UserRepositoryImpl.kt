package com.runiq.data.repository

import com.runiq.core.util.Result
import com.runiq.data.local.preferences.PreferencesManager
import com.runiq.data.local.preferences.UserPreferences
import com.runiq.data.local.preferences.FitnessLevel
import com.runiq.data.local.preferences.Units
import com.runiq.data.local.preferences.CoachingFrequency
import com.runiq.data.local.preferences.ThemeMode
import com.runiq.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository
 * Manages user preferences and profile data
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseRepository(), UserRepository {

    override fun observeUserPreferences(): Flow<UserPreferences> {
        return preferencesManager.userPreferencesFlow
    }

    override suspend fun getUserPreferences(): Result<UserPreferences> {
        return preferencesManager.getUserPreferences()
    }

    override suspend fun updateUserProfile(
        userId: String?,
        userName: String?,
        userEmail: String?,
        profileImageUrl: String?
    ): Result<Unit> {
        return preferencesManager.updateUserProfile(
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            profileImageUrl = profileImageUrl
        )
    }

    override suspend fun updateFitnessSettings(
        weightKg: Float?,
        heightCm: Int?,
        birthYear: Int?,
        fitnessLevel: FitnessLevel?,
        preferredUnits: Units?
    ): Result<Unit> {
        return preferencesManager.updateFitnessSettings(
            weightKg = weightKg,
            heightCm = heightCm,
            birthYear = birthYear,
            fitnessLevel = fitnessLevel,
            preferredUnits = preferredUnits
        )
    }

    override suspend fun updateCoachingPreferences(
        selectedCoachId: String?,
        coachingFrequency: CoachingFrequency?,
        enableVoiceCoaching: Boolean?,
        coachingLanguage: String?,
        voiceVolume: Float?
    ): Result<Unit> {
        return preferencesManager.updateCoachingSettings(
            selectedCoachId = selectedCoachId,
            coachingFrequency = coachingFrequency,
            enableVoiceCoaching = enableVoiceCoaching,
            coachingLanguage = coachingLanguage,
            voiceVolume = voiceVolume
        )
    }

    override suspend fun updateMusicPreferences(
        enableSpotifyIntegration: Boolean?,
        spotifyAccessToken: String?,
        spotifyRefreshToken: String?,
        preferredMusicGenres: Set<String>?,
        targetBpm: Int?,
        enableBpmMatching: Boolean?,
        musicVolume: Float?
    ): Result<Unit> {
        return preferencesManager.updateSpotifySettings(
            enableSpotifyIntegration = enableSpotifyIntegration,
            spotifyAccessToken = spotifyAccessToken,
            spotifyRefreshToken = spotifyRefreshToken,
            preferredMusicGenres = preferredMusicGenres,
            targetBpm = targetBpm,
            enableBpmMatching = enableBpmMatching,
            musicVolume = musicVolume
        )
    }

    override suspend fun updatePermissions(
        hasLocationPermission: Boolean?,
        hasHealthConnectPermission: Boolean?,
        hasNotificationPermission: Boolean?
    ): Result<Unit> {
        return preferencesManager.updatePermissions(
            hasLocationPermission = hasLocationPermission,
            hasHealthConnectPermission = hasHealthConnectPermission,
            hasNotificationPermission = hasNotificationPermission
        )
    }

    override suspend fun updateOnboardingProgress(
        isOnboardingCompleted: Boolean?,
        isProfileSetupCompleted: Boolean?,
        isPermissionsSetupCompleted: Boolean?,
        hasSeenFeatureTour: Boolean?
    ): Result<Unit> {
        return preferencesManager.updateOnboardingProgress(
            isOnboardingCompleted = isOnboardingCompleted,
            isProfileSetupCompleted = isProfileSetupCompleted,
            isPermissionsSetupCompleted = isPermissionsSetupCompleted,
            hasSeenFeatureTour = hasSeenFeatureTour
        )
    }

    override suspend fun updateAppSettings(
        themeMode: ThemeMode?,
        enableDynamicColor: Boolean?,
        enableHapticFeedback: Boolean?,
        enableNotifications: Boolean?,
        notificationSound: Boolean?
    ): Result<Unit> {
        return preferencesManager.updateAppSettings(
            themeMode = themeMode,
            enableDynamicColor = enableDynamicColor,
            enableHapticFeedback = enableHapticFeedback,
            enableNotifications = enableNotifications,
            notificationSound = notificationSound
        )
    }

    override suspend fun clearUserData(): Result<Unit> {
        return preferencesManager.clearAllPreferences()
    }

    override suspend fun isUserSetupComplete(): Result<Boolean> {
        return getUserPreferences().map { preferences ->
            preferences.isBasicSetupComplete
        }
    }

    override suspend fun getUserBmi(): Result<Float> {
        return getUserPreferences().map { preferences ->
            preferences.bmi
        }
    }

    override suspend fun getUserAge(): Result<Int> {
        return getUserPreferences().map { preferences ->
            preferences.age
        }
    }
}