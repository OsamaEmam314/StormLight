package com.example.stormlight.alarmmanager

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stormlight.data.model.AlertItem
import com.example.stormlight.utilities.Constants
import com.example.stormlight.utilities.NotificationHelper
import com.example.stormlight.utilities.enums.AlertType

class SnoozeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("ALERT_ID", -1)
        if (alertId == -1) return
        val message = intent.getStringExtra("ALERT_message") ?: return
        val hour = intent.getIntExtra("ALERT_hour", -1)
        val minute = intent.getIntExtra("ALERT_minute", -1)

        NotificationHelper.stopAlarmSound()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alertId)

        StormLightAlarmSchedulerImpl(context).scheduleAlert(
            AlertItem(
                id = alertId,
                triggerAtMillis = System.currentTimeMillis() + Constants.SNOOZE_DURATION_MS,
                type = AlertType.ALARM,
                message = message,
                hour = hour,
                minute = minute
            )
        )
    }
}