package com.example.stormlight.ui.screens.alerts.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.alerts.repository.AlertRepository
import com.example.stormlight.data.model.AlertEntity
import com.example.stormlight.ui.screens.alerts.view.AlertUiState
import com.example.stormlight.ui.screens.alerts.view.CreateAlertDialogState
import com.example.stormlight.utilities.enums.AlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AlertViewModel(
    private val alertRepository: AlertRepository,
    //private val alertScheduler: AlertScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(CreateAlertDialogState())
    val dialogState: StateFlow<CreateAlertDialogState> = _dialogState.asStateFlow()

    init {
        getAllAlerts()
    }

    private fun getAllAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            alertRepository.getAllAlerts()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message)
                    }
                }
                .collect { alerts ->
                    _uiState.update {
                        it.copy(isLoading = false, alerts = alerts, errorMessage = null)
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showCreateDialog() {
        val currentTime = LocalTime.now()
        _dialogState.update {
            CreateAlertDialogState(
                isVisible = true,
                selectedHour = currentTime.hour,
                selectedMinute = currentTime.minute,
                selectedType = AlertType.NOTIFICATION,
                label = ""
            )
        }
    }

    fun hideCreateDialog() {
        _dialogState.update { it.copy(isVisible = false) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        _dialogState.update { it.copy(selectedHour = hour, selectedMinute = minute) }
    }

    fun onAlertTypeSelected(type: AlertType) {
        _dialogState.update { it.copy(selectedType = type) }
    }

    fun onLabelChanged(label: String) {
        _dialogState.update { it.copy(label = label) }
    }

    fun createAlert() {
        val state = _dialogState.value
        viewModelScope.launch {
            val alert = AlertEntity(
                hour = state.selectedHour,
                minute = state.selectedMinute,
                type = state.selectedType,
                isEnabled = true,
                label = state.label.trim()
            )
            alertRepository.addAlert(alert)
            val insertedAlert = alertRepository.getAlertByTime(state.selectedHour, state.selectedMinute)
            if (insertedAlert != null) {
              //  alertScheduler.scheduleAlert(insertedAlert)
            }
            hideCreateDialog()
        }
    }

    fun toggleAlert(alert: AlertEntity) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = !alert.isEnabled)
            alertRepository.updateAlert(updated)
            if (updated.isEnabled) {
             //   alertScheduler.scheduleAlert(updated)
            } else {
               // alertScheduler.cancelAlert(updated.id)
            }
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            //alertScheduler.cancelAlert(alert.id)
            alertRepository.deleteAlert(alert)
        }
    }
}

