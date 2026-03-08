package com.example.stormlight.utilities


import com.example.stormlight.BuildConfig

object Constants {

    const val API_KEY  = BuildConfig.OWM_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/"

    fun weatherIconUrl(iconCode: String): String =
        "https://openweathermap.org/img/wn/${iconCode}@2x.png"


    const val API_UNITS = "metric"

    const val UNIT_CELSIUS = "celsius"
    const val UNIT_FAHRENHEIT = "fahrenheit"
    const val UNIT_KELVIN = "kelvin"

    const val UNIT_MS = "ms"
    const val UNIT_MPH = "mph"

    const val LANG_EN = "en"
    const val LANG_AR = "ar"

    const val LOCATION_GPS = "gps"
    const val LOCATION_MAP = "map"
}