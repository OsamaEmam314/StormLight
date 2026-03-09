package com.example.stormlight.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.home.view.HomeUiState
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: PrefrencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var latestWeather: CurrentWeatherDto? = null
    private var latestForecast: ForecastDto? = null

    init {
        loadWeather()
    }

    fun loadWeather(){
        viewModelScope.launch {
            val prefs = prefrencesRepository.userPreferences.firstOrNull() ?: UserPrefrences()
            val lat = prefs.lat.toDoubleOrNull() ?: run {
                _uiState.value = HomeUiState.Error("Location not set. Please configure in Settings.")
                return@launch
            }
            val lon = prefs.lon.toDoubleOrNull() ?: run {
                _uiState.value = HomeUiState.Error("Location not set. Please configure in Settings.")
                return@launch
            }
            val lang = prefs.language.language
            combine(
                weatherRepository.getCurrentWeather(lat, lon, lang),
                weatherRepository.getForecast(lat, lon, lang),
                prefrencesRepository.userPreferences
            ){
                    weatherResource, forecastResource, latestPrefs ->
                Triple(weatherResource, forecastResource, latestPrefs)
            }.collectLatest { (weatherResource, forecastResource, latestPrefs) ->
                if(weatherResource is Resource.Success){
                    latestWeather = weatherResource.data
                }
                if(forecastResource is Resource.Success){
                    latestForecast = forecastResource.data
                }
                val weather = latestWeather
                val forecast = latestForecast
                val prefs = latestPrefs
                _uiState.value = when{
                    weather != null && forecast != null -> HomeUiState.Success(
                        currentWeather = weather,
                        forecast = forecast,
                        userPrefrences = prefs
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
    fun setLocation(lat: Double, lon: Double){
        viewModelScope.launch {
            prefrencesRepository.setLatitude(lat.toString())
            prefrencesRepository.setLongitude(lon.toString())
            loadWeather()
        }
    }

    fun retry() = loadWeather()

}