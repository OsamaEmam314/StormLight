package com.example.stormlight.data.model

import com.example.stormlight.data.model.supportdto.ForecastCityDto
import com.example.stormlight.data.model.supportdto.ForecastItemDto
import com.google.gson.annotations.SerializedName

data class ForecastDto(
    @SerializedName("cod") val cod: String,
    @SerializedName("cnt") val cnt: Int,
    @SerializedName("list") val list: List<ForecastItemDto>,
    @SerializedName("city") val city: ForecastCityDto
)