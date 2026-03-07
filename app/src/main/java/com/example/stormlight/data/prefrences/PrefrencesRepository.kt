package com.example.stormlight.data.prefrences

import android.content.Context
import com.example.stormlight.data.datastore.StormLightPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import com.example.stormlight.utilities.enums.LocationSource

class PrefrencesRepository(private val context: Context) {
    private val localDataSource = StormLightPreferencesDataStore(context)
    val userPreferences: Flow<UserPrefrences> = localDataSource.userPrefrencesFlow

    suspend fun setLanguage(language: Language) = localDataSource.setLanguage(language)
    suspend fun setThemeMode(themeMode: ThemeMode) = localDataSource.setThemeMode(themeMode)
    suspend fun setTemperatureUnit(unit: TemperatureUnit) = localDataSource.setTemperatureUnit(unit)
    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) = localDataSource.setWindSpeedUnit(unit)
    suspend fun setLocationSource(source: LocationSource) = localDataSource.setLocationSource(source)
    suspend fun setLatitude(lat: String) = localDataSource.setLatitude(lat)
    suspend fun setLongitude(lon: String) = localDataSource.setLongitude(lon)
}