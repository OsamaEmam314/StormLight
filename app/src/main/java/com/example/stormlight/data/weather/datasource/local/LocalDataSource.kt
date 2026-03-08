package com.example.stormlight.data.weather.datasource.local

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto

interface LocalDataSource {
    suspend fun saveCurrentWeather(dto: CurrentWeatherDto)
    suspend fun saveForecast(dto: ForecastDto)
    suspend fun clearCache()
}