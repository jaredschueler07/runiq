package com.runiq.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.runiq.core.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property to create DataStore instance
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages user preferences using DataStore for type-safe, asynchronous storage
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    // Preference Keys
    private object PreferenceKeys {
        // User Profile
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PROFILE_IMAGE_URL = stringPreferencesKey("profile_image_url")
        
        // Fitness Settings
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val HEIGHT_CM = intPreferencesKey("height_cm")
        val BIRTH_YEAR = intPreferencesKey("birth_year")
        val FITNESS_LEVEL = stringPreferencesKey("fitness_level")
        val PREFERRED_UNITS = stringPreferencesKey("preferred_units")
        
        // Running Preferences
        val DEFAULT_WORKOUT_TYPE = stringPreferencesKey("default_workout_type")
        val TARGET_WEEKLY_DISTANCE = floatPreferencesKey("target_weekly_distance")
        val TARGET_WEEKLY_RUNS = intPreferencesKey("target_weekly_runs")
        val PREFERRED_RUNNING_DAYS = stringSetPreferencesKey("preferred_running_days")
        
        // Coaching Settings
        val SELECTED_COACH_ID = stringPreferencesKey("selected_coach_id")
        val COACHING_FREQUENCY = stringPreferencesKey("coaching_frequency")
        val ENABLE_VOICE_COACHING = booleanPreferencesKey("enable_voice_coaching")
        val COACHING_LANGUAGE = stringPreferencesKey("coaching_language")
        val VOICE_VOLUME = floatPreferencesKey("voice_volume")
        
        // Music Settings
        val ENABLE_SPOTIFY_INTEGRATION = booleanPreferencesKey("enable_spotify_integration")
        val SPOTIFY_ACCESS_TOKEN = stringPreferencesKey("spotify_access_token")
        val SPOTIFY_REFRESH_TOKEN = stringPreferencesKey("spotify_refresh_token")
        val PREFERRED_MUSIC_GENRES = stringSetPreferencesKey("preferred_music_genres")
        val TARGET_BPM = intPreferencesKey("target_bpm")
        val ENABLE_BPM_MATCHING = booleanPreferencesKey("enable_bpm_matching")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        
        // Privacy & Permissions
        val HAS_LOCATION_PERMISSION = booleanPreferencesKey("has_location_permission")
        val HAS_HEALTH_CONNECT_PERMISSION = booleanPreferencesKey("has_health_connect_permission")
        val HAS_NOTIFICATION_PERMISSION = booleanPreferencesKey("has_notification_permission")
        val ENABLE_DATA_SHARING = booleanPreferencesKey("enable_data_sharing")
        val ENABLE_ANALYTICS = booleanPreferencesKey("enable_analytics")
        
        // App Settings
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ENABLE_DYNAMIC_COLOR = booleanPreferencesKey("enable_dynamic_color")
        val ENABLE_HAPTIC_FEEDBACK = booleanPreferencesKey("enable_haptic_feedback")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        val NOTIFICATION_SOUND = booleanPreferencesKey("notification_sound")
        val ENABLE_AUTO_LOCK = booleanPreferencesKey("enable_auto_lock")
        
        // Health Connect Settings
        val HEALTH_CONNECT_SYNC_ENABLED = booleanPreferencesKey("health_connect_sync_enabled")
        val LAST_HEALTH_CONNECT_SYNC = longPreferencesKey("last_health_connect_sync")
        val HEALTH_CONNECT_DATA_SOURCES = stringSetPreferencesKey("health_connect_data_sources")
        
        // Onboarding & Setup
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val IS_PROFILE_SETUP_COMPLETED = booleanPreferencesKey("is_profile_setup_completed")
        val IS_PERMISSIONS_SETUP_COMPLETED = booleanPreferencesKey("is_permissions_setup_completed")
        val HAS_SEEN_FEATURE_TOUR = booleanPreferencesKey("has_seen_feature_tour")
        val APP_VERSION = stringPreferencesKey("app_version")
        val LAST_APP_UPDATE = longPreferencesKey("last_app_update")
        
        // Advanced Settings
        val ENABLE_DEBUG_MODE = booleanPreferencesKey("enable_debug_mode")
        val CRASH_REPORTING_ENABLED = booleanPreferencesKey("crash_reporting_enabled")
        val DATA_RETENTION_DAYS = intPreferencesKey("data_retention_days")
        val AUTO_UPLOAD_RUNS = booleanPreferencesKey("auto_upload_runs")
        val GPS_ACCURACY_THRESHOLD = floatPreferencesKey("gps_accuracy_threshold")
        val AUTO_PAUSE_ENABLED = booleanPreferencesKey("auto_pause_enabled")
        val AUTO_PAUSE_SENSITIVITY = floatPreferencesKey("auto_pause_sensitivity")
    }
    
    /**
     * Get user preferences as a Flow
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            UserPreferences(
                // User Profile
                userId = preferences[PreferenceKeys.USER_ID] ?: "",
                userName = preferences[PreferenceKeys.USER_NAME] ?: "",
                userEmail = preferences[PreferenceKeys.USER_EMAIL] ?: "",
                profileImageUrl = preferences[PreferenceKeys.PROFILE_IMAGE_URL] ?: "",
                
                // Fitness Settings
                weightKg = preferences[PreferenceKeys.WEIGHT_KG] ?: 70f,
                heightCm = preferences[PreferenceKeys.HEIGHT_CM] ?: 170,
                birthYear = preferences[PreferenceKeys.BIRTH_YEAR] ?: 1990,
                fitnessLevel = FitnessLevel.valueOf(
                    preferences[PreferenceKeys.FITNESS_LEVEL] ?: FitnessLevel.INTERMEDIATE.name
                ),
                preferredUnits = Units.valueOf(
                    preferences[PreferenceKeys.PREFERRED_UNITS] ?: Units.METRIC.name
                ),
                
                // Running Preferences
                defaultWorkoutType = WorkoutType.valueOf(
                    preferences[PreferenceKeys.DEFAULT_WORKOUT_TYPE] ?: WorkoutType.EASY_RUN.name
                ),
                targetWeeklyDistance = preferences[PreferenceKeys.TARGET_WEEKLY_DISTANCE] ?: 20f,
                targetWeeklyRuns = preferences[PreferenceKeys.TARGET_WEEKLY_RUNS] ?: 3,
                preferredRunningDays = preferences[PreferenceKeys.PREFERRED_RUNNING_DAYS]
                    ?.mapNotNull { 
                        try { DayOfWeek.valueOf(it) } catch (e: Exception) { null }
                    }?.toSet() ?: setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                
                // Coaching Settings
                selectedCoachId = preferences[PreferenceKeys.SELECTED_COACH_ID] ?: "default",
                coachingFrequency = CoachingFrequency.valueOf(
                    preferences[PreferenceKeys.COACHING_FREQUENCY] ?: CoachingFrequency.MODERATE.name
                ),
                enableVoiceCoaching = preferences[PreferenceKeys.ENABLE_VOICE_COACHING] ?: true,
                coachingLanguage = preferences[PreferenceKeys.COACHING_LANGUAGE] ?: "en",
                voiceVolume = preferences[PreferenceKeys.VOICE_VOLUME] ?: 0.8f,
                
                // Music Settings
                enableSpotifyIntegration = preferences[PreferenceKeys.ENABLE_SPOTIFY_INTEGRATION] ?: false,
                spotifyAccessToken = preferences[PreferenceKeys.SPOTIFY_ACCESS_TOKEN] ?: "",
                spotifyRefreshToken = preferences[PreferenceKeys.SPOTIFY_REFRESH_TOKEN] ?: "",
                preferredMusicGenres = preferences[PreferenceKeys.PREFERRED_MUSIC_GENRES] ?: emptySet(),
                targetBpm = preferences[PreferenceKeys.TARGET_BPM] ?: 120,
                enableBpmMatching = preferences[PreferenceKeys.ENABLE_BPM_MATCHING] ?: true,
                musicVolume = preferences[PreferenceKeys.MUSIC_VOLUME] ?: 0.7f,
                
                // Privacy & Permissions
                hasLocationPermission = preferences[PreferenceKeys.HAS_LOCATION_PERMISSION] ?: false,
                hasHealthConnectPermission = preferences[PreferenceKeys.HAS_HEALTH_CONNECT_PERMISSION] ?: false,
                hasNotificationPermission = preferences[PreferenceKeys.HAS_NOTIFICATION_PERMISSION] ?: false,
                enableDataSharing = preferences[PreferenceKeys.ENABLE_DATA_SHARING] ?: false,
                enableAnalytics = preferences[PreferenceKeys.ENABLE_ANALYTICS] ?: true,
                
                // App Settings
                themeMode = ThemeMode.valueOf(
                    preferences[PreferenceKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                enableDynamicColor = preferences[PreferenceKeys.ENABLE_DYNAMIC_COLOR] ?: true,
                enableHapticFeedback = preferences[PreferenceKeys.ENABLE_HAPTIC_FEEDBACK] ?: true,
                enableNotifications = preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] ?: true,
                notificationSound = preferences[PreferenceKeys.NOTIFICATION_SOUND] ?: true,
                enableAutoLock = preferences[PreferenceKeys.ENABLE_AUTO_LOCK] ?: false,
                
                // Health Connect Settings
                healthConnectSyncEnabled = preferences[PreferenceKeys.HEALTH_CONNECT_SYNC_ENABLED] ?: true,
                lastHealthConnectSync = preferences[PreferenceKeys.LAST_HEALTH_CONNECT_SYNC] ?: 0L,
                healthConnectDataSources = preferences[PreferenceKeys.HEALTH_CONNECT_DATA_SOURCES] ?: emptySet(),
                
                // Onboarding & Setup
                isOnboardingCompleted = preferences[PreferenceKeys.IS_ONBOARDING_COMPLETED] ?: false,
                isProfileSetupCompleted = preferences[PreferenceKeys.IS_PROFILE_SETUP_COMPLETED] ?: false,
                isPermissionsSetupCompleted = preferences[PreferenceKeys.IS_PERMISSIONS_SETUP_COMPLETED] ?: false,
                hasSeenFeatureTour = preferences[PreferenceKeys.HAS_SEEN_FEATURE_TOUR] ?: false,
                appVersion = preferences[PreferenceKeys.APP_VERSION] ?: "",
                lastAppUpdate = preferences[PreferenceKeys.LAST_APP_UPDATE] ?: 0L,
                
                // Advanced Settings
                enableDebugMode = preferences[PreferenceKeys.ENABLE_DEBUG_MODE] ?: false,
                crashReportingEnabled = preferences[PreferenceKeys.CRASH_REPORTING_ENABLED] ?: true,
                dataRetentionDays = preferences[PreferenceKeys.DATA_RETENTION_DAYS] ?: 365,
                autoUploadRuns = preferences[PreferenceKeys.AUTO_UPLOAD_RUNS] ?: true,
                gpsAccuracyThreshold = preferences[PreferenceKeys.GPS_ACCURACY_THRESHOLD] ?: 10f,
                autoPauseEnabled = preferences[PreferenceKeys.AUTO_PAUSE_ENABLED] ?: true,
                autoPauseSensitivity = preferences[PreferenceKeys.AUTO_PAUSE_SENSITIVITY] ?: 0.5f
            )
        }
        .catch { exception ->
            Timber.e(exception, "Error reading preferences")
            emit(UserPreferences()) // Emit default preferences on error
        }
    
    /**
     * Get current user preferences (one-time read)
     */
    suspend fun getUserPreferences(): Result<UserPreferences> {
        return try {
            val preferences = userPreferencesFlow.first()
            Result.Success(preferences)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user preferences")
            Result.Error(e)
        }
    }
    
    /**
     * Update user profile information
     */
    suspend fun updateUserProfile(
        userId: String? = null,
        userName: String? = null,
        userEmail: String? = null,
        profileImageUrl: String? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                userId?.let { preferences[PreferenceKeys.USER_ID] = it }
                userName?.let { preferences[PreferenceKeys.USER_NAME] = it }
                userEmail?.let { preferences[PreferenceKeys.USER_EMAIL] = it }
                profileImageUrl?.let { preferences[PreferenceKeys.PROFILE_IMAGE_URL] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update user profile")
            Result.Error(e)
        }
    }
    
    /**
     * Update fitness settings
     */
    suspend fun updateFitnessSettings(
        weightKg: Float? = null,
        heightCm: Int? = null,
        birthYear: Int? = null,
        fitnessLevel: FitnessLevel? = null,
        preferredUnits: Units? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                weightKg?.let { preferences[PreferenceKeys.WEIGHT_KG] = it }
                heightCm?.let { preferences[PreferenceKeys.HEIGHT_CM] = it }
                birthYear?.let { preferences[PreferenceKeys.BIRTH_YEAR] = it }
                fitnessLevel?.let { preferences[PreferenceKeys.FITNESS_LEVEL] = it.name }
                preferredUnits?.let { preferences[PreferenceKeys.PREFERRED_UNITS] = it.name }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update fitness settings")
            Result.Error(e)
        }
    }
    
    /**
     * Update coaching settings
     */
    suspend fun updateCoachingSettings(
        selectedCoachId: String? = null,
        coachingFrequency: CoachingFrequency? = null,
        enableVoiceCoaching: Boolean? = null,
        coachingLanguage: String? = null,
        voiceVolume: Float? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                selectedCoachId?.let { preferences[PreferenceKeys.SELECTED_COACH_ID] = it }
                coachingFrequency?.let { preferences[PreferenceKeys.COACHING_FREQUENCY] = it.name }
                enableVoiceCoaching?.let { preferences[PreferenceKeys.ENABLE_VOICE_COACHING] = it }
                coachingLanguage?.let { preferences[PreferenceKeys.COACHING_LANGUAGE] = it }
                voiceVolume?.let { preferences[PreferenceKeys.VOICE_VOLUME] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update coaching settings")
            Result.Error(e)
        }
    }
    
    /**
     * Update Spotify settings
     */
    suspend fun updateSpotifySettings(
        enableSpotifyIntegration: Boolean? = null,
        spotifyAccessToken: String? = null,
        spotifyRefreshToken: String? = null,
        preferredMusicGenres: Set<String>? = null,
        targetBpm: Int? = null,
        enableBpmMatching: Boolean? = null,
        musicVolume: Float? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                enableSpotifyIntegration?.let { preferences[PreferenceKeys.ENABLE_SPOTIFY_INTEGRATION] = it }
                spotifyAccessToken?.let { preferences[PreferenceKeys.SPOTIFY_ACCESS_TOKEN] = it }
                spotifyRefreshToken?.let { preferences[PreferenceKeys.SPOTIFY_REFRESH_TOKEN] = it }
                preferredMusicGenres?.let { preferences[PreferenceKeys.PREFERRED_MUSIC_GENRES] = it }
                targetBpm?.let { preferences[PreferenceKeys.TARGET_BPM] = it }
                enableBpmMatching?.let { preferences[PreferenceKeys.ENABLE_BPM_MATCHING] = it }
                musicVolume?.let { preferences[PreferenceKeys.MUSIC_VOLUME] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update Spotify settings")
            Result.Error(e)
        }
    }
    
    /**
     * Update permission states
     */
    suspend fun updatePermissions(
        hasLocationPermission: Boolean? = null,
        hasHealthConnectPermission: Boolean? = null,
        hasNotificationPermission: Boolean? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                hasLocationPermission?.let { preferences[PreferenceKeys.HAS_LOCATION_PERMISSION] = it }
                hasHealthConnectPermission?.let { preferences[PreferenceKeys.HAS_HEALTH_CONNECT_PERMISSION] = it }
                hasNotificationPermission?.let { preferences[PreferenceKeys.HAS_NOTIFICATION_PERMISSION] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update permissions")
            Result.Error(e)
        }
    }
    
    /**
     * Update onboarding progress
     */
    suspend fun updateOnboardingProgress(
        isOnboardingCompleted: Boolean? = null,
        isProfileSetupCompleted: Boolean? = null,
        isPermissionsSetupCompleted: Boolean? = null,
        hasSeenFeatureTour: Boolean? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                isOnboardingCompleted?.let { preferences[PreferenceKeys.IS_ONBOARDING_COMPLETED] = it }
                isProfileSetupCompleted?.let { preferences[PreferenceKeys.IS_PROFILE_SETUP_COMPLETED] = it }
                isPermissionsSetupCompleted?.let { preferences[PreferenceKeys.IS_PERMISSIONS_SETUP_COMPLETED] = it }
                hasSeenFeatureTour?.let { preferences[PreferenceKeys.HAS_SEEN_FEATURE_TOUR] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update onboarding progress")
            Result.Error(e)
        }
    }
    
    /**
     * Update app settings
     */
    suspend fun updateAppSettings(
        themeMode: ThemeMode? = null,
        enableDynamicColor: Boolean? = null,
        enableHapticFeedback: Boolean? = null,
        enableNotifications: Boolean? = null,
        notificationSound: Boolean? = null
    ): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                themeMode?.let { preferences[PreferenceKeys.THEME_MODE] = it.name }
                enableDynamicColor?.let { preferences[PreferenceKeys.ENABLE_DYNAMIC_COLOR] = it }
                enableHapticFeedback?.let { preferences[PreferenceKeys.ENABLE_HAPTIC_FEEDBACK] = it }
                enableNotifications?.let { preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] = it }
                notificationSound?.let { preferences[PreferenceKeys.NOTIFICATION_SOUND] = it }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update app settings")
            Result.Error(e)
        }
    }
    
    /**
     * Clear all preferences (useful for logout)
     */
    suspend fun clearAllPreferences(): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear preferences")
            Result.Error(e)
        }
    }
    
    /**
     * Update Health Connect sync timestamp
     */
    suspend fun updateHealthConnectSyncTimestamp(): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.LAST_HEALTH_CONNECT_SYNC] = System.currentTimeMillis()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update Health Connect sync timestamp")
            Result.Error(e)
        }
    }
    
    /**
     * Update app version and timestamp
     */
    suspend fun updateAppVersion(version: String): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.APP_VERSION] = version
                preferences[PreferenceKeys.LAST_APP_UPDATE] = System.currentTimeMillis()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update app version")
            Result.Error(e)
        }
    }
}