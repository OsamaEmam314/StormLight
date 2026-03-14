package com.example.stormlight.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavWeather(
    @PrimaryKey(autoGenerate = false)
    val loc: String,
    val lat: Double,
    val lon: Double,
    val temp: Double,
    val currentWeather: CurrentWeatherDto,
    val forecast: ForecastDto
)
