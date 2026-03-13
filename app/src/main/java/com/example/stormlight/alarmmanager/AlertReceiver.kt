package com.example.stormlight.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent?.getStringExtra("ALERT_message") ?: return
        println("Alert message: $message")
    }
}