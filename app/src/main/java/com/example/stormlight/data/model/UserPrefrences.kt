package com.example.stormlight.data.model

import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.WindSpeedUnit

data class UserPrefrences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val language: Language = Language.ENGLISH,
    val locationSource: LocationSource = LocationSource.GPS,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METER_PER_SEC
)
