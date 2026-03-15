package com.example.stormlight.ui.screens.home.view

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.UserPrefrences

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val currentWeather: CurrentWeatherDto,
        val forecast: ForecastDto,
        val userPrefrences: UserPrefrences
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}