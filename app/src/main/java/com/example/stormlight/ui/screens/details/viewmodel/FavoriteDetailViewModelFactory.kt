package com.example.stormlight.ui.screens.details.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository

class FavoriteDetailViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository,
    private val loc: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteDetailViewModel::class.java)) {
            return FavoriteDetailViewModel(weatherRepository, prefrencesRepository, loc) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}