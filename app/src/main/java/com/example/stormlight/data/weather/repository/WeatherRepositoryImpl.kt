package com.example.stormlight.data.weather.repository

import android.util.Log
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.GeoLocationDto
import com.example.stormlight.data.weather.local.LocalDataSource
import com.example.stormlight.data.weather.remote.RemoteDataSource
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository {

    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<CurrentWeatherDto>> = flow {

        emit(Resource.Loading)
        val cached = localDataSource.currentWeatherFlow.firstOrNull()
        if (cached != null) emit(Resource.Success(cached))
        try {
            val fresh = remoteDataSource.getCurrentWeather(lat, lon, lang)
            val cityName = try {
                remoteDataSource.reverseGeocode(lat, lon)?.name ?: fresh.name
            } catch (e: Exception) {
                fresh.name
            }
            try {
                val geoResults = remoteDataSource.searchCity(cityName)
                fresh.localNames = geoResults.firstOrNull()?.localNames
            } catch (e: Exception) {
            }
            localDataSource.saveCurrentWeather(fresh)
            emit(Resource.Success(fresh))

        } catch (e: Exception) {
            if (cached == null) emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<ForecastDto>> = flow {

        emit(Resource.Loading)

        val cached = localDataSource.forecastFlow.firstOrNull()
        if (cached != null) emit(Resource.Success(cached))

        try {
            val fresh = remoteDataSource.getForecast(lat, lon, lang)
            localDataSource.saveForecast(fresh)
            emit(Resource.Success(fresh))
        } catch (e: Exception) {
            if (cached == null) emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun searchCity(query: String): Resource<List<GeoLocationDto>> {
        return try {
            Resource.Success(remoteDataSource.searchCity(query))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "City search failed")
        }
    }

    override suspend fun reverseGeocode(lat: Double, lon: Double): Resource<GeoLocationDto> {
        return try {
            val result = remoteDataSource.reverseGeocode(lat, lon)
            if (result != null) Resource.Success(result)
            else Resource.Error("No location found for coordinates")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Reverse geocoding failed")
        }
    }
}