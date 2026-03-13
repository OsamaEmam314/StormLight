package com.example.stormlight.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.WindSpeedUnit

import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "storm_light_preferences"
)

class StormLightPreferencesDataStore(private val context: Context) {
    companion object {
        val KEY_TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val KEY_WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_LOCATION_SOURCE = stringPreferencesKey("location")
        val KEY_LAT = stringPreferencesKey("lat")
        val KEY_LON = stringPreferencesKey("lon")
    }

    val userPrefrencesFlow = context.dataStore.data.map {
        UserPrefrences(
            language = Language.valueOf(it[KEY_LANGUAGE] ?: Language.ENGLISH.name),
            locationSource = LocationSource.valueOf(
                it[KEY_LOCATION_SOURCE] ?: LocationSource.GPS.name
            ),
            themeMode = ThemeMode.valueOf(it[KEY_THEME_MODE] ?: ThemeMode.DARK.name),
            windSpeedUnit = WindSpeedUnit.valueOf(
                it[KEY_WIND_SPEED_UNIT] ?: WindSpeedUnit.METER_PER_SEC.name
            ),
            temperatureUnit = TemperatureUnit.valueOf(
                it[KEY_TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name
            ),
            lat = it[KEY_LAT] ?: "0.0",
            lon = it[KEY_LON] ?: "0.0"
        )
    }

    suspend fun setLatitude(lat: String) {
        context.dataStore.edit { it[KEY_LAT] = lat }
    }

    suspend fun setLongitude(lon: String) {
        context.dataStore.edit { it[KEY_LON] = lon }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = themeMode.name }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[KEY_TEMPERATURE_UNIT] = unit.name }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { it[KEY_WIND_SPEED_UNIT] = unit.name }
    }

    suspend fun setLocationSource(source: LocationSource) {
        context.dataStore.edit { it[KEY_LOCATION_SOURCE] = source.name }
    }
}