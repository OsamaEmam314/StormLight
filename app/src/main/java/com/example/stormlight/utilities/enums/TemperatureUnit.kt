package com.example.stormlight.utils.enums

enum class TemperatureUnit(val apiValue: String, val symbol: String) {
    CELSIUS("metric", "°C"),
    FAHRENHEIT("imperial", "°F"),
    KELVIN("standard", "K")
}