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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.stormlight.R
import com.example.stormlight.data.model.supportdto.ForecastItemDto
import com.example.stormlight.utilities.Constants
import com.example.stormlight.utilities.DateUtils
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.TemperatureUnit
import java.util.Locale

private data class DailyRowData(
    val dayLabel:    String,
    val dateLabel:   String,
    val iconCode:    String,
    val description: String,
    val tempMax:     Double,
    val tempMin:     Double,
    val isToday:     Boolean
)

@Composable
fun DailyForecast(
    forecastItems: List<ForecastItemDto>,
    timezoneOffset: Int,
    temperatureUnit: TemperatureUnit,
    language: Language,                  // ← new param so remember reruns on change
    modifier: Modifier = Modifier
) {
    val locale     = if (language == Language.ARABIC) Locale("ar") else Locale.ENGLISH
    val todayString = stringResource(R.string.label_today)

    val todayLabel = remember(timezoneOffset) {
        DateUtils.utcDateLabel(System.currentTimeMillis() / 1000L, timezoneOffset)
    }

    // Keyed on BOTH forecastItems AND language — reruns when user changes locale
    val dailyGroups = remember(forecastItems, language) {
        forecastItems
            .groupBy { DateUtils.utcDateLabel(it.dt, timezoneOffset) }
            .entries
            .take(5)
            .map { (dateKey, slots) ->
                val rep = slots.minByOrNull {
                    Math.abs((it.dt + timezoneOffset) % 86400 - 43200L)
                } ?: slots.first()
                DailyRowData(
                    dayLabel    = if (dateKey == todayLabel) todayString
                    else DateUtils.formatDayLabel(rep.dt, timezoneOffset, locale),
                    dateLabel   = DateUtils.formatShortDate(rep.dt, timezoneOffset, locale),
                    iconCode    = rep.weather.firstOrNull()?.icon.orEmpty(),
                    description = rep.weather.firstOrNull()?.description.orEmpty(),
                    tempMax     = slots.maxOf { it.main.tempMax },
                    tempMin     = slots.minOf { it.main.tempMin },
                    isToday     = dateKey == todayLabel
                )
            }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
            Text(
                text       = stringResource(R.string.label_5day),
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
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
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day name + short date stacked
        Column(
            modifier            = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text       = day.dayLabel.split(",").first(),   // strip any comma suffix
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.SemiBold,
                color      = if (day.isToday) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text          = day.dateLabel,   // now locale-aware: "Oct 23" or "23 أكتوبر"
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                letterSpacing = 0.8.sp
            )
        }

        // Icon + description
        Row(
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model              = Constants.weatherIconUrl(day.iconCode),
                contentDescription = day.description,
                modifier           = Modifier.size(28.dp),
                contentScale       = ContentScale.Fit
            )
            Text(
                text  = day.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // High / Low — always LTR so numbers don't flip
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = UnitUtils.formatTemp(day.tempMax, temperatureUnit.symbol),
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text  = UnitUtils.formatTemp(day.tempMin, temperatureUnit.symbol),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}