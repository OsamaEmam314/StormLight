package com.example.stormlight.data.alerts.local

import com.example.stormlight.data.model.AlertEntity
import kotlinx.coroutines.flow.Flow

class AlertLocalDataSourceImpl(
    private val alertDao: AlertDao
) : AlertLocalDataSource {

    override fun getAllAlerts(): Flow<List<AlertEntity>> =
        alertDao.getAllAlerts()

    override suspend fun insertAlert(alert: AlertEntity) =
        alertDao.insertAlert(alert)

    override suspend fun updateAlert(alert: AlertEntity) =
        alertDao.updateAlert(alert)

    override suspend fun deleteAlert(alert: AlertEntity) =
        alertDao.deleteAlert(alert)

    override suspend fun getAlertById(alertId: Int): AlertEntity? =
        alertDao.getAlertById(alertId)
    override suspend fun getAlertByTime(hour: Int, minute: Int): AlertEntity? =
        alertDao.getAlertByTime(hour, minute)

}