package com.runiq.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * RunIQ app shapes following Material3 design system
 * Optimized for modern, clean fitness app interface
 */
val RunIQShapes = Shapes(
    // Extra small - Chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - Cards, medium buttons
    small = RoundedCornerShape(8.dp),
    
    // Medium - Large cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large - Sheets, large surfaces
    large = RoundedCornerShape(16.dp),
    
    // Extra large - Full screen components
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * Custom shapes specific to RunIQ components
 */
object RunIQCustomShapes {
    
    // Metric cards - Slightly rounded for clean look
    val MetricCard = RoundedCornerShape(12.dp)
    
    // Action buttons - More rounded for friendly feel
    val ActionButton = RoundedCornerShape(20.dp)
    
    // FAB shape - Circular
    val Fab = RoundedCornerShape(16.dp)
    
    // Bottom sheet
    val BottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Top bar
    val TopBar = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    // Progress indicators
    val ProgressIndicator = RoundedCornerShape(8.dp)
    
    // Input fields
    val InputField = RoundedCornerShape(8.dp)
    
    // Achievement badges
    val AchievementBadge = RoundedCornerShape(50) // Circular
    
    // Map overlay
    val MapOverlay = RoundedCornerShape(16.dp)
    
    // Coach avatar
    val CoachAvatar = RoundedCornerShape(50) // Circular
    
    // Music player
    val MusicPlayer = RoundedCornerShape(24.dp)
}