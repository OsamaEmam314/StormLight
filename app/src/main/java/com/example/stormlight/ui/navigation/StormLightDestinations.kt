package com.example.stormlight.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.EditNotifications
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.EditNotifications
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class StormlightDestinations(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : StormlightDestinations(
        route = "home",
        labelResId = com.example.stormlight.R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    data object Favorites : StormlightDestinations(
        route = "favorites",
        labelResId = com.example.stormlight.R.string.nav_favorites,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
    )
    data object Alerts : StormlightDestinations(
        route = "alerts",
        labelResId = com.example.stormlight.R.string.nav_alerts,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
    )

    data object Settings : StormlightDestinations(
        route = "settings",
        labelResId = com.example.stormlight.R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    companion object {
        val all: List<StormlightDestinations> get() = listOf(Home, Favorites, Alerts, Settings)
    }
}