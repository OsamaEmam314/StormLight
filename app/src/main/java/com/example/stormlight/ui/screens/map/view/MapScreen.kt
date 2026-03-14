package com.example.stormlight.ui.screens.map.view

import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stormlight.R
import com.example.stormlight.data.datastore.WeatherDataStore
import com.example.stormlight.data.db.StormLightDatabase
import com.example.stormlight.data.model.GeoLocationDto
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.network.RetrofitClient
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.local.WeatherLocalDataSource
import com.example.stormlight.data.weather.remote.WeatherRemoteDataSource
import com.example.stormlight.data.weather.repository.WeatherRepositoryImpl
import com.example.stormlight.ui.screens.map.viewmodel.MapPickerViewModel
import com.example.stormlight.ui.screens.map.viewmodel.MapPickerViewModelFactory
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    onLocationConfirmed: (lat: Double, lon: Double, cityName: String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val viewModel: MapPickerViewModel = viewModel(
        factory = MapPickerViewModelFactory(
            weatherRepository = WeatherRepositoryImpl(
                WeatherRemoteDataSource(RetrofitClient.weatherApiService),
                WeatherLocalDataSource(
                    WeatherDataStore(context),
                    StormLightDatabase.getInstance(context).favoriteDao()
                )
            ),
            prefrencesRepository = PrefrencesRepository(context)
        )
    )
    val prefs by viewModel.prefs.collectAsState(initial = UserPrefrences())
    val langCode = prefs.language.language

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<GeoLocationDto>()) }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedCityName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = context.packageName
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            isSearching = true
            delay(500)
            suggestions = viewModel.searchCity(searchQuery)
            isSearching = false
        } else {
            suggestions = emptyList()
            isSearching = false
        }
    }

    LaunchedEffect(selectedPoint) {
        val point = selectedPoint ?: return@LaunchedEffect
        if (selectedCityName.isBlank()) {
            val result = viewModel.reverseGeocode(point.latitude, point.longitude)
            selectedCityName = result?.localNames?.get(langCode)
                ?: result?.name
                    .orEmpty()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopAppBar(
            title = { Text(stringResource(R.string.favorites_add)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_cancel)
                    )
                }
            }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                isSearchActive = false
                keyboardController?.hide()
            },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            placeholder = { Text(stringResource(R.string.favorites_search_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
        ) {
            LazyColumn {
                items(suggestions) { suggestion ->
                    val displayName = suggestion.localNames?.get(langCode)
                        ?: suggestion.name

                    ListItem(
                        headlineContent = {
                            Text("$displayName, ${suggestion.country}")
                        },
                        modifier = Modifier.clickable {
                            searchQuery = "$displayName, ${suggestion.country}"
                            isSearchActive = false
                            keyboardController?.hide()
                            selectedPoint = GeoPoint(suggestion.lat, suggestion.lon)
                            selectedCityName = displayName
                        }
                    )
                }
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)
                    controller.setCenter(GeoPoint(30.0333, 31.4833))

                    val tapReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            selectedPoint = p
                            selectedCityName = ""
                            isSearchActive = false
                            keyboardController?.hide()
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(tapReceiver))
                }
            },
            update = { mapView ->
                mapView.overlays.removeAll { it is Marker }
                selectedPoint?.let { point ->
                    val marker = Marker(mapView)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                    mapView.controller.animateTo(point)
                }
                mapView.invalidate()
            }
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (selectedCityName.isNotBlank()) "📍 $selectedCityName"
                    else stringResource(R.string.favorites_search_hint),
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        val point = selectedPoint ?: return@Button
                        onLocationConfirmed(
                            point.latitude,
                            point.longitude,
                            selectedCityName.ifBlank {
                                "${point.latitude}, ${point.longitude}"
                            }
                        )
                    },
                    enabled = selectedPoint != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.favorites_add))
                }
            }
        }
    }
}