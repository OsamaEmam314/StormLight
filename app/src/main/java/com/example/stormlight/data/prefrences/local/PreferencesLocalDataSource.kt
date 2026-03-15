package com.example.stormlight.data.prefrences.local

import com.example.stormlight.data.datastore.StormLightPreferencesDataStore
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

class PreferencesLocalDataSource(private val dataStore: StormLightPreferencesDataStore):
    IPreferencesLocalDataSource {

    override val userPreferences: Flow<UserPrefrences> = dataStore.userPrefrencesFlow
    override suspend fun setLanguage(language: Language) = dataStore.setLanguage(language)
    override suspend fun setThemeMode(themeMode: ThemeMode) = dataStore.setThemeMode(themeMode)
    override suspend fun setTemperatureUnit(unit: TemperatureUnit) = dataStore.setTemperatureUnit(unit)
    override suspend fun setWindSpeedUnit(unit: WindSpeedUnit) = dataStore.setWindSpeedUnit(unit)
    override suspend fun setLocationSource(source: LocationSource) = dataStore.setLocationSource(source)
    override suspend fun setLatitude(lat: String) = dataStore.setLatitude(lat)
    override suspend fun setLongitude(lon: String) = dataStore.setLongitude(lon)
}