package com.example.stormlight.data.prefrences.repository

import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface IPrefrencesRepository {
    val userPreferences: Flow<UserPrefrences>
    suspend fun setLanguage(language: Language)
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)
    suspend fun setLocationSource(source: LocationSource)
    suspend fun setLatitude(lat: String)
    suspend fun setLongitude(lon: String)
}