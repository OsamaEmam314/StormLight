package com.example.stormlight.utilities

import kotlin.math.roundToInt

object UnitUtils {

    fun convertTemp(celsius: Double, unit: String): Double = when (unit) {
        Constants.UNIT_FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
        Constants.UNIT_KELVIN -> celsius + 273.15
        else -> celsius
    }
    fun tempLabel(unit: String): String = when (unit) {
        Constants.UNIT_FAHRENHEIT -> "°F"
        Constants.UNIT_KELVIN -> "K"
        else -> "°C"
    }
    fun formatTemp(celsius: Double, unit: String): String =
        "${convertTemp(celsius, unit).roundToInt()}${tempLabel(unit)}"

    fun convertWind(ms: Double, unit: String): Double = when (unit) {
        Constants.UNIT_MPH -> ms * 2.23694
        else -> ms
    }

    fun windLabel(unit: String): String = when (unit) {
        Constants.UNIT_MPH -> "mph"
        else -> "m/s"
    }

    fun formatWind(ms: Double, unit: String): String {
        val value = convertWind(ms, unit)
        return "${(value * 10).roundToInt() / 10.0} ${windLabel(unit)}"
    }

    fun formatVisibility(metres: Int): String =
        if (metres >= 1000) "${metres / 1000} km" else "$metres m"
}