package com.runiq.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Modern visual effects for 21st.dev style components
 * Includes glassmorphism, gradients, and advanced visual treatments
 */
object ModernEffects {
    
    /**
     * Glassmorphism effect modifier
     */
    @Composable
    fun Modifier.glassmorphism(
        alpha: Float = 0.1f,
        blur: Float = 10f,
        borderAlpha: Float = 0.2f
    ): Modifier {
        val surfaceColor = MaterialTheme.colorScheme.surface
        val borderColor = MaterialTheme.colorScheme.outline
        
        return this
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = surfaceColor.copy(alpha = alpha),
                shape = RoundedCornerShape(16.dp)
            )
            .blur(blur.dp)
    }
    
    /**
     * Modern gradient backgrounds
     */
    object Gradients {
        @Composable
        fun primaryGradient(): Brush {
            return Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
            )
        }
        
        @Composable
        fun surfaceGradient(): Brush {
            return Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        }
        
        @Composable
        fun heroGradient(): Brush {
            return Brush.radialGradient(
                colors = listOf(
                    RunIQPrimary.copy(alpha = 0.3f),
                    Color.Transparent,
                    RunIQSecondary.copy(alpha = 0.2f)
                )
            )
        }
        
        @Composable
        fun metricGradient(color: Color): Brush {
            return Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.8f),
                    color.copy(alpha = 0.4f),
                    color.copy(alpha = 0.1f)
                )
            )
        }
    }
    
    /**
     * Modern shadow and elevation effects
     */
    object Shadows {
        val small = 4.dp
        val medium = 8.dp
        val large = 16.dp
        val extraLarge = 24.dp
    }
    
    /**
     * Modern border radius values
     */
    object Radius {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 20.dp
        val xxl = 24.dp
        val full = 9999.dp
    }
    
    /**
     * Modern color overlays
     */
    object Overlays {
        @Composable
        fun primary(alpha: Float = 0.1f) = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
        
        @Composable
        fun surface(alpha: Float = 0.05f) = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
        
        @Composable
        fun success(alpha: Float = 0.1f) = RunIQSuccess.copy(alpha = alpha)
        
        @Composable
        fun warning(alpha: Float = 0.1f) = RunIQWarning.copy(alpha = alpha)
        
        @Composable
        fun error(alpha: Float = 0.1f) = RunIQError.copy(alpha = alpha)
    }
}