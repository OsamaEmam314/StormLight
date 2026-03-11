package com.example.stormlight.data.alerts.local

import com.example.stormlight.data.model.AlertEntity
import kotlinx.coroutines.flow.Flow

interface AlertLocalDataSource {
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun updateAlert(alert: AlertEntity)
    suspend fun deleteAlert(alert: AlertEntity)
    suspend fun getAlertById(alertId: Int): AlertEntity?
    suspend fun getAlertByTime(hour: Int, minute: Int): AlertEntity?

}