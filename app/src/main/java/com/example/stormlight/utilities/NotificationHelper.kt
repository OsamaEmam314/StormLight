package com.example.stormlight.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.stormlight.R
import com.example.stormlight.alarmmanager.DismissAlarmReceiver
import com.example.stormlight.alarmmanager.SnoozeAlarmReceiver
import com.example.stormlight.ui.main.view.MainActivity

object NotificationHelper {

    private var alarmPlayer: MediaPlayer? = null

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationSoundUri = Uri.parse(
                "android.resource://${context.packageName}/${R.raw.notification_ping}"
            )
            val notificationAudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val notificationChannel = NotificationChannel(
                Constants.CHANNEL_ID_NOTIFICATION,
                context.getString(R.string.channel_name_notification),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_desc_notification)
                setSound(notificationSoundUri, notificationAudioAttributes)
                enableVibration(false)
            }

            val alarmChannel = NotificationChannel(
                Constants.CHANNEL_ID_ALARM,
                context.getString(R.string.channel_name_alarm),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_desc_alarm)
                setSound(null, null)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }

    fun sendNotification(
        context: Context,
        alertId: Int,
        message: String,
        temp: String = "",
        weatherDesc: String = "",
        weatherIcon: String = ""
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context, alertId, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val hasWeather = temp.isNotBlank() || weatherDesc.isNotBlank()
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(context.getString(R.string.notification_title))

        if (message.isNotBlank()) {
            inboxStyle.addLine(message)
        }
        if (hasWeather) {
            inboxStyle.addLine(buildWeatherLine(context, temp, weatherDesc))
        }

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_NOTIFICATION)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                if (hasWeather) buildWeatherLine(context, temp, weatherDesc) else message
            )
            .setStyle(inboxStyle)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(alertId, notification)
    }

    fun sendAlarm(
        context: Context,
        alertId: Int,
        message: String,
        hour: Int,
        minute: Int,
        temp: String = "",
        weatherDesc: String = "",
        weatherIcon: String = ""
    ) {
        stopAlarmSound()

        alarmPlayer = MediaPlayer().apply {
            val alarmSoundUri = Uri.parse(
                "android.resource://${context.packageName}/${R.raw.alarm_sound}"
            )
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            setDataSource(context, alarmSoundUri)
            isLooping = true
            prepare()
            start()
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val hasWeather = temp.isNotBlank() || weatherDesc.isNotBlank()

        val dismissIntent = Intent(context, DismissAlarmReceiver::class.java).apply {
            putExtra("ALERT_ID", alertId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, alertId, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, SnoozeAlarmReceiver::class.java).apply {
            putExtra("ALERT_ID", alertId)
            putExtra("ALERT_message", message)
            putExtra("ALERT_hour", hour)
            putExtra("ALERT_minute", minute)
            putExtra("ALERT_temp", temp)
            putExtra("ALERT_weather_desc", weatherDesc)
            putExtra("ALERT_weather_icon", weatherIcon)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, alertId + 10_000, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context, alertId, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // BigTextStyle — shows full weather info when expanded
        val expandedText = buildString {
            if (message.isNotBlank()) {
                append(message)
                append("\n")
            }
            if (hasWeather) {
                append(buildWeatherLine(context, temp, weatherDesc))
            }
        }.trim()

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.alarm_title))
            .setContentText(
                if (hasWeather) buildWeatherLine(context, temp, weatherDesc) else message
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(expandedText)
                    .setBigContentTitle(context.getString(R.string.alarm_title))
            )
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(openAppPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                android.R.drawable.ic_media_pause,
                context.getString(R.string.action_snooze),
                snoozePendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.alarm_dismiss),
                dismissPendingIntent
            )
            .build()

        notificationManager.notify(alertId, notification)
    }

    private fun buildWeatherLine(context: Context, temp: String, weatherDesc: String): String {
        return listOf(weatherDesc, temp)
            .filter { it.isNotBlank() }
            .joinToString(" • ")
    }

    fun stopAlarmSound() {
        alarmPlayer?.let {
            runCatching {
                if (it.isPlaying) it.stop()
                it.release()
            }
        }
        alarmPlayer = null
    }
}
