package com.example.stormlight.data.weather.local

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val currentWeatherFlow: Flow<CurrentWeatherDto?>
    val forecastFlow: Flow<ForecastDto?>
    suspend fun saveCurrentWeather(dto: CurrentWeatherDto)
    suspend fun saveForecast(dto: ForecastDto)
    suspend fun clearCache()
}