package com.example.stormlight.ui.screens.favorites.view

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.stormlight.R
import com.example.stormlight.data.datastore.WeatherDataStore
import com.example.stormlight.data.db.StormLightDatabase
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.network.RetrofitClient
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.local.WeatherLocalDataSource
import com.example.stormlight.data.weather.remote.WeatherRemoteDataSource
import com.example.stormlight.data.weather.repository.WeatherRepositoryImpl
import com.example.stormlight.ui.screens.details.FavoriteDetailActivity
import com.example.stormlight.ui.screens.favorites.viewmodel.FavViewModel
import com.example.stormlight.ui.screens.favorites.viewmodel.FavViewModelFactory
import com.example.stormlight.ui.screens.map.MapPickerActivity
import com.example.stormlight.utilities.Constants
import com.example.stormlight.utilities.UnitUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: FavViewModel = viewModel(
        factory = FavViewModelFactory(
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

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by rememberSaveable { mutableStateOf<String?>(null) }


    val mapLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lat = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LAT, 0.0) ?: 0.0
            val lon = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LON, 0.0) ?: 0.0
            val cityName = result.data
                ?.getStringExtra(MapPickerActivity.EXTRA_CITY_NAME)
                .orEmpty()
            viewModel.onLocationConfirmed(lat, lon, cityName)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FavoritesUiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)

                is FavoritesUiEvent.NavigateToMap -> {
                   mapLauncher.launch(Intent(context, MapPickerActivity::class.java))
                }

                is FavoritesUiEvent.NavigateToDetail -> {
                    val intent = Intent(context, FavoriteDetailActivity::class.java).apply {
                        putExtra(FavoriteDetailActivity.EXTRA_LOC, event.loc)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    val currentState = uiState
    if (pendingDelete != null && currentState is FavUiState.Success) {
        val locToDelete = pendingDelete
        val favToDelete = currentState.favorites.firstOrNull { it.loc == locToDelete }
        if (favToDelete != null) {
            ConfirmDeleteDialog(
                cityName = favToDelete.currentWeather.localNames?.get(currentState.prefs.language.language) ?: favToDelete.loc,
                onDismiss = { pendingDelete = null },
                onConfirm = {
                    viewModel.removeFavorite(favToDelete)
                    pendingDelete = null
                }
            )
        }
    }

    Box(modifier.fillMaxSize()) {
        when (val state = uiState) {
            is FavUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is FavUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            is FavUiState.Success -> {
                if (state.favorites.isEmpty()) {
                    Text(
                        text = stringResource(R.string.favorites_empty),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .padding(bottom = 80.dp)
                    ) {
                        items(
                            items = state.favorites,
                            key = { it.loc }
                        ) { fav ->
                            FavoriteItem(
                                favWeather = fav,
                                prefs = state.prefs,
                                onClick = { viewModel.onFavoriteClicked(fav) },
                                onDelete = { pendingDelete = fav.loc }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = { viewModel.onAddFavoriteClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.favorites_add)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
private fun ConfirmDeleteDialog(
    cityName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_fav_title)) },
        text = { Text(stringResource(R.string.delete_fav_message, cityName)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.action_remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteItem(
    favWeather: FavWeather,
    prefs: UserPrefrences,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.favorites_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        FavoriteCard(
            favWeather = favWeather,
            prefs = prefs,
            onClick = onClick
        )
    }
}
@Composable
private fun FavoriteCard(
    favWeather: FavWeather,
    prefs: UserPrefrences,
    onClick: () -> Unit,
) {
    val localTime = remember(favWeather.currentWeather.timezone) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val offsetMillis = favWeather.currentWeather.timezone * 1000L
        sdf.format(Date(System.currentTimeMillis() + offsetMillis))
    }
    val tempDisplay = UnitUtils.formatTemp(
        celsius = favWeather.currentWeather.main.temp,
        unit = prefs.temperatureUnit.symbol
    )

    val iconCode = favWeather.currentWeather.weather.firstOrNull()?.icon.orEmpty()

    val langCode = prefs.language.language
    val cityName = favWeather.currentWeather.localNames?.get(langCode)
        ?: favWeather.currentWeather.name.ifBlank { favWeather.loc }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = localTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (iconCode.isNotBlank()) {
                    AsyncImage(
                        model = Constants.weatherIconUrl(iconCode),
                        contentDescription = favWeather.currentWeather.weather
                            .firstOrNull()?.description,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = tempDisplay,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}