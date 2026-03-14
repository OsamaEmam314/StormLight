package com.example.stormlight.ui.screens.map

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.stormlight.ui.screens.map.view.MapPickerScreen
import com.example.stormlight.ui.theme.StormLightTheme

class MapPickerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
        const val EXTRA_CITY_NAME = "extra_city_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val windowInsetsController = WindowCompat
            .getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            StormLightTheme {
                MapPickerScreen(
                    onLocationConfirmed = { lat, lon, cityName ->
                        val resultIntent = Intent().apply {
                            putExtra(EXTRA_LAT, lat)
                            putExtra(EXTRA_LON, lon)
                            putExtra(EXTRA_CITY_NAME, cityName)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}