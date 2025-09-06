package com.runiq.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.runiq.presentation.screens.coach.CoachSelectionScreen
import com.runiq.presentation.screens.history.RunHistoryScreen
import com.runiq.presentation.screens.home.HomeScreen
import com.runiq.presentation.screens.onboarding.OnboardingScreen
import com.runiq.presentation.screens.onboarding.PermissionsScreen
import com.runiq.presentation.screens.profile.ProfileScreen
import com.runiq.presentation.screens.run.ActiveRunScreen
import com.runiq.presentation.screens.run.RunDetailScreen
import com.runiq.presentation.screens.settings.MusicSettingsScreen
import com.runiq.presentation.screens.settings.SettingsScreen

/**
 * Main navigation component for RunIQ app
 * Handles all screen navigation and animations
 */
@Composable
fun RunIQNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Home.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine if we should show bottom navigation
    val shouldShowBottomNav = currentRoute in NavRoutes.mainRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNav) {
                RunIQBottomNavBar(
                    currentRoute = currentRoute ?: NavRoutes.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the graph's start destination to avoid building up a large stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Main screens with bottom navigation
            composable(NavRoutes.Home.route) {
                HomeScreen(
                    onNavigateToActiveRun = {
                        navController.navigate(NavRoutes.ActiveRun.route)
                    },
                    onNavigateToCoachSelection = {
                        navController.navigate(NavRoutes.CoachSelection.route)
                    },
                    onNavigateToRunDetail = { runId ->
                        navController.navigate(NavRoutes.RunDetail.createRoute(runId))
                    }
                )
            }
            
            composable(NavRoutes.History.route) {
                RunHistoryScreen(
                    onNavigateToRunDetail = { runId ->
                        navController.navigate(NavRoutes.RunDetail.createRoute(runId))
                    }
                )
            }
            
            composable(NavRoutes.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(NavRoutes.Settings.route)
                    },
                    onNavigateToCoachSelection = {
                        navController.navigate(NavRoutes.CoachSelection.route)
                    }
                )
            }
            
            // Full-screen experiences (no bottom nav)
            composable(NavRoutes.ActiveRun.route) {
                ActiveRunScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRunComplete = { runId ->
                        // Navigate to run detail and clear back stack
                        navController.navigate(NavRoutes.RunDetail.createRoute(runId)) {
                            popUpTo(NavRoutes.Home.route)
                        }
                    }
                )
            }
            
            // Detail screens
            composable(NavRoutes.RunDetail.route) { backStackEntry ->
                val runId = backStackEntry.arguments?.getString("runId") ?: ""
                RunDetailScreen(
                    runId = runId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Secondary screens
            composable(NavRoutes.CoachSelection.route) {
                CoachSelectionScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onCoachSelected = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(NavRoutes.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMusicSettings = {
                        navController.navigate(NavRoutes.MusicSettings.route)
                    }
                )
            }
            
            composable(NavRoutes.MusicSettings.route) {
                MusicSettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Onboarding flow
            composable(NavRoutes.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToPermissions = {
                        navController.navigate(NavRoutes.Permissions.route) {
                            popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                        }
                    },
                    onSkip = {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(NavRoutes.Permissions.route) {
                PermissionsScreen(
                    onPermissionsGranted = {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Permissions.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}