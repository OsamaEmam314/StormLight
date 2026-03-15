package com.example.stormlight.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.stormlight.data.model.AlertItem
import com.example.stormlight.utilities.NotificationHelper
import com.example.stormlight.utilities.enums.AlertType
import java.util.Calendar
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("ALERT_ID", -1)
        if (alertId == -1) return
        val message = intent.getStringExtra("ALERT_message") ?: return
        val isNotification = intent.getBooleanExtra("ALERT_type", true)
        val hour = intent.getIntExtra("ALERT_hour", -1)
        val minute = intent.getIntExtra("ALERT_minute", -1)

        val inputData = Data.Builder()
            .putInt("ALERT_ID", alertId)
            .putString("ALERT_message", message)
            .putBoolean("ALERT_type", isNotification)
            .putInt("ALERT_hour", hour)
            .putInt("ALERT_minute", minute)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        if (hour != -1 && minute != -1) {
            val tomorrowMillis = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            StormLightAlarmSchedulerImpl(context).scheduleAlert(
                AlertItem(
                    id = alertId,
                    triggerAtMillis = tomorrowMillis,
                    type = if (isNotification)
                        AlertType.NOTIFICATION
                    else
                        AlertType.ALARM,
                    message = message,
                    hour = hour,
                    minute = minute
                )
            )
        }
    }
}