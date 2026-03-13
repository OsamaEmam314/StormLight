package com.example.stormlight.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stormlight.data.model.AlertItem
import java.time.ZoneId
import kotlin.jvm.java

class StormLightAlarmSchedulerImpl(
    private val context: Context
) : StormLightAlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun scheduleAlert(alert: AlertItem) {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("ALERT_message", alert.message)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                alert.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        )
    }

    override fun cancelAlert(alert: AlertItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alert.hashCode(),
                Intent(context, AlertReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}