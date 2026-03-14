package com.example.stormlight.ui.screens.details.view


import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.UserPrefrences

sealed class FavDetailUiState {
    data object Loading : FavDetailUiState()
    data class Error(val message: String) : FavDetailUiState()
    data class Success(
        val currentWeather: CurrentWeatherDto,
        val forecast: ForecastDto,
        val prefs: UserPrefrences
    ) : FavDetailUiState()
}