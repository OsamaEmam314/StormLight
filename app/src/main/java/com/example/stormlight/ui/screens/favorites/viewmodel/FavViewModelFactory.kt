package com.example.stormlight.ui.screens.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository

class FavViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: PrefrencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavViewModel::class.java)) {
            return FavViewModel(weatherRepository, prefrencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}