package com.example.stormlight.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.weatherDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "weather_cache")

class WeatherDataStore(private val context: Context) {
    companion object {
        val KEY_CURRENT_JSON = stringPreferencesKey("current_weather_json")
        val KEY_FORECAST_JSON = stringPreferencesKey("forecast_json")
    }

    suspend fun saveCurrentWeather(curr: String) {
        context.weatherDataStore.edit { it[KEY_CURRENT_JSON] = curr }
    }

    suspend fun saveForecast(forecast: String) {
        context.weatherDataStore.edit { it[KEY_FORECAST_JSON] = forecast }
    }

    val currentWeatherFlow: Flow<String?> =
        context.weatherDataStore.data.map { it[KEY_CURRENT_JSON] }
    val forecastFlow: Flow<String?> = context.weatherDataStore.data.map { it[KEY_FORECAST_JSON] }
    suspend fun clear() {
        context.weatherDataStore.edit { it.clear() }
    }

}