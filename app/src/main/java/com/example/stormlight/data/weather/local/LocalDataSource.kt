package com.example.stormlight.data.weather.local

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.ForecastDto
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    val currentWeatherFlow: Flow<CurrentWeatherDto?>
    val forecastFlow: Flow<ForecastDto?>
    suspend fun saveCurrentWeather(dto: CurrentWeatherDto)
    suspend fun saveForecast(dto: ForecastDto)
    suspend fun clearCache()

    fun getAllFavorites(): Flow<List<FavWeather>>
    suspend fun insertFavorite(favWeather: FavWeather)
    suspend fun updateFavorite(favWeather: FavWeather)
    suspend fun deleteFavorite(favWeather: FavWeather)
    suspend fun isFavorite(lat: Double, lon: Double): Boolean
    suspend fun getFavoriteByLoc(loc: String): FavWeather?

}