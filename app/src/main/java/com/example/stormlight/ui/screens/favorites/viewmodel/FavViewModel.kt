package com.example.stormlight.ui.screens.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.favorites.view.FavUiState
import com.example.stormlight.ui.screens.favorites.view.FavoritesUiEvent
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavUiState>(FavUiState.Loading)
    val uiState: StateFlow<FavUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FavoritesUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent: SharedFlow<FavoritesUiEvent> = _uiEvent.asSharedFlow()

    init {
        combine(
            weatherRepository.getAllFavorites(),
            prefrencesRepository.userPreferences
        ) { favorites, prefs ->
            FavUiState.Success(favorites = favorites, prefs = prefs)
        }
            .onEach { state -> _uiState.value = state }
            .launchIn(viewModelScope)
    }

    fun onAddFavoriteClicked() {
        viewModelScope.launch {
            _uiEvent.emit(FavoritesUiEvent.NavigateToMap)
        }
    }

    fun onFavoriteClicked(favWeather: FavWeather) {
        viewModelScope.launch {
            _uiEvent.emit(FavoritesUiEvent.NavigateToDetail(loc = favWeather.loc))
        }
    }

    fun removeFavorite(favWeather: FavWeather) {
        viewModelScope.launch {
            weatherRepository.removeFavorite(favWeather)
            _uiEvent.emit(FavoritesUiEvent.ShowSnackbar("${favWeather.loc} removed"))
        }
    }

    fun onLocationConfirmed(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            if (weatherRepository.isFavorite(lat, lon)) {
                _uiEvent.emit(
                    FavoritesUiEvent.ShowSnackbar("$cityName is already in favorites")
                )
                return@launch
            }

            weatherRepository
                .getFavoriteWeather(
                    lat,
                    lon,
                    cityName,
                    prefrencesRepository.userPreferences.first().language.language
                )
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> Unit
                        is Resource.Success ->
                            _uiEvent.emit(
                                FavoritesUiEvent.ShowSnackbar("$cityName added to favorites")
                            )

                        is Resource.Error ->
                            _uiEvent.emit(
                                FavoritesUiEvent.ShowSnackbar(
                                    resource.message ?: "Could not fetch weather for $cityName"
                                )
                            )
                    }
                }
        }
    }
}