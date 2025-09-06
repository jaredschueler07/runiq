package com.runiq.ui.theme

import androidx.compose.ui.unit.dp

/**
 * RunIQ Spacing System
 * Consistent spacing values for padding, margins, and layout
 */
object RunIQSpacing {
    // Micro spacing - For fine adjustments
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    
    // Small spacing - For compact layouts
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    
    // Large spacing - For generous layouts
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    
    // Extra large spacing - For major sections
    val huge = 48.dp
    val massive = 64.dp
    
    // Component-specific spacing
    val cardPadding = md
    val screenPadding = md
    val sectionSpacing = xl
    val itemSpacing = sm
    
    // Metrics display spacing
    val metricsCardPadding = lg
    val metricsSpacing = md
    val metricLabelSpacing = xs
}

/**
 * Common elevation values for cards and surfaces
 */
object RunIQElevation {
    val none = 0.dp
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val extraLarge = 12.dp
}