package com.example.stormlight.ui.screens.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import androidx.lifecycle.ViewModelProvider
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository

class SettingsViewModelFactory(private val repository: IPrefrencesRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
