package com.example.stormlight.data.prefrences.repository

import android.content.Context
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.local.IPreferencesLocalDataSource
import com.example.stormlight.data.prefrences.local.PreferencesLocalDataSource
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

class PrefrencesRepository(private val localDataSource : IPreferencesLocalDataSource)  : IPrefrencesRepository {

    override val userPreferences: Flow<UserPrefrences> = localDataSource.userPreferences

    override suspend fun setLanguage(language: Language) = localDataSource.setLanguage(language)
    override suspend fun setThemeMode(themeMode: ThemeMode) = localDataSource.setThemeMode(themeMode)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) = localDataSource.setTemperatureUnit(unit)
    override suspend fun setWindSpeedUnit(unit: WindSpeedUnit) = localDataSource.setWindSpeedUnit(unit)
    override suspend fun setLocationSource(source: LocationSource) =
        localDataSource.setLocationSource(source)

    override suspend fun setLatitude(lat: String) = localDataSource.setLatitude(lat)
    override suspend fun setLongitude(lon: String) = localDataSource.setLongitude(lon)
}