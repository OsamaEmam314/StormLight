package com.example.stormlight.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.home.view.HomeUiState
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var latestWeather: CurrentWeatherDto? = null
    private var latestForecast: ForecastDto? = null
    private var weatherJob: Job? = null

    init {
        loadWeather()
    }


    fun refresh() {
        _isRefreshing.value = true
        loadWeather()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadWeather() {
        weatherJob?.cancel()
        if (latestWeather == null || latestForecast == null) {
            _uiState.value = HomeUiState.Loading
        }
        weatherJob = viewModelScope.launch {
            prefrencesRepository.userPreferences
                .flatMapLatest { prefs ->
                    val lat = prefs.lat.toDoubleOrNull()
                    val lon = prefs.lon.toDoubleOrNull()
                    val lang = prefs.language.language
                    if (lat == null || lon == null) {
                        flowOf(
                            Triple<Any, Any, UserPrefrences>(
                                Resource.Error("Location not set."),
                                Resource.Error(""),
                                prefs
                            )
                        )
                    } else {
                        combine(
                            weatherRepository.getCurrentWeather(lat, lon, lang),
                            weatherRepository.getForecast(lat, lon, lang)
                        ) { w, f -> Triple(w, f, prefs) }
                    }
                }
                .collect { (rawW, rawF, latestPrefs) ->
                    val wr = rawW as Resource<CurrentWeatherDto>
                    val fr = rawF as Resource<ForecastDto>

                    if (wr is Resource.Success) latestWeather = wr.data
                    if (fr is Resource.Success) latestForecast = fr.data

                    val newState = when {
                        latestWeather != null && latestForecast != null -> {
                            HomeUiState.Success(latestWeather!!, latestForecast!!, latestPrefs)
                        }

                        wr is Resource.Error -> HomeUiState.Error(wr.message)
                        fr is Resource.Error -> HomeUiState.Error(fr.message)
                        else -> HomeUiState.Loading
                    }
                    if (wr !is Resource.Loading && fr !is Resource.Loading) {
                        _isRefreshing.value = false
                    }

                    _uiState.value = newState
                }
        }
    }

    fun retry() = loadWeather()
}

