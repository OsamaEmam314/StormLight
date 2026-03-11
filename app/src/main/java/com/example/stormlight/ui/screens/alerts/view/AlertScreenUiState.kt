package com.example.stormlight.ui.screens.alerts.view

import com.example.stormlight.data.model.AlertEntity
import com.example.stormlight.utilities.enums.AlertType

data class AlertUiState(
    val alerts: List<AlertEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class CreateAlertDialogState(
    val isVisible: Boolean = false,
    val selectedHour: Int = 8,
    val selectedMinute: Int = 0,
    val selectedType: AlertType = AlertType.NOTIFICATION,
    val label: String = ""
)

sealed class AlertEvent {
    data class ShowSnackbar(val message: String) : AlertEvent()
    data class AlarmScheduled(val alertId: Int, val hour: Int, val minute: Int) : AlertEvent()
    data class AlarmCancelled(val alertId: Int) : AlertEvent()
    object AlertDeleted : AlertEvent()
}