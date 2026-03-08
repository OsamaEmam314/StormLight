package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class WeatherDescDto(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)