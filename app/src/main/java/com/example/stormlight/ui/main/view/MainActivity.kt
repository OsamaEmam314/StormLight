package com.example.stormlight.ui.main.view

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stormlight.ui.theme.StormLightTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.stormlight.R
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.ui.AppNavGraph
import com.example.stormlight.ui.main.viewmodel.MainViewModel
import com.example.stormlight.ui.main.viewmodel.MainViewModelFactory
import com.example.stormlight.ui.navigation.StormlightDestinations
import com.example.stormlight.utilities.LocaleUtils
import com.example.stormlight.utilities.LocationHelper
import com.example.stormlight.utilities.PermissionUtils
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        PrefrencesRepository(
            applicationContext
        )
    }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchAndSaveGpsLocation()
        else Log.w("MainActivity", "Location permission denied")
    }
    private fun requestLocationIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = repository.userPreferences.first()
            if (prefs.locationSource == LocationSource.GPS) {
                if (PermissionUtils.hasLocationPermission(applicationContext)) {
                    fetchAndSaveGpsLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
    fun fetchAndSaveGpsLocation() {
        if (!LocationHelper.isLocationEnabled(this)) {
            Log.w("MainActivity", "Location services disabled on device")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            LocationHelper.getCurrentLocation(applicationContext).collect { latLon->
                if (latLon != null) {
                    mainViewModel.setLatitude(latLon.lat.toString())
                    mainViewModel.setLongitude(latLon.lon.toString())
                    Log.d("MainActivity", "Location fetched: ${latLon.lat}, ${latLon.lon}")
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        requestLocationIfNeeded()

        setContent {
            val prefs by mainViewModel.userPrefs.collectAsStateWithLifecycle()
            LaunchedEffect(prefs.language) {
                val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                if (currentLocale != prefs.language.language) {
                    Log.d("pref from main", "Applying language: ${prefs.language.language}")
                    LocaleUtils.applyLocale(prefs.language, applicationContext)
                }
            }
            val darkMode = when(prefs.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            StormLightTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    StormLightApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StormLightApp(){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        topBar = {
            val  destTitle = currentDestination?.route ?: StormlightDestinations.Home.route
            if (destTitle == StormlightDestinations.Settings.route) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.nav_settings),
                        )
                    },

                )
            }
            if (destTitle == StormlightDestinations.Alerts.route) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.nav_alerts),
                        )
                    },

                    )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = Dp(0f),
            ){
                StormlightDestinations.all.forEach { destination ->
                    var selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.labelResId),
                            )
                        },
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(destination.labelResId),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        )

                    )
                }
            }
        },

    ) {
        innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

