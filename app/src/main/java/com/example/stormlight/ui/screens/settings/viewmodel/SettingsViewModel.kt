package com.example.stormlight.ui.screens.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit
import com.example.stormlight.utilities.enums.LocationSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val repository: PrefrencesRepository
) : ViewModel() {
    val userPrefrencesState: StateFlow<UserPrefrences> = repository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = Lazily,
            initialValue = UserPrefrences()
        )

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }
    fun setLocationSource(LocationSource: LocationSource) {
        viewModelScope.launch {
            repository.setLocationSource(LocationSource)
        }
    }
    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(themeMode) }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { repository.setTemperatureUnit(unit) }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch { repository.setWindSpeedUnit(unit) }
    }
}