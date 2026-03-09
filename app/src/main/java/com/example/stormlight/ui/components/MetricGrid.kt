package com.example.stormlight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stormlight.R
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.WindSpeedUnit

@Composable
private fun MetricCard(
    iconRes: Int,
    label: String,
    value: String,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .padding(6.dp)
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(
    current: CurrentWeatherDto,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    modifier: Modifier = Modifier
) {
    val windFormatted = UnitUtils.formatWind(current.wind.speed, windSpeedUnit.symbol)
    val visFormatted = UnitUtils.formatVisibility(current.visibility)

    val windParts = windFormatted.split(" ")
    val windValue = windParts.getOrElse(0) { windFormatted }
    val windUnit = windParts.getOrElse(1) { "" }

    val visParts = visFormatted.split(" ")
    val visValue = visParts.getOrElse(0) { visFormatted }
    val visUnit = visParts.getOrElse(1) { "" }

    val cards = listOf(
        arrayOf(
            R.drawable.ic_humidity,
            stringResource(R.string.label_humidity),
            "${current.main.humidity}",
            "%"
        ),
        arrayOf(R.drawable.ic_wind, stringResource(R.string.label_wind), windValue, windUnit),
        arrayOf(
            R.drawable.ic_pressure,
            stringResource(R.string.label_pressure),
            "${current.main.pressure}",
            "hPa"
        ),
        arrayOf(
            R.drawable.ic_visibility,
            stringResource(R.string.label_visibility),
            visValue,
            visUnit
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                @Suppress("UNCHECKED_CAST")
                (row as List<Array<Any>>).forEach { (icon, label, value, unit) ->
                    MetricCard(
                        iconRes = icon as Int,
                        label = label as String,
                        value = value as String,
                        unit = unit as String,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}