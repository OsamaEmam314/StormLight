package com.example.stormlight.ui.screens.home.components.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.TemperatureUnit
import java.util.Locale

@Composable
fun HourlyForecast(
    forecastItems: List<ForecastItemDto>,
    timezoneOffset: Int,
    temperatureUnit: TemperatureUnit,
    language: Language,                  // ← new param
    modifier: Modifier = Modifier
) {
    val locale = if (language == Language.ARABIC) Locale("ar") else Locale.ENGLISH

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Schedule,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
            Text(
                text       = stringResource(R.string.label_hourly),
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        LazyRow(
            contentPadding        = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(forecastItems) { item ->
                HourlyItem(
                    item            = item,
                    timezoneOffset  = timezoneOffset,
                    temperatureUnit = temperatureUnit,
                    locale          = locale
                )
            }
        }
    }
}

@Composable
private fun HourlyItem(
    item: ForecastItemDto,
    timezoneOffset: Int,
    temperatureUnit: TemperatureUnit,
    locale: Locale
) {
    val nowHour  = System.currentTimeMillis() / 1000L / 3600
    val itemHour = item.dt / 3600
    val isNow    = remember(item.dt) { nowHour == itemHour }

    // Locale-aware hour label — "03 م" in Arabic, "03 PM" in English
    val hourLabel = remember(item.dt, locale) {
        DateUtils.formatHourLabel(item.dt, timezoneOffset, locale)
    }

    val shape = RoundedCornerShape(32.dp)

    val cardModifier = if (isNow) {
        Modifier
            .shadow(
                elevation    = 16.dp,
                shape        = shape,
                ambientColor = Color(0xFF0DA6F2).copy(alpha = 0.5f),
                spotColor    = Color(0xFF0DA6F2).copy(alpha = 0.5f)
            )
            .background(MaterialTheme.colorScheme.primary, shape)
    } else {
        Modifier
            .background(Color.White.copy(alpha = 0.05f), shape)
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
    }

    Column(
        modifier = Modifier
            .width(70.dp)
            .clip(shape)
            .then(cardModifier)
            .padding(vertical = 20.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text       = if (isNow) stringResource(R.string.label_now) else hourLabel,
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color      = if (isNow) Color.White
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )

        AsyncImage(
            model              = Constants.weatherIconUrl(item.weather.firstOrNull()?.icon.orEmpty()),
            contentDescription = item.weather.firstOrNull()?.description,
            modifier           = Modifier.size(28.dp),
            contentScale       = ContentScale.Fit
        )

        // Temperature — always LTR regardless of locale
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text       = UnitUtils.formatTemp(item.main.temp, temperatureUnit.symbol),
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color      = if (isNow) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}