package com.example.stormlight.data.weather.datasource.remote

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.utilities.Constants

class WeatherRemoteDataSource(
    private val apiService: WeatherApiService
): RemoteDataSource {
   override suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String): CurrentWeatherDto{
        val response = apiService.getCurrentWeather(
            lat = lat,
            lon = lon,
            lang = lang,
            apiKey = Constants.API_KEY,
            units = Constants.API_UNITS
        )
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to fetch current weather: ${response.code()}, ${response.message()}")
        }
    }
    override suspend fun getForecast(lat: Double, lon: Double, lang: String): ForecastDto {
        val response = apiService.getForecast(
            lat = lat,
            lon = lon,
            lang = lang,
            apiKey = Constants.API_KEY,
            units = Constants.API_UNITS
        )
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to fetch forecast: ${response.code()}, ${response.message()}")
        }
    }
}