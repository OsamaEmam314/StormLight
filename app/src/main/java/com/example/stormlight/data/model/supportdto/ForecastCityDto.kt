package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class ForecastCityDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("country") val country: String,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
)