package com.runiq.data.local.preferences

import androidx.annotation.Keep

/**
 * User preferences data class containing all user settings
 */
@Keep
data class UserPreferences(
    // User Profile
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUrl: String = "",
    
    // Fitness Settings
    val weightKg: Float = 70f,
    val heightCm: Int = 170,
    val birthYear: Int = 1990,
    val fitnessLevel: FitnessLevel = FitnessLevel.INTERMEDIATE,
    val preferredUnits: Units = Units.METRIC,
    
    // Running Preferences
    val defaultWorkoutType: WorkoutType = WorkoutType.EASY_RUN,
    val targetWeeklyDistance: Float = 20f, // km
    val targetWeeklyRuns: Int = 3,
    val preferredRunningDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY, 
        DayOfWeek.WEDNESDAY, 
        DayOfWeek.FRIDAY
    ),
    
    // Coaching Settings
    val selectedCoachId: String = "default",
    val coachingFrequency: CoachingFrequency = CoachingFrequency.MODERATE,
    val enableVoiceCoaching: Boolean = true,
    val coachingLanguage: String = "en",
    val voiceVolume: Float = 0.8f,
    
    // Music Settings
    val enableSpotifyIntegration: Boolean = false,
    val spotifyAccessToken: String = "",
    val spotifyRefreshToken: String = "",
    val preferredMusicGenres: Set<String> = emptySet(),
    val targetBpm: Int = 120,
    val enableBpmMatching: Boolean = true,
    val musicVolume: Float = 0.7f,
    
    // Privacy & Permissions
    val hasLocationPermission: Boolean = false,
    val hasHealthConnectPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val enableDataSharing: Boolean = false,
    val enableAnalytics: Boolean = true,
    
    // App Settings
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val enableDynamicColor: Boolean = true,
    val enableHapticFeedback: Boolean = true,
    val enableNotifications: Boolean = true,
    val notificationSound: Boolean = true,
    val enableAutoLock: Boolean = false,
    
    // Health Connect Settings
    val healthConnectSyncEnabled: Boolean = true,
    val lastHealthConnectSync: Long = 0L,
    val healthConnectDataSources: Set<String> = emptySet(),
    
    // Onboarding & Setup
    val isOnboardingCompleted: Boolean = false,
    val isProfileSetupCompleted: Boolean = false,
    val isPermissionsSetupCompleted: Boolean = false,
    val hasSeenFeatureTour: Boolean = false,
    val appVersion: String = "",
    val lastAppUpdate: Long = 0L,
    
    // Advanced Settings
    val enableDebugMode: Boolean = false,
    val crashReportingEnabled: Boolean = true,
    val dataRetentionDays: Int = 365,
    val autoUploadRuns: Boolean = true,
    val gpsAccuracyThreshold: Float = 10f, // meters
    val autoPauseEnabled: Boolean = true,
    val autoPauseSensitivity: Float = 0.5f
)

/**
 * Fitness level enumeration
 */
enum class FitnessLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    ELITE
}

/**
 * Unit system preferences
 */
enum class Units {
    METRIC,
    IMPERIAL
}

/**
 * Workout type enumeration
 */
enum class WorkoutType(val displayName: String) {
    EASY_RUN("Easy Run"),
    TEMPO_RUN("Tempo Run"),
    INTERVAL_TRAINING("Interval Training"),
    LONG_RUN("Long Run"),
    RECOVERY_RUN("Recovery Run"),
    FARTLEK("Fartlek"),
    HILL_TRAINING("Hill Training"),
    RACE("Race")
}

/**
 * Day of week enumeration
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

/**
 * Coaching frequency settings
 */
enum class CoachingFrequency {
    MINIMAL,    // Every 5-10 minutes
    MODERATE,   // Every 2-5 minutes
    FREQUENT,   // Every 1-2 minutes
    MAXIMUM     // Every 30-60 seconds
}

/**
 * Theme mode options
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Extension functions for UserPreferences
 */

/**
 * Check if user has completed basic setup
 */
val UserPreferences.isBasicSetupComplete: Boolean
    get() = isOnboardingCompleted && isProfileSetupCompleted

/**
 * Check if user has all required permissions
 */
val UserPreferences.hasRequiredPermissions: Boolean
    get() = hasLocationPermission && hasHealthConnectPermission

/**
 * Get user's age based on birth year
 */
val UserPreferences.age: Int
    get() = java.time.LocalDate.now().year - birthYear

/**
 * Get BMI calculation
 */
val UserPreferences.bmi: Float
    get() {
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

/**
 * Get BMI category
 */
val UserPreferences.bmiCategory: String
    get() = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25f -> "Normal weight"
        bmi < 30f -> "Overweight"
        else -> "Obese"
    }

/**
 * Check if Spotify is properly configured
 */
val UserPreferences.isSpotifyConfigured: Boolean
    get() = enableSpotifyIntegration && 
            spotifyAccessToken.isNotBlank() && 
            spotifyRefreshToken.isNotBlank()

/**
 * Get coaching interval in milliseconds based on frequency
 */
val UserPreferences.coachingIntervalMs: Long
    get() = when (coachingFrequency) {
        CoachingFrequency.MINIMAL -> 7 * 60 * 1000L    // 7 minutes
        CoachingFrequency.MODERATE -> 3 * 60 * 1000L   // 3 minutes
        CoachingFrequency.FREQUENT -> 90 * 1000L       // 1.5 minutes
        CoachingFrequency.MAXIMUM -> 45 * 1000L        // 45 seconds
    }