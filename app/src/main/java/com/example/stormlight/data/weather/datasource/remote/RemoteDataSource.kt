package com.example.stormlight.data.weather.datasource.remote

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String): CurrentWeatherDto
    suspend fun getForecast(lat: Double, lon: Double, lang: String): ForecastDto
}