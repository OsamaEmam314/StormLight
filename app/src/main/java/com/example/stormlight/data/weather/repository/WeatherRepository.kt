package com.example.stormlight.data.weather.repository


import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.GeoLocationDto
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

    suspend fun searchCity(query: String): Resource<List<GeoLocationDto>>
    suspend fun reverseGeocode(lat: Double, lon: Double): Resource<GeoLocationDto>

    fun getFavoriteWeather(
        lat: Double,
        lon: Double,
        loc: String,
        lang: String
    ): Flow<Resource<FavWeather>>

    fun getAllFavorites(): Flow<List<FavWeather>>
    suspend fun addFavorite(favWeather: FavWeather)
    suspend fun removeFavorite(favWeather: FavWeather)
    suspend fun isFavorite(lat: Double, lon: Double): Boolean
    suspend fun getFavoriteByLoc(loc: String): FavWeather?
}