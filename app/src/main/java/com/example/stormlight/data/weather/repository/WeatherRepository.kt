package com.example.stormlight.data.weather.repository


import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<CurrentWeatherDto>>

    fun getForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<ForecastDto>>
}