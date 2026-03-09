package com.example.stormlight.data.model

import com.example.stormlight.data.model.supportdto.CloudsDto
import com.example.stormlight.data.model.supportdto.CoordDto
import com.example.stormlight.data.model.supportdto.MainDto
import com.example.stormlight.data.model.supportdto.SysDto
import com.example.stormlight.data.model.supportdto.WeatherDescDto
import com.example.stormlight.data.model.supportdto.WindDto
import com.google.gson.annotations.SerializedName

data class CurrentWeatherDto(
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("weather") val weather: List<WeatherDescDto>,
    @SerializedName("main") val main: MainDto,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("clouds") val clouds: CloudsDto,
    @SerializedName("dt") val dt: Long,
    @SerializedName("sys") val sys: SysDto,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("name") val name: String,
    var localNames: Map<String, String>? = null,
    // @SerializedName("cod") val cod: Int
)








