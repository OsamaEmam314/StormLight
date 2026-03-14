package com.example.stormlight.ui.screens.favorites.view
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Search and Autocomplete State
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<PlaceSuggestion>()) }
    var isSearching by remember { mutableStateOf(false) }

    // Map State
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }

    // osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Debounce logic for Autocomplete
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            isSearching = true
            delay(500) // Wait 500ms after the user stops typing before making the API call
            suggestions = fetchPlaceSuggestions(searchQuery)
            isSearching = false
        } else {
            suggestions = emptyList()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        // --- 1. Material 3 Search Bar with Autocomplete ---
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {
                // Optional: Handle exact enter key press if needed
                isSearchActive = false
                keyboardController?.hide()
            },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            placeholder = { Text("Search for places...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
                .padding(top = 8.dp)
        ) {
            // Dropdown list of suggestions
            LazyColumn {
                items(suggestions) { suggestion ->
                    ListItem(
                        headlineContent = { Text(suggestion.displayName) },
                        modifier = Modifier.clickable {
                            // When a user taps a suggestion:
                            searchQuery = suggestion.displayName
                            isSearchActive = false
                            keyboardController?.hide()

                            // Update the map point
                            selectedPoint = GeoPoint(suggestion.lat, suggestion.lon)
                        }
                    )
                }
            }
        }

        // --- 2. Map View ---
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
                            isSearchActive = false // Close search bar if open
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
                    marker.title = "Selected Location"

                    mapView.overlays.add(marker)
                    mapView.controller.animateTo(point)
                }
                mapView.invalidate()
            }
        )

        // --- 3. Selected Coordinates Display ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selected Location:",
                    style = MaterialTheme.typography.titleMedium
                )
                if (selectedPoint != null) {
                    Text("Latitude: ${selectedPoint!!.latitude}")
                    Text("Longitude: ${selectedPoint!!.longitude}")
                } else {
                    Text("Tap on the map or search to select a location.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}


data class PlaceSuggestion(
    val displayName: String,
    val lat: Double,
    val lon: Double
)

suspend fun fetchPlaceSuggestions(query: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
    if (query.isBlank()) return@withContext emptyList()

    val results = mutableListOf<PlaceSuggestion>()
    try {
        // Photon API by Komoot (Free, no API key, OSM data)
        val url = URL("https://photon.komoot.io/api/?q=${query.replace(" ", "+")}&limit=5")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 3000

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(response)
            val features = jsonObject.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")

                // GeoJSON format is [longitude, latitude]
                val lon = coordinates.getDouble(0)
                val lat = coordinates.getDouble(1)

                val properties = feature.getJSONObject("properties")
                val name = properties.optString("name", "")
                val city = properties.optString("city", "")
                val country = properties.optString("country", "")

                // Construct a readable display name
                val displayName = listOf(name, city, country)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                if (displayName.isNotBlank()) {
                    results.add(PlaceSuggestion(displayName, lat, lon))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace() // Handle network errors gracefully in production
    }
    return@withContext results
}