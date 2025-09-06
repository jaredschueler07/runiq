package com.runiq.presentation.screens.coach

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.runiq.presentation.components.cards.CoachPersonality
import com.runiq.presentation.components.cards.CoachPersonalityCard
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Coach selection screen - Choose AI coaching personality
 * Allows users to select their preferred coaching style and personality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachSelectionScreen(
    onNavigateBack: () -> Unit,
    onCoachSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCoachId by remember { mutableStateOf("alex") }
    
    val availableCoaches = listOf(
        CoachPersonality(
            id = "alex",
            name = "Alex",
            description = "High-energy motivational coach who pushes you to achieve your personal best. Great for building confidence and breaking through barriers.",
            personality = "Motivational & Energetic",
            avatarIcon = Icons.Default.SportsScore,
            color = Color(0xFF4CAF50)
        ),
        CoachPersonality(
            id = "sam",
            name = "Sam",
            description = "Calm and supportive coach focused on mindful running and sustainable progress. Perfect for stress relief and building healthy habits.",
            personality = "Supportive & Mindful",
            avatarIcon = Icons.Default.Favorite,
            color = Color(0xFF2196F3)
        ),
        CoachPersonality(
            id = "taylor",
            name = "Taylor",
            description = "Data-driven analytical coach who focuses on metrics, pacing strategies, and performance optimization for serious runners.",
            personality = "Analytical & Strategic",
            avatarIcon = Icons.Default.Analytics,
            color = Color(0xFF9C27B0)
        ),
        CoachPersonality(
            id = "riley",
            name = "Riley",
            description = "Balanced coach combining motivation with technical guidance. Adapts coaching style based on your mood and performance.",
            personality = "Adaptive & Balanced",
            avatarIcon = Icons.Default.Psychology,
            color = Color(0xFFFF9800)
        )
    )
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose Your Coach",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = RunIQSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.md)
        ) {
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.sm))
            }
            
            // Header description
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
                ) {
                    Text(
                        text = "Your AI Running Coach",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Choose a coaching personality that matches your running style and motivation preferences. You can change this anytime in settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.sm))
            }
            
            // Coach options
            items(availableCoaches) { coach ->
                CoachPersonalityCard(
                    coach = coach,
                    isSelected = coach.id == selectedCoachId,
                    onSelect = { selectedCoach ->
                        selectedCoachId = selectedCoach.id
                        // Auto-save selection and navigate back
                        // In real app, this would call a ViewModel method
                        onCoachSelected()
                    }
                )
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(RunIQSpacing.xl))
            }
        }
    }
}

@Preview(showBackground = true, name = "Coach Selection Screen")
@Composable
private fun CoachSelectionScreenPreview() {
    RunIQTheme {
        CoachSelectionScreen(
            onNavigateBack = { },
            onCoachSelected = { }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Coach Selection Dark")
@Composable
private fun CoachSelectionScreenDarkPreview() {
    RunIQTheme {
        CoachSelectionScreen(
            onNavigateBack = { },
            onCoachSelected = { }
        )
    }
}