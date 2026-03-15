package com.example.stormlight.ui.screens.alerts.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stormlight.alarmmanager.StormLightAlarmScheduler
import com.example.stormlight.data.alerts.repository.AlertRepository
import com.example.stormlight.data.model.AlertEntity
import com.example.stormlight.data.model.AlertItem
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.alerts.view.AlertUiState
import com.example.stormlight.ui.screens.alerts.view.CreateAlertDialogState
import com.example.stormlight.utilities.Resource
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.AlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

class AlertViewModel(
    private val alertRepository: AlertRepository,
    private val alertScheduler: StormLightAlarmScheduler,
    private val weatherRepository: WeatherRepository,
    private val prefrencesRepository: IPrefrencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(CreateAlertDialogState())
    val dialogState: StateFlow<CreateAlertDialogState> = _dialogState.asStateFlow()

    init {
        loadAlerts()
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            alertRepository.getAllAlerts()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { alerts ->
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
        val now = LocalTime.now()
        _dialogState.update {
            CreateAlertDialogState(
                isVisible = true,
                selectedHour = now.hour,
                selectedMinute = now.minute,
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            val inserted = alertRepository.getAlertByTime(state.selectedHour, state.selectedMinute)
            if (inserted != null) {
                alertScheduler.scheduleAlert(
                    AlertItem(
                        id = inserted.id,
                        triggerAtMillis = resolveNextAlarmMillis(inserted.hour, inserted.minute),
                        type = inserted.type,
                        message = inserted.label,
                        hour = inserted.hour,
                        minute = inserted.minute
                    )
                )
            }
            hideCreateDialog()
        }
    }

    fun toggleAlert(alert: AlertEntity) {
        viewModelScope.launch {
            val updated = alert.copy(isEnabled = !alert.isEnabled)
            alertRepository.updateAlert(updated)
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            alertRepository.deleteAlert(alert)
        }
    }

    private fun resolveNextAlarmMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis < now.timeInMillis - 60_000L) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }

}