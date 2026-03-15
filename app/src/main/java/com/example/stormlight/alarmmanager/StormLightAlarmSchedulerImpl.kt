package com.example.stormlight.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.stormlight.data.model.AlertItem
import com.example.stormlight.utilities.enums.AlertType

class StormLightAlarmSchedulerImpl(
    private val context: Context
) : StormLightAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleAlert(alert: AlertItem) {

        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_ID", alert.id)
            putExtra("ALERT_message", alert.message)
            putExtra("ALERT_type", alert.type == AlertType.NOTIFICATION)
            putExtra("ALERT_hour", alert.hour)
            putExtra("ALERT_minute", alert.minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.triggerAtMillis,
            pendingIntent
        )


    }

    override fun cancelAlert(alert: AlertItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alert.id,
                Intent(context, AlertReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}