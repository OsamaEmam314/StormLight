package com.example.stormlight.alarmmanager

import com.example.stormlight.data.model.AlertItem

interface StormLightAlarmScheduler {
    fun scheduleAlert(alert: AlertItem)
    fun cancelAlert(alert: AlertItem)
}

