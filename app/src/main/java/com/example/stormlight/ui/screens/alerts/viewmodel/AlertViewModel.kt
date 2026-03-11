package com.example.stormlight.ui.screens.alerts.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stormlight.data.alerts.repository.AlertRepository
import com.example.stormlight.data.model.AlertEntity
import com.example.stormlight.ui.screens.alerts.view.AlertEvent
import com.example.stormlight.ui.screens.alerts.view.AlertUiState
import com.example.stormlight.ui.screens.alerts.view.CreateAlertDialogState
import com.example.stormlight.utilities.enums.AlertType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AlertViewModel(
    private val alertRepository: AlertRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(CreateAlertDialogState())
    val dialogState: StateFlow<CreateAlertDialogState> = _dialogState.asStateFlow()

    private val _events = MutableSharedFlow<AlertEvent>()
    val events: SharedFlow<AlertEvent> = _events.asSharedFlow()

    init {
        getAllAlerts()
    }

    private fun getAllAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            alertRepository.getAllAlerts().catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }.collect { alerts ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        alerts = alerts,
                        errorMessage = null
                    )
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
               // alertScheduler.scheduleAlert(insertedAlert)
                _events.emit(
                    AlertEvent.AlarmScheduled(
                        alertId = insertedAlert.id,
                        hour = insertedAlert.hour,
                        minute = insertedAlert.minute
                    )
                )
            }
            _events.emit(AlertEvent.ShowSnackbar("Alert set for ${formatTime(state.selectedHour, state.selectedMinute)}"))
            hideCreateDialog()
        }
    }
    fun toggleAlert(alert: AlertEntity) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = !alert.isEnabled)
            alertRepository.updateAlert(updated)
            if (updated.isEnabled) {
             //   alertScheduler.scheduleAlert(updated)
                _events.emit(AlertEvent.AlarmScheduled(updated.id, updated.hour, updated.minute))
            } else {
               // alertScheduler.cancelAlert(updated.id)
                _events.emit(AlertEvent.AlarmCancelled(updated.id))
            }
        }
    }
    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
          //  alertScheduler.cancelAlert(alert.id)
            alertRepository.deleteAlert(alert)
            _events.emit(AlertEvent.AlertDeleted)
            _events.emit(AlertEvent.ShowSnackbar("Alert deleted"))
        }
    }
    private fun formatTime(hour: Int, minute: Int): String {
        val h = if (hour % 12 == 0) 12 else hour % 12
        val m = minute.toString().padStart(2, '0')
        val amPm = if (hour < 12) "AM" else "PM"
        return "$h:$m $amPm"
    }
}