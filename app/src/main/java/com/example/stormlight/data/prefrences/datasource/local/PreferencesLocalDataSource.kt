package com.example.stormlight.data.prefrences.datasource.local

import android.content.Context
import com.example.stormlight.data.datastore.StormLightPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import com.example.stormlight.utilities.enums.LocationSource


class PreferencesLocalDataSource(private val context: Context) {
    private val dataStore: StormLightPreferencesDataStore = StormLightPreferencesDataStore(context)
    val userPreferences: Flow<UserPrefrences> = dataStore.userPrefrencesFlow
    suspend fun setLanguage(language: Language)             = dataStore.setLanguage(language)
    suspend fun setThemeMode(themeMode: ThemeMode)          = dataStore.setThemeMode(themeMode)
    suspend fun setTemperatureUnit(unit: TemperatureUnit)   = dataStore.setTemperatureUnit(unit)
    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)       = dataStore.setWindSpeedUnit(unit)
    suspend fun setLocationSource(source: LocationSource)   = dataStore.setLocationSource(source)

}