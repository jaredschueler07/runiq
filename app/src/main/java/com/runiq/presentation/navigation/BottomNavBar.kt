package com.runiq.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.runiq.R
import com.runiq.ui.theme.RunIQTheme

/**
 * Data class representing a bottom navigation item
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String? = null
)

/**
 * Bottom navigation bar for main app navigation
 */
@Composable
fun RunIQBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val items = listOf(
        BottomNavItem(
            route = NavRoutes.Home.route,
            title = "Home", // TODO: Replace with string resource
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            contentDescription = "Home screen"
        ),
        BottomNavItem(
            route = NavRoutes.History.route,
            title = "History", // TODO: Replace with string resource
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History,
            contentDescription = "Run history"
        ),
        BottomNavItem(
            route = NavRoutes.Profile.route,
            title = "Profile", // TODO: Replace with string resource
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            contentDescription = "Profile and settings"
        )
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.contentDescription ?: item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RunIQBottomNavBarPreview() {
    RunIQTheme {
        RunIQBottomNavBar(
            currentRoute = NavRoutes.Home.route,
            onNavigate = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RunIQBottomNavBarHistorySelectedPreview() {
    RunIQTheme {
        RunIQBottomNavBar(
            currentRoute = NavRoutes.History.route,
            onNavigate = { }
        )
    }
}