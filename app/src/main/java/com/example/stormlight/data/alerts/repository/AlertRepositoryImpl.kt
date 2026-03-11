package com.example.stormlight.data.alerts.repository

import com.example.stormlight.data.alerts.local.AlertLocalDataSource
import com.example.stormlight.data.model.AlertEntity
import kotlinx.coroutines.flow.Flow

class AlertRepositoryImpl(
    private val localDataSource: AlertLocalDataSource
) : AlertRepository {

    override fun getAllAlerts(): Flow<List<AlertEntity>> =
        localDataSource.getAllAlerts()

    override suspend fun addAlert(alert: AlertEntity) =
        localDataSource.insertAlert(alert)

    override suspend fun updateAlert(alert: AlertEntity) =
        localDataSource.updateAlert(alert)

    override suspend fun deleteAlert(alert: AlertEntity) =
        localDataSource.deleteAlert(alert)

    override suspend fun getAlertById(alertId: Int): AlertEntity? =
        localDataSource.getAlertById(alertId)

    override suspend fun getAlertByTime(hour: Int, minute: Int): AlertEntity? =
        localDataSource.getAlertByTime(hour, minute)

}