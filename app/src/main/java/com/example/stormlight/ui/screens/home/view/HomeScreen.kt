package com.example.stormlight.ui.screens.home.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stormlight.R
import com.example.stormlight.data.datastore.WeatherDataStore
import com.example.stormlight.data.db.StormLightDatabase
import com.example.stormlight.data.network.RetrofitClient
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.local.WeatherLocalDataSource
import com.example.stormlight.data.weather.remote.WeatherRemoteDataSource
import com.example.stormlight.data.weather.repository.WeatherRepositoryImpl
import com.example.stormlight.ui.components.CurrentWeatherHeader
import com.example.stormlight.ui.components.CurrentWeatherHero
import com.example.stormlight.ui.components.MetricsGrid
import com.example.stormlight.ui.components.DailyForecast
import com.example.stormlight.ui.screens.home.components.forecast.HourlyForecast
import com.example.stormlight.ui.screens.home.viewmodel.HomeViewModel
import com.example.stormlight.ui.screens.home.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            weatherRepository = WeatherRepositoryImpl(
                WeatherRemoteDataSource(
                    RetrofitClient.weatherApiService
                ),
                WeatherLocalDataSource(
                    WeatherDataStore(context),
                    StormLightDatabase.getInstance(context).favoriteDao()

                )
            ),
            prefrencesRepository = PrefrencesRepository(context)
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = modifier.fillMaxSize(),
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = pullRefreshState,
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> HomeLoadingState()
            is HomeUiState.Error -> HomeErrorState(
                message = state.message,
                onRetry = { viewModel.retry() }
            )

            is HomeUiState.Success -> HomeSuccessState(state = state)
        }
    }
}

@Composable
fun HomeSuccessState(state: HomeUiState.Success) {
    val current = state.currentWeather
    val forecast = state.forecast
    val prefs = state.userPrefrences
    val timezoneOffset = current.timezone
    val forecastHourlyList = forecast.list.take(8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CurrentWeatherHeader(
            cityName = current.localNames?.get(prefs.language.language) ?: current.name,
            country = current.sys.country,
            dt = current.dt,
            timezoneOffset = timezoneOffset,
            language = prefs.language
        )

        CurrentWeatherHero(
            temperature = current.main.temp,
            tempMin = current.main.tempMin,
            tempMax = current.main.tempMax,
            description = current.weather.firstOrNull()?.description.orEmpty(),
            iconCode = current.weather.firstOrNull()?.icon.orEmpty(),
            temperatureUnit = prefs.temperatureUnit,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        MetricsGrid(
            current = current,
            temperatureUnit = prefs.temperatureUnit,
            windSpeedUnit = prefs.windSpeedUnit,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        HourlyForecast(
            forecastItems = forecastHourlyList,
            timezoneOffset = timezoneOffset,
            temperatureUnit = prefs.temperatureUnit,
            modifier = Modifier.padding(horizontal = 24.dp),
            language = prefs.language
        )

        DailyForecast(
            forecastItems = forecast.list,
            timezoneOffset = timezoneOffset,
            temperatureUnit = prefs.temperatureUnit,
            modifier = Modifier.padding(horizontal = 24.dp),
            language = prefs.language
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HomeErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Log.d("HomeScreen", "HomeErrorState: $message")
            Text(
                text = stringResource(R.string.error_generic),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            IconButton(
                onClick = onRetry,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Retry",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxSize(0.6f)
                )
            }
        }
    }
}

@Composable
private fun HomeLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}