package com.example.stormlight.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.ForecastDto
import com.google.gson.Gson

@ProvidedTypeConverter
class WeatherTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeatherDto(dto: CurrentWeatherDto): String =
        gson.toJson(dto)

    @TypeConverter
    fun toCurrentWeatherDto(json: String): CurrentWeatherDto =
        gson.fromJson(json, CurrentWeatherDto::class.java)

    @TypeConverter
    fun fromForecastDto(dto: ForecastDto): String =
        gson.toJson(dto)

    @TypeConverter
    fun toForecastDto(json: String): ForecastDto =
        gson.fromJson(json, ForecastDto::class.java)
}