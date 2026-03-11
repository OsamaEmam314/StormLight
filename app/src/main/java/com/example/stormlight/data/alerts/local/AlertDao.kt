package com.example.stormlight.data.alerts.local

import androidx.room.*
import com.example.stormlight.data.model.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts ORDER BY isEnabled DESC, hour ASC, minute ASC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)
    @Query("SELECT * FROM alerts WHERE id = :alertId")
    suspend fun getAlertById(alertId: Int): AlertEntity?

    @Query("SELECT * FROM alerts WHERE hour = :hour AND minute = :minute ORDER BY id DESC LIMIT 1")
    suspend fun getAlertByTime(hour: Int, minute: Int): AlertEntity?
}