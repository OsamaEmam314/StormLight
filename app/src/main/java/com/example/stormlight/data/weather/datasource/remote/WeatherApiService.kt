package com.example.stormlight.data.weather.datasource.remote

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.GeoLocationDto
import com.example.stormlight.utilities.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("units") units: String,
        @Query("lang")  lang: String,
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherDto>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("units") units: String,
        @Query("lang")  lang: String,
        @Query("appid") apiKey: String
    ): Response<ForecastDto>
    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q")     query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<GeoLocationDto>>

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): Response<List<GeoLocationDto>>
}