package com.runiq.presentation.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runiq.ui.theme.RunIQElevation
import com.runiq.ui.theme.RunIQSpacing
import com.runiq.ui.theme.RunIQTheme

/**
 * Data class representing a coach personality
 */
data class CoachPersonality(
    val id: String,
    val name: String,
    val description: String,
    val personality: String,
    val avatarIcon: ImageVector = Icons.Default.Person,
    val color: Color = Color.Gray
)

/**
 * Card component for selecting and displaying coach personalities
 * Used in coach selection screen and profile settings
 */
@Composable
fun CoachPersonalityCard(
    coach: CoachPersonality,
    isSelected: Boolean,
    onSelect: (CoachPersonality) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(coach) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) RunIQElevation.large else RunIQElevation.small
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RunIQSpacing.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(RunIQSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with selection indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Avatar background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(coach.color.copy(alpha = 0.2f))
                        .border(
                            2.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = coach.avatarIcon,
                        contentDescription = "${coach.name} avatar",
                        tint = coach.color,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Selection indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            
            // Coach information
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(RunIQSpacing.xs)
            ) {
                Text(
                    text = coach.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Text(
                    text = coach.personality,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = coach.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Unselected Coach")
@Composable
private fun CoachPersonalityCardPreview() {
    RunIQTheme {
        CoachPersonalityCard(
            coach = CoachPersonality(
                id = "1",
                name = "Alex",
                description = "Motivational and energetic coach who pushes you to achieve your best performance.",
                personality = "Motivational",
                color = Color(0xFF4CAF50)
            ),
            isSelected = false,
            onSelect = { }
        )
    }
}

@Preview(showBackground = true, name = "Selected Coach")
@Composable
private fun CoachPersonalityCardSelectedPreview() {
    RunIQTheme {
        CoachPersonalityCard(
            coach = CoachPersonality(
                id = "2",
                name = "Sam",
                description = "Calm and supportive coach who provides gentle encouragement and mindful guidance.",
                personality = "Supportive",
                color = Color(0xFF2196F3)
            ),
            isSelected = true,
            onSelect = { }
        )
    }
}

@Preview(showBackground = true, name = "Multiple Coaches")
@Composable
private fun CoachPersonalityCardsPreview() {
    RunIQTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(RunIQSpacing.sm)
        ) {
            CoachPersonalityCard(
                coach = CoachPersonality(
                    id = "1",
                    name = "Alex",
                    description = "Motivational and energetic",
                    personality = "Motivational",
                    color = Color(0xFF4CAF50)
                ),
                isSelected = true,
                onSelect = { }
            )
            
            CoachPersonalityCard(
                coach = CoachPersonality(
                    id = "2",
                    name = "Sam",
                    description = "Calm and supportive",
                    personality = "Supportive",
                    color = Color(0xFF2196F3)
                ),
                isSelected = false,
                onSelect = { }
            )
            
            CoachPersonalityCard(
                coach = CoachPersonality(
                    id = "3",
                    name = "Taylor",
                    description = "Data-focused and analytical",
                    personality = "Analytical",
                    color = Color(0xFF9C27B0)
                ),
                isSelected = false,
                onSelect = { }
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme")
@Composable
private fun CoachPersonalityCardDarkPreview() {
    RunIQTheme {
        CoachPersonalityCard(
            coach = CoachPersonality(
                id = "1",
                name = "Alex",
                description = "Motivational and energetic coach who pushes you to achieve your best performance.",
                personality = "Motivational",
                color = Color(0xFF4CAF50)
            ),
            isSelected = true,
            onSelect = { }
        )
    }
}