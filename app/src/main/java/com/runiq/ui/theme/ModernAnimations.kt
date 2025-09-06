package com.runiq.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Modern animation system for 21st.dev style micro-interactions
 */
object ModernAnimations {
    
    /**
     * Standard animation durations following modern design principles
     */
    object Durations {
        const val FAST = 150
        const val NORMAL = 300
        const val SLOW = 500
        const val EXTRA_SLOW = 800
    }
    
    /**
     * Modern easing curves
     */
    object Easings {
        val EMPHASIZED = FastOutSlowInEasing
        val STANDARD = LinearEasing
        val SPRING_BOUNCY = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
        val SPRING_SMOOTH = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }
    
    /**
     * Entrance animations
     */
    @Composable
    fun Modifier.fadeInUp(
        duration: Int = Durations.NORMAL,
        delay: Int = 0
    ): Modifier {
        val animatable = remember { Animatable(0f) }
        
        LaunchedEffect(Unit) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = Easings.EMPHASIZED
                )
            )
        }
        
        return this
            .graphicsLayer {
                alpha = animatable.value
                translationY = (1f - animatable.value) * 50f
            }
    }
    
    @Composable
    fun Modifier.scaleIn(
        duration: Int = Durations.NORMAL,
        delay: Int = 0
    ): Modifier {
        val animatable = remember { Animatable(0.8f) }
        
        LaunchedEffect(Unit) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    delayMillis = delay,
                    easing = Easings.EMPHASIZED
                )
            )
        }
        
        return this.scale(animatable.value)
    }
    
    /**
     * Hover and interaction effects
     */
    @Composable
    fun Modifier.hoverScale(
        scale: Float = 1.05f,
        duration: Int = Durations.FAST
    ): Modifier {
        return this.graphicsLayer {
            // This would be implemented with pointer input in real usage
            scaleX = scale
            scaleY = scale
        }
    }
    
    /**
     * Loading animations
     */
    @Composable
    fun Modifier.pulse(): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = Easings.EMPHASIZED),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
        
        return this.scale(scale)
    }
    
    @Composable
    fun Modifier.shimmer(): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
        val translateX by infiniteTransition.animateFloat(
            initialValue = -100f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )
        
        return this.offset(x = translateX.dp)
    }
    
    @Composable
    fun Modifier.rotate(): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "rotate")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        
        return this.rotate(rotation)
    }
    
    /**
     * Staggered animations for lists
     */
    @Composable
    fun staggeredDelay(index: Int, baseDelay: Int = 50): Int {
        return baseDelay * index
    }
    
    /**
     * Spring animations for interactive elements
     */
    fun <T> springAnimation(
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessLow
    ): AnimationSpec<T> = spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )
    
    /**
     * Tween animations with modern easing
     */
    fun <T> tweenAnimation(
        duration: Int = Durations.NORMAL,
        delay: Int = 0
    ): AnimationSpec<T> = tween(
        durationMillis = duration,
        delayMillis = delay,
        easing = Easings.EMPHASIZED
    )
}