package com.example.stormlight.ui.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.home.view.HomeUiState
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: PrefrencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var latestWeather: CurrentWeatherDto? = null
    private var latestForecast: ForecastDto? = null

    private var weatherJob: Job? = null

    init {
        loadWeather()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadWeather() {
        weatherJob?.cancel()
        _uiState.value = HomeUiState.Loading

        weatherJob = viewModelScope.launch {
            prefrencesRepository.userPreferences
                .flatMapLatest { prefs ->
                    val lat = prefs.lat.toDoubleOrNull()
                    val lon = prefs.lon.toDoubleOrNull()
                    val lang = prefs.language.language

                    if (lat == null || lon == null) {
                        flowOf(HomeUiState.Error("Location not set. Please configure in Settings."))
                    } else {
                        latestWeather = null
                        latestForecast = null
                        combine(
                            weatherRepository.getCurrentWeather(lat, lon, lang),
                            weatherRepository.getForecast(lat, lon, lang)
                        ) { weatherResource, forecastResource ->
                            Triple(weatherResource, forecastResource, prefs)
                        }
                    }
                }
                .collectLatest { result ->
                    if (result is HomeUiState) {
                        _uiState.value = result
                        return@collectLatest
                    }

                    val (weatherResource, forecastResource, latestPrefs) =
                        result as Triple<Resource<CurrentWeatherDto>, Resource<ForecastDto>, com.example.stormlight.data.model.UserPrefrences>

                    if (weatherResource is Resource.Success) latestWeather = weatherResource.data
                    if (forecastResource is Resource.Success) latestForecast = forecastResource.data

                    val weather = latestWeather
                    val forecast = latestForecast

                    _uiState.value = when {
                        weather != null && forecast != null -> HomeUiState.Success(
                            currentWeather = weather,
                            forecast = forecast,
                            userPrefrences = latestPrefs
                        )
                        weatherResource is Resource.Error || forecastResource is Resource.Error ->
                            HomeUiState.Error(
                                (weatherResource as? Resource.Error)?.message
                                    ?: (forecastResource as? Resource.Error)?.message
                                    ?: "Unknown error"
                            )
                        else -> HomeUiState.Loading
                    }
                }
        }
    }

    fun setLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            prefrencesRepository.setLatitude(lat.toString())
            prefrencesRepository.setLongitude(lon.toString())
        }
    }

    fun retry() = loadWeather()
}