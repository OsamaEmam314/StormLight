package com.example.stormlight.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stormlight.utilities.enums.AlertType

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val type: AlertType,
    val isEnabled: Boolean = true,
    val label: String = ""

    )
