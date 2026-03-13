package com.example.stormlight.ui.screens.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.stormlight.data.prefrences.PrefrencesRepository
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory(private val repository: PrefrencesRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
