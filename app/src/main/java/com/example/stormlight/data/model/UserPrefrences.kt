package com.example.stormlight.data.model

import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.Location
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.WindSpeedUnit

data class UserPrefrences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.ENGLISH,
    val location: Location = Location.GPS,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METER_PER_SEC
)
