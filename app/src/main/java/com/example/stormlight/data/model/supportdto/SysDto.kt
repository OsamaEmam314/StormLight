package com.example.stormlight.data.model.supportdto

import com.google.gson.annotations.SerializedName

data class SysDto(
    @SerializedName("country") val country: String?,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset")  val sunset: Long
)