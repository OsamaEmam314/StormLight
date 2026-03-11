package com.example.stormlight.data.weather.local

import com.example.stormlight.data.datastore.WeatherDataStore
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherLocalDataSource(
    private val weatherDataStore: WeatherDataStore,
    private val gson: Gson = Gson()): LocalDataSource {
    override suspend fun saveCurrentWeather(dto: CurrentWeatherDto) {
        weatherDataStore.saveCurrentWeather(gson.toJson(dto))
    }

    override suspend fun saveForecast(dto: ForecastDto) {
        weatherDataStore.saveForecast(gson.toJson(dto))
    }
    override val currentWeatherFlow: Flow<CurrentWeatherDto?> = weatherDataStore.currentWeatherFlow.map {
        json ->
        if (json == null) return@map null
        try {
            gson.fromJson(json, CurrentWeatherDto::class.java)
        } catch (_: JsonSyntaxException) {
            null
        }
    }
    override val forecastFlow: Flow<ForecastDto?> = weatherDataStore.forecastFlow.map {
        json ->
        if (json == null) return@map null
        try {
            gson.fromJson(json, ForecastDto::class.java)
        } catch (_: JsonSyntaxException) {
            null
        }
    }

    override suspend fun clearCache() {
        weatherDataStore.clear()
    }
}