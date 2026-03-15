package com.example.stormlight.ui.screens.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.details.view.FavDetailUiState
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class FavoriteDetailViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository,
    private val loc: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavDetailUiState>(FavDetailUiState.Loading)
    val uiState: StateFlow<FavDetailUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var latestWeather: CurrentWeatherDto? = null
    private var latestForecast: ForecastDto? = null
    private var detailJob: Job? = null

    init {
        loadDetail()
    }

    fun refresh() {
        _isRefreshing.value = true
        loadDetail()
    }

    fun retry() = loadDetail()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadDetail() {
        detailJob?.cancel()

        if (latestWeather == null || latestForecast == null) {
            _uiState.value = FavDetailUiState.Loading
        }

        detailJob = viewModelScope.launch {
            val cached = weatherRepository.getFavoriteByLoc(loc)
            if (cached == null) {
                _isRefreshing.value = false
                _uiState.value = FavDetailUiState.Error("Favorite not found")
                return@launch
            }

            val lat = cached.lat
            val lon = cached.lon

            prefrencesRepository.userPreferences
                .flatMapLatest { prefs ->
                    val lang = prefs.language.language
                    combine(
                        weatherRepository.getCurrentWeather(lat, lon, lang),
                        weatherRepository.getForecast(lat, lon, lang)
                    ) { w, f -> Triple(w, f, prefs) }
                }
                .collect { (rawW, rawF, latestPrefs) ->
                    val wr = rawW as Resource<CurrentWeatherDto>
                    val fr = rawF as Resource<ForecastDto>

                    if (wr is Resource.Success) latestWeather = wr.data
                    if (fr is Resource.Success) latestForecast = fr.data

                    val newState: FavDetailUiState = when {
                        latestWeather != null && latestForecast != null ->
                            FavDetailUiState.Success(
                                currentWeather = latestWeather!!,
                                forecast = latestForecast!!,
                                prefs = latestPrefs
                            )

                        wr is Resource.Error -> FavDetailUiState.Error(wr.message)
                        fr is Resource.Error -> FavDetailUiState.Error(fr.message)
                        else -> FavDetailUiState.Loading
                    }
                    if (wr !is Resource.Loading && fr !is Resource.Loading) {
                        _isRefreshing.value = false
                    }

                    _uiState.value = newState
                }
        }
    }
}