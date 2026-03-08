package com.example.stormlight.data.weather.repository

import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.weather.datasource.local.LocalDataSource
import com.example.stormlight.data.weather.datasource.local.WeatherLocalDataSource
import com.example.stormlight.data.weather.datasource.remote.RemoteDataSource
import com.example.stormlight.data.weather.datasource.remote.WeatherRemoteDataSource
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository{
    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<CurrentWeatherDto>> = flow {
        emit(Resource.Loading)
        val cachedCurrentWeather = localDataSource.currentWeatherFlow.firstOrNull()
        if (cachedCurrentWeather!= null){
            emit(Resource.Success(cachedCurrentWeather))
        }
        try {
            val remoteCurrentWeather = remoteDataSource.getCurrentWeather(lat, lon, lang)
            localDataSource.saveCurrentWeather(remoteCurrentWeather)
            emit(Resource.Success(remoteCurrentWeather))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }

    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<ForecastDto>> = flow{
        emit(Resource.Loading)
        val cachedForecast = localDataSource.forecastFlow.firstOrNull()
        if (cachedForecast!= null){
            emit(Resource.Success(cachedForecast))
        }
        try {
            val remoteForecast = remoteDataSource.getForecast(lat, lon, lang)
            localDataSource.saveForecast(remoteForecast)
            emit(Resource.Success(remoteForecast))
            } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

}