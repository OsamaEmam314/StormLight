package com.example.stormlight.ui.screens.home.components.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.stormlight.R
import com.example.stormlight.data.model.supportdto.ForecastItemDto
import com.example.stormlight.utilities.Constants
import com.example.stormlight.utilities.DateUtils
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.TemperatureUnit

@Composable
fun DailyForecast(
    forecastItems: List<ForecastItemDto>,
    timezoneOffset: Int,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    // Group 3-hour slots by date label → one row per day
    val dailyGroups = remember(forecastItems) {
        forecastItems
            .groupBy { DateUtils.utcDateLabel(it.dt, timezoneOffset) }
            .entries
            .take(5)
            .map { (_, slots) ->
                val rep = slots.minByOrNull {
                    Math.abs((it.dt + timezoneOffset) % 86400 - 43200L)
                } ?: slots.first()
                DailyRowData(
                    dayLabel    = DateUtils.formatDayLabel(rep.dt, timezoneOffset),
                    iconCode    = rep.weather.firstOrNull()?.icon.orEmpty(),
                    description = rep.weather.firstOrNull()?.description.orEmpty(),
                    tempMax     = slots.maxOf { it.main.tempMax },
                    tempMin     = slots.minOf { it.main.tempMin }
                )
            }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.label_5day),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dailyGroups.forEach { day ->
                DailyRow(day = day, temperatureUnit = temperatureUnit)
            }
        }
    }
}

@Composable
private fun DailyRow(
    day: DailyRowData,
    temperatureUnit: TemperatureUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day label
        Text(
            text = day.dayLabel,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.2f)
        )

        // Icon + description
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = Constants.weatherIconUrl(day.iconCode),
                contentDescription = day.description,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = day.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        androidx.compose.runtime.CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr
        )
        {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UnitUtils.formatTemp(day.tempMax, temperatureUnit.symbol),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = UnitUtils.formatTemp(day.tempMin, temperatureUnit.symbol),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
        }
}

private data class DailyRowData(
    val dayLabel: String,
    val iconCode: String,
    val description: String,
    val tempMax: Double,
    val tempMin: Double
)
