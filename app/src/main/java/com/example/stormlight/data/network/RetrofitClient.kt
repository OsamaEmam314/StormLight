package com.example.stormlight.data.network

import com.example.stormlight.data.weather.datasource.remote.WeatherApiService
import com.example.stormlight.utilities.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

    }
}