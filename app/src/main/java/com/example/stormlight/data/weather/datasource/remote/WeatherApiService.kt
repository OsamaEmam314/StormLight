package com.example.stormlight.data.weather.datasource.remote

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.utilities.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("units") units: String = Constants.API_UNITS,
        @Query("lang")  lang: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): CurrentWeatherDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("units") units: String = Constants.API_UNITS,
        @Query("lang")  lang: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): ForecastDto
}