package com.example.stormlight.data.model

import com.example.stormlight.utilities.enums.AlertType
import java.time.LocalDateTime

data class AlertItem(
    val id: Int,
   val time: LocalDateTime,
    val type: AlertType,
    val message: String
)
