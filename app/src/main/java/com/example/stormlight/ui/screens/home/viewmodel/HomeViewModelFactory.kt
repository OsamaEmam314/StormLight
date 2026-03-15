package com.example.stormlight.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository

class HomeViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(weatherRepository, prefrencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}