package com.example.stormlight.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stormlight.ui.navigation.StormlightDestinations
import com.example.stormlight.ui.screens.alerts.view.AlertsScreen
import com.example.stormlight.ui.screens.favorites.view.FavoritesScreen
import com.example.stormlight.ui.screens.home.view.HomeScreen
import com.example.stormlight.ui.screens.settings.view.SettingsScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = StormlightDestinations.Home.route,
        modifier = modifier,
    ) {
        composable(route = StormlightDestinations.Home.route) {
            HomeScreen()
        }
        composable(route = StormlightDestinations.Favorites.route) {
            FavoritesScreen()
        }
        composable(route = StormlightDestinations.Alerts.route) {
            AlertsScreen()
        }
        composable(route = StormlightDestinations.Settings.route) {
            SettingsScreen()
        }

    }
}