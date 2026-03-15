package com.example.stormlight.ui.screens.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stormlight.data.datastore.StormLightPreferencesDataStore
import com.example.stormlight.data.prefrences.local.PreferencesLocalDataSource
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.ui.main.viewmodel.MainViewModel
import com.example.stormlight.ui.main.viewmodel.MainViewModelFactory
import com.example.stormlight.ui.screens.details.view.FavoriteDetailScreen
import com.example.stormlight.ui.theme.StormLightTheme
import com.example.stormlight.utilities.enums.ThemeMode

class FavoriteDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_LOC = "extra_loc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val loc = intent.getStringExtra(EXTRA_LOC).orEmpty()

        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(PrefrencesRepository(
                    PreferencesLocalDataSource(
                        StormLightPreferencesDataStore(applicationContext)
                    )
                ))
            )
            val prefs by mainViewModel.userPrefs.collectAsStateWithLifecycle()

            val darkTheme = when (prefs.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            StormLightTheme(darkTheme = darkTheme) {
                Scaffold()
                { innerPadding ->
                    FavoriteDetailScreen(
                        loc = loc,
                        onBack = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}