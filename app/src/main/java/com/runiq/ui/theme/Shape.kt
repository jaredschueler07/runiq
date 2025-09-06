package com.runiq.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * RunIQ Shape System
 * Consistent corner radius values for components
 */
val RunIQShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Custom shapes for specific components
 */
object RunIQCustomShapes {
    val button = RoundedCornerShape(24.dp)
    val card = RoundedCornerShape(12.dp)
    val bottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val chip = RoundedCornerShape(16.dp)
    val fab = RoundedCornerShape(16.dp)
    
    // Circular shapes
    val circular = RoundedCornerShape(50)
    val startRun = RoundedCornerShape(50) // Perfect circle for start button
}