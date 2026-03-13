package com.example.stormlight.utilities


import com.example.stormlight.BuildConfig

object Constants {

    const val API_KEY = BuildConfig.OWM_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/"

    fun weatherIconUrl(iconCode: String): String =
        "https://openweathermap.org/img/wn/${iconCode}@2x.png"


    const val API_UNITS = "metric"
    const val UNIT_FAHRENHEIT_SYMBOL = "°F"
    const val UNIT_KELVIN_SYMBOL = "K"

    const val UNIT_MPH = "mph"

    const val SNOOZE_DURATION_MS = 10 * 60 * 1000L
    const val CHANNEL_ID_NOTIFICATION = "stormlight_weather_alerts_v5"
    const val CHANNEL_ID_ALARM = "stormlight_alarm_sound_v11"
}