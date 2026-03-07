package com.example.stormlight.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.PrefrencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    repository: PrefrencesRepository
) : ViewModel() {
    val userPrefs: StateFlow<UserPrefrences> = repository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPrefrences()
        )
}