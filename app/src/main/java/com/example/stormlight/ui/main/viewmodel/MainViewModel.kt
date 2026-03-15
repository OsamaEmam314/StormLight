package com.example.stormlight.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.utilities.enums.Language
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: IPrefrencesRepository
) : ViewModel() {
    val userPrefs: StateFlow<UserPrefrences> = repository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPrefrences()
        )

    fun setLatitude(lat: String) {
        viewModelScope.launch {
            repository.setLatitude(lat)
        }
    }

    fun setLongitude(lon: String) {
        viewModelScope.launch {
            repository.setLongitude(lon)
        }
    }
}