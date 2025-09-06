package com.runiq.presentation.components.sections

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.presentation.components.buttons.ModernButtonVariant
import com.runiq.presentation.components.buttons.ModernStartRunButton
import com.runiq.presentation.components.buttons.RunButtonState
import com.runiq.ui.theme.ModernAnimations
import com.runiq.ui.theme.ModernEffects
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Modern hero section with 21st.dev design principles
 * Features gradient backgrounds, glassmorphism, and engaging animations
 */
@Composable
fun ModernHeroSection(
    title: String,
    subtitle: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    primaryActionText: String = "Get Started",
    showStats: Boolean = true,
    variant: ModernHeroVariant = ModernHeroVariant.GRADIENT
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (variant) {
                    ModernHeroVariant.GRADIENT -> Modifier.background(
                        ModernEffects.Gradients.heroGradient()
                    )
                    ModernHeroVariant.GLASS -> Modifier.background(
                        Color.Transparent
                    )
                    ModernHeroVariant.SOLID -> Modifier.background(
                        MaterialTheme.colorScheme.surface
                    )
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RunIQSpacing.sectionSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xl)
        ) {
            // Hero content
            ModernHeroContent(
                title = title,
                subtitle = subtitle,
                variant = variant
            )
            
            // Primary action button
            ModernStartRunButton(
                state = RunButtonState.READY,
                onStartRun = onPrimaryAction,
                onPauseRun = { },
                onResumeRun = { },
                onStopRun = { },
                variant = when (variant) {
                    ModernHeroVariant.GLASS -> ModernButtonVariant.GLASSMORPHISM
                    ModernHeroVariant.GRADIENT -> ModernButtonVariant.GRADIENT
                    ModernHeroVariant.SOLID -> ModernButtonVariant.ELEVATED
                }
            )
            
            // Stats preview
            if (showStats) {
                ModernStatsPreview(variant = variant)
            }
        }
        
        // Decorative elements
        ModernHeroDecorations(variant = variant)
    }
}

/**
 * Hero content with animated text
 */
@Composable
private fun ModernHeroContent(
    title: String,
    subtitle: String,
    variant: ModernHeroVariant,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        // Main title with staggered animation
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = when (variant) {
                ModernHeroVariant.GLASS -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.fadeInUp(delay = 200)
        )
        
        // Subtitle with delayed animation
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = when (variant) {
                ModernHeroVariant.GLASS -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.fadeInUp(delay = 400)
        )
    }
}

/**
 * Modern stats preview with glassmorphism cards
 */
@Composable
private fun ModernStatsPreview(
    variant: ModernHeroVariant,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fadeInUp(delay = 600),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val stats = listOf(
            StatItem("12", "Runs", Icons.Default.TrendingUp),
            StatItem("47.2", "km", Icons.Default.TrendingUp),
            StatItem("5:24", "avg/km", Icons.Default.TrendingUp)
        )
        
        stats.forEachIndexed { index, stat ->
            ModernStatCard(
                value = stat.value,
                label = stat.label,
                icon = stat.icon,
                variant = variant,
                modifier = Modifier
                    .weight(1f)
                    .fadeInUp(delay = 800 + (index * 100))
            )
        }
    }
}

/**
 * Individual stat card with modern styling
 */
@Composable
private fun ModernStatCard(
    value: String,
    label: String,
    icon: ImageVector,
    variant: ModernHeroVariant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = RunIQSpacing.xs),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when (variant) {
                ModernHeroVariant.GLASS -> 0.dp
                else -> ModernEffects.Shadows.small
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (variant) {
                ModernHeroVariant.GLASS -> Color.Transparent
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            }
        ),
        shape = RoundedCornerShape(ModernEffects.Radius.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (variant == ModernHeroVariant.GLASS) {
                        Modifier.glassmorphism(alpha = 0.15f)
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(RunIQSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Decorative background elements
 */
@Composable
private fun ModernHeroDecorations(
    variant: ModernHeroVariant,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Floating geometric shapes
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((40 + index * 20).dp)
                    .clip(RoundedCornerShape(ModernEffects.Radius.lg))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.05f - index * 0.01f
                        )
                    )
                    .align(
                        when (index) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopEnd
                            else -> Alignment.BottomStart
                        }
                    )
                    .fadeInUp(delay = 1000 + index * 200)
            )
        }
    }
}

/**
 * Hero variant options
 */
enum class ModernHeroVariant {
    GRADIENT,   // Gradient background
    GLASS,      // Glassmorphism effect
    SOLID       // Solid background
}

/**
 * Data class for stat items
 */
private data class StatItem(
    val value: String,
    val label: String,
    val icon: ImageVector
)

// Extension functions for animations
@Composable
private fun Modifier.fadeInUp(delay: Int = 0): Modifier = 
    this.then(ModernAnimations.fadeInUp(delay = delay))

@Composable
private fun Modifier.glassmorphism(alpha: Float = 0.1f): Modifier = 
    this.then(ModernEffects.glassmorphism(alpha = alpha))

@Preview(showBackground = true, name = "Modern Hero - Gradient")
@Composable
private fun ModernHeroSectionGradientPreview() {
    RunIQTheme {
        ModernHeroSection(
            title = "Ready to Run?",
            subtitle = "Your AI-powered running companion",
            onPrimaryAction = { },
            variant = ModernHeroVariant.GRADIENT
        )
    }
}

@Preview(showBackground = true, name = "Modern Hero - Glass")
@Composable
private fun ModernHeroSectionGlassPreview() {
    RunIQTheme {
        ModernHeroSection(
            title = "Welcome Back",
            subtitle = "Let's continue your fitness journey",
            onPrimaryAction = { },
            variant = ModernHeroVariant.GLASS
        )
    }
}

@Preview(showBackground = true, name = "Modern Hero - No Stats")
@Composable
private fun ModernHeroSectionNoStatsPreview() {
    RunIQTheme {
        ModernHeroSection(
            title = "Get Started",
            subtitle = "Begin your running adventure today",
            onPrimaryAction = { },
            showStats = false,
            variant = ModernHeroVariant.SOLID
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Modern Hero Dark")
@Composable
private fun ModernHeroSectionDarkPreview() {
    RunIQTheme {
        ModernHeroSection(
            title = "Good Evening",
            subtitle = "Time for your night run?",
            onPrimaryAction = { },
            variant = ModernHeroVariant.GRADIENT
        )
    }
}