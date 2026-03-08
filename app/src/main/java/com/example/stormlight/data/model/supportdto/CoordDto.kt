package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class CoordDto(
    @SerializedName("lon") val lon: Double,
    @SerializedName("lat") val lat: Double
)