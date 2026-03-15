package com.example.stormlight.ui.screens.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.stormlight.data.model.GeoLocationDto
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.utilities.Resource
import kotlinx.coroutines.flow.Flow

class MapPickerViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository
) : ViewModel() {

    val prefs: Flow<UserPrefrences> = prefrencesRepository.userPreferences

    suspend fun searchCity(query: String): List<GeoLocationDto> {
        return when (val result = weatherRepository.searchCity(query)) {
            is Resource.Success -> result.data
            is Resource.Error -> emptyList()
            is Resource.Loading -> emptyList()
        }
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): GeoLocationDto? {
        return when (val result = weatherRepository.reverseGeocode(lat, lon)) {
            is Resource.Success -> result.data
            is Resource.Error -> null
            is Resource.Loading -> null
        }
    }
}