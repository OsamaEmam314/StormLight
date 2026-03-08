package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class WindDto(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg")   val deg: Int
)
