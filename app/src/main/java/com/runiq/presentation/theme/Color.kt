package com.runiq.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * RunIQ app color palette following Material3 design system
 * Colors are optimized for fitness/running app with energetic and motivational feel
 */
object RunIQColors {
    
    // Primary Colors - Energetic Orange/Red for motivation
    val Primary = Color(0xFFFF6B35)          // Vibrant orange-red
    val OnPrimary = Color(0xFFFFFFFF)        // White text on primary
    val PrimaryContainer = Color(0xFFFFDAD6) // Light orange container
    val OnPrimaryContainer = Color(0xFF410002) // Dark text on container
    
    // Secondary Colors - Cool Blue for balance
    val Secondary = Color(0xFF0077BE)         // Professional blue
    val OnSecondary = Color(0xFFFFFFFF)      // White text on secondary
    val SecondaryContainer = Color(0xFFD1E4FF) // Light blue container
    val OnSecondaryContainer = Color(0xFF001D36) // Dark text on container
    
    // Tertiary Colors - Green for success/achievement
    val Tertiary = Color(0xFF006D3B)          // Success green
    val OnTertiary = Color(0xFFFFFFFF)       // White text on tertiary
    val TertiaryContainer = Color(0xFF89F8C7) // Light green container
    val OnTertiaryContainer = Color(0xFF00210F) // Dark text on container
    
    // Error Colors
    val Error = Color(0xFFBA1A1A)            // Standard error red
    val OnError = Color(0xFFFFFFFF)          // White text on error
    val ErrorContainer = Color(0xFFFFDAD6)   // Light error container
    val OnErrorContainer = Color(0xFF410002) // Dark text on error container
    
    // Neutral Colors - Light Theme
    val Background = Color(0xFFFFFBFF)       // Pure white background
    val OnBackground = Color(0xFF1C1B1F)     // Dark text on background
    val Surface = Color(0xFFFFFBFF)          // Surface color
    val OnSurface = Color(0xFF1C1B1F)        // Text on surface
    val SurfaceVariant = Color(0xFFE7E0EC)   // Variant surface
    val OnSurfaceVariant = Color(0xFF49454F) // Text on variant surface
    val Outline = Color(0xFF79747E)          // Border/outline color
    val OutlineVariant = Color(0xFFCAC4D0)   // Variant outline
    
    // Dark Theme Colors
    val DarkPrimary = Color(0xFFFFB4AB)          // Lighter orange for dark theme
    val DarkOnPrimary = Color(0xFF690005)        // Dark text on primary
    val DarkPrimaryContainer = Color(0xFF93000A) // Dark primary container
    val DarkOnPrimaryContainer = Color(0xFFFFDAD6) // Light text on container
    
    val DarkSecondary = Color(0xFF9ECAFF)         // Lighter blue for dark theme
    val DarkOnSecondary = Color(0xFF003258)       // Dark text on secondary
    val DarkSecondaryContainer = Color(0xFF00497D) // Dark secondary container
    val DarkOnSecondaryContainer = Color(0xFFD1E4FF) // Light text on container
    
    val DarkTertiary = Color(0xFF6DDBAA)          // Lighter green for dark theme
    val DarkOnTertiary = Color(0xFF00391D)        // Dark text on tertiary
    val DarkTertiaryContainer = Color(0xFF00522B) // Dark tertiary container
    val DarkOnTertiaryContainer = Color(0xFF89F8C7) // Light text on container
    
    val DarkError = Color(0xFFFFB4AB)            // Light error for dark theme
    val DarkOnError = Color(0xFF690005)          // Dark text on error
    val DarkErrorContainer = Color(0xFF93000A)   // Dark error container
    val DarkOnErrorContainer = Color(0xFFFFDAD6) // Light text on error container
    
    val DarkBackground = Color(0xFF1C1B1F)       // Dark background
    val DarkOnBackground = Color(0xFFE6E1E5)     // Light text on background
    val DarkSurface = Color(0xFF1C1B1F)          // Dark surface
    val DarkOnSurface = Color(0xFFE6E1E5)        // Light text on surface
    val DarkSurfaceVariant = Color(0xFF49454F)   // Dark variant surface
    val DarkOnSurfaceVariant = Color(0xFFCAC4D0) // Light text on variant surface
    val DarkOutline = Color(0xFF938F99)          // Light outline for dark theme
    val DarkOutlineVariant = Color(0xFF49454F)   // Variant outline for dark theme
    
    // Custom RunIQ Colors
    val RunningActive = Color(0xFF4CAF50)        // Green for active running
    val RunningPaused = Color(0xFFFF9800)        // Orange for paused state
    val RunningCompleted = Color(0xFF2196F3)     // Blue for completed runs
    val HeartRateZone1 = Color(0xFF81C784)       // Light green - Easy
    val HeartRateZone2 = Color(0xFFFFB74D)       // Orange - Moderate
    val HeartRateZone3 = Color(0xFFFF8A65)       // Red-orange - Hard
    val HeartRateZone4 = Color(0xFFE57373)       // Red - Very Hard
    val HeartRateZone5 = Color(0xFFAD1457)       // Dark red - Maximum
    
    // GPS and Location
    val GpsConnected = Color(0xFF4CAF50)         // Green for good GPS
    val GpsSearching = Color(0xFFFF9800)         // Orange for searching
    val GpsDisconnected = Color(0xFFF44336)      // Red for no GPS
    
    // Achievement and Progress
    val AchievementGold = Color(0xFFFFD700)      // Gold for achievements
    val AchievementSilver = Color(0xFFC0C0C0)    // Silver for achievements
    val AchievementBronze = Color(0xFFCD7F32)    // Bronze for achievements
    val ProgressComplete = Color(0xFF4CAF50)     // Green for completed progress
    val ProgressIncomplete = Color(0xFFE0E0E0)   // Gray for incomplete progress
    
    // Warning and Info
    val Warning = Color(0xFFFF9800)              // Orange for warnings
    val Info = Color(0xFF2196F3)                 // Blue for information
    val Success = Color(0xFF4CAF50)              // Green for success
    
    // Spotify Integration
    val SpotifyGreen = Color(0xFF1DB954)         // Official Spotify green
    val SpotifyBlack = Color(0xFF191414)         // Official Spotify black
    
    // Transparent overlays
    val OverlayLight = Color(0x80FFFFFF)         // Light overlay
    val OverlayDark = Color(0x80000000)          // Dark overlay
}