package com.runiq.presentation.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.presentation.components.cards.CoachPersonality
import com.runiq.presentation.components.cards.CoachPersonalityCard
import com.runiq.presentation.components.cards.ModernCardVariant
import com.runiq.presentation.components.cards.ModernRunMetricsCard
import com.runiq.presentation.components.cards.RunMetrics
import com.runiq.presentation.components.charts.StatsRingData
import com.runiq.presentation.components.charts.StatsRingRow
import com.runiq.presentation.components.lists.RunHistoryEntry
import com.runiq.presentation.components.lists.RunHistoryItem
import com.runiq.presentation.components.sections.ModernHeroSection
import com.runiq.presentation.components.sections.ModernHeroVariant
import com.runiq.ui.theme.ModernAnimations
import com.runiq.ui.theme.ModernEffects
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme
import java.time.LocalDateTime

/**
 * Modern version of HomeScreen using 21st.dev design principles
 * Features glassmorphism, advanced animations, and modern visual hierarchy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    onNavigateToActiveRun: () -> Unit,
    onNavigateToCoachSelection: () -> Unit,
    onNavigateToRunDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Good morning! ☀️",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fadeInUp(delay = 100)
                        )
                        Text(
                            text = "Ready to run?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fadeInUp(delay = 200)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Navigate to settings */ },
                        modifier = Modifier.fadeInUp(delay = 300)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sectionSpacing)
        ) {
            // Hero Section
            item {
                ModernHeroSection(
                    title = "Let's Go Running",
                    subtitle = "Your AI coach is ready",
                    onPrimaryAction = onNavigateToActiveRun,
                    variant = ModernHeroVariant.GRADIENT,
                    modifier = Modifier.fadeInUp(delay = 400)
                )
            }
            
            // Weekly Progress Section
            item {
                ModernSection(
                    title = "This Week's Progress",
                    animationDelay = 600
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
                    ) {
                        // Weekly metrics card
                        ModernRunMetricsCard(
                            metrics = RunMetrics(
                                pace = "5:32",
                                distance = "15.2",
                                duration = "84:15",
                                heartRate = 142,
                                calories = 1247
                            ),
                            variant = ModernCardVariant.GLASS,
                            isCompact = true,
                            modifier = Modifier.fadeInUp(delay = 700)
                        )
                        
                        // Progress rings
                        StatsRingRow(
                            rings = listOf(
                                StatsRingData(
                                    value = 15.2f,
                                    maxValue = 20f,
                                    label = "Distance",
                                    color = MaterialTheme.colorScheme.primary,
                                    unit = "km"
                                ),
                                StatsRingData(
                                    value = 3f,
                                    maxValue = 4f,
                                    label = "Runs",
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                StatsRingData(
                                    value = 180f,
                                    maxValue = 240f,
                                    label = "Minutes",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    unit = "min"
                                )
                            ),
                            modifier = Modifier.fadeInUp(delay = 800)
                        )
                    }
                }
            }
            
            // AI Coach Section
            item {
                ModernSection(
                    title = "Your AI Coach",
                    subtitle = "Personalized guidance for every run",
                    animationDelay = 900,
                    action = {
                        IconButton(onClick = onNavigateToCoachSelection) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Change coach",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    CoachPersonalityCard(
                        coach = CoachPersonality(
                            id = "current",
                            name = "Alex",
                            description = "Your motivational running coach ready to push you to new limits and help you achieve your personal best!",
                            personality = "Motivational & Energetic",
                            color = MaterialTheme.colorScheme.primary
                        ),
                        isSelected = true,
                        onSelect = { onNavigateToCoachSelection() },
                        modifier = Modifier.fadeInUp(delay = 1000)
                    )
                }
            }
            
            // Recent Activity Section
            item {
                ModernSection(
                    title = "Recent Activity",
                    subtitle = "Your latest running achievements",
                    animationDelay = 1100
                ) {
                    val recentRuns = listOf(
                        RunHistoryEntry(
                            id = "1",
                            date = LocalDateTime.now().minusDays(1),
                            distance = 5.2,
                            duration = "26:45",
                            pace = "5:08",
                            calories = 342,
                            workoutType = "Morning Run"
                        ),
                        RunHistoryEntry(
                            id = "2",
                            date = LocalDateTime.now().minusDays(3),
                            distance = 8.1,
                            duration = "41:23",
                            pace = "5:06",
                            calories = 567,
                            workoutType = "Long Run"
                        ),
                        RunHistoryEntry(
                            id = "3",
                            date = LocalDateTime.now().minusDays(5),
                            distance = 3.5,
                            duration = "18:12",
                            pace = "5:12",
                            workoutType = "Recovery Run"
                        )
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
                    ) {
                        recentRuns.take(3).forEachIndexed { index, run ->
                            RunHistoryItem(
                                run = run,
                                onClick = onNavigateToRunDetail,
                                showDivider = index < 2,
                                modifier = Modifier.fadeInUp(
                                    delay = 1200 + (index * 100)
                                )
                            )
                        }
                    }
                }
            }
            
            // Bottom spacing for better scrolling experience
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.massive))
            }
        }
    }
}

/**
 * Modern section wrapper with consistent styling
 */
@Composable
private fun ModernSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    animationDelay: Int = 0,
    action: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RunIQSpacing.screenPadding)
            .fadeInUp(delay = animationDelay),
        verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            action?.invoke()
        }
        
        // Section content
        content()
    }
}

// Extension function for fade in animation
@Composable
private fun Modifier.fadeInUp(delay: Int = 0): Modifier = 
    this.then(ModernAnimations.fadeInUp(delay = delay))

@Preview(showBackground = true, name = "Modern Home Screen")
@Composable
private fun ModernHomeScreenPreview() {
    RunIQTheme {
        ModernHomeScreen(
            onNavigateToActiveRun = { },
            onNavigateToCoachSelection = { },
            onNavigateToRunDetail = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Modern Home Dark")
@Composable
private fun ModernHomeScreenDarkPreview() {
    RunIQTheme {
        ModernHomeScreen(
            onNavigateToActiveRun = { },
            onNavigateToCoachSelection = { },
            onNavigateToRunDetail = { }
        )
    }
}