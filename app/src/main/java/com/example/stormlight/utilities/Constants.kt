package com.example.stormlight.utilities


import com.example.stormlight.BuildConfig

object Constants {

    const val API_KEY  = BuildConfig.OWM_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/"

    fun weatherIconUrl(iconCode: String): String =
        "https://openweathermap.org/img/wn/${iconCode}@2x.png"


    const val API_UNITS = "metric"

    const val UNIT_CELSIUS = "celsius"
    const val UNIT_CELSIUS_SYMBOL = "°C"
    const val UNIT_FAHRENHEIT = "fahrenheit"
    const val UNIT_FAHRENHEIT_SYMBOL = "°F"
    const val UNIT_KELVIN = "kelvin"
    const val UNIT_KELVIN_SYMBOL = "K"

    const val UNIT_MS = "ms"
    const val UNIT_MPH = "mph"

    const val LANG_EN = "en"
    const val LANG_AR = "ar"

    const val LOCATION_GPS = "gps"
    const val LOCATION_MAP = "map"

    // Notification Channels
    const val CHANNEL_ID_NOTIFICATION = "stormlight_notification_channel"
    const val CHANNEL_ID_ALARM = "stormlight_alarm_channel"

    // Intent / Worker Data Keys
    const val EXTRA_ALERT_ID = "extra_alert_id"
    const val EXTRA_ALERT_TYPE = "extra_alert_type"
    const val EXTRA_ALERT_LABEL = "extra_alert_label"

    // Notification Actions
    const val ACTION_DISMISS_ALARM = "com.example.stormlight.ACTION_DISMISS_ALARM"
    const val ACTION_SNOOZE_ALARM = "com.example.stormlight.ACTION_SNOOZE_ALARM"

    // Snooze Duration (10 minutes)
    const val SNOOZE_DURATION_MILLIS = 10 * 60 * 1000L
}