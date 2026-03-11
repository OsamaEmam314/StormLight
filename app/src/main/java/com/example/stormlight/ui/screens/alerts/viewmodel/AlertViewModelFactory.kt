package com.example.stormlight.ui.screens.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stormlight.data.alerts.repository.AlertRepository

class AlertViewModelFactory(
    private val alertRepository: AlertRepository,
  //  private val alertScheduler: AlertScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertViewModel(alertRepository/*, alertScheduler*/) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}