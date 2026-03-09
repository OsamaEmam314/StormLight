package com.example.stormlight.data.model

import com.google.gson.annotations.SerializedName

data class GeoLocationDto(
    val name: String,
    @SerializedName("local_names")
    val localNames: Map<String, String>? = null,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
) {
    fun localizedName(langCode: String): String =
        localNames?.get(langCode) ?: name
}