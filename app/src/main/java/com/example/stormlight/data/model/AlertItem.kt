package com.example.stormlight.data.model

import android.os.Message
import com.example.stormlight.utilities.enums.AlertType
import java.time.LocalDateTime

data class AlertItem(
    val id: Int,
    val triggerAtMillis: Long,
    val type: AlertType,
    val message: String,
    val hour: Int,
    val minute: Int
)
