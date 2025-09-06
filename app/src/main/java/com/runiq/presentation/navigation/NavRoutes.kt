package com.runiq.presentation.navigation

/**
 * Navigation routes for RunIQ app
 * Sealed class to ensure type safety for navigation
 */
sealed class NavRoutes(val route: String) {
    // Main navigation routes
    data object Home : NavRoutes("home")
    data object ActiveRun : NavRoutes("active_run")
    data object History : NavRoutes("history")
    data object Profile : NavRoutes("profile")
    
    // Secondary screens
    data object CoachSelection : NavRoutes("coach_selection")
    data object RunDetail : NavRoutes("run_detail/{runId}") {
        fun createRoute(runId: String) = "run_detail/$runId"
    }
    data object Settings : NavRoutes("settings")
    data object MusicSettings : NavRoutes("music_settings")
    
    // Onboarding flow
    data object Onboarding : NavRoutes("onboarding")
    data object Permissions : NavRoutes("permissions")
    
    companion object {
        /**
         * Get all bottom navigation routes
         */
        val bottomNavRoutes = listOf(Home, History, Profile)
        
        /**
         * Get all main routes that should show bottom navigation
         */
        val mainRoutes = setOf(
            Home.route,
            History.route,
            Profile.route
        )
    }
}