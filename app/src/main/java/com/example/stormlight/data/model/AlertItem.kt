package com.example.stormlight.data.model

import android.os.Message
import com.example.stormlight.utilities.enums.AlertType
import java.time.LocalDateTime

data class AlertItem(
    val id: Int,
    val triggerAtMillis: Long,
    val type: AlertType,
    val hour: Int,
    val minute: Int,
    val temp: String = "",
    val weatherDesc: String = "",
    val weatherIcon: String = "",
    val message: String
)