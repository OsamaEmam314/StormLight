package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherDescDto>,
    @SerializedName("clouds") val clouds: CloudsDto,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("pop") val pop: Double,
    @SerializedName("dt_txt") val dtTxt: String
)