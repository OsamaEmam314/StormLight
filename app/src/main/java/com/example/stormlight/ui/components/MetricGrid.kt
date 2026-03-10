package com.example.stormlight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stormlight.R
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.WindSpeedUnit

private data class MetricData(
    val iconRes: Int,
    val label: String,
    val value: String,
    val unit: String
)

@Composable
fun MetricsGrid(
    current: CurrentWeatherDto,
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    modifier: Modifier = Modifier
) {
    val windFormatted = UnitUtils.formatWind(current.wind.speed, windSpeedUnit.symbol)
    val windParts = windFormatted.split(" ")
    val windValue = windParts.getOrElse(0) { windFormatted }
    val windUnit = windParts.getOrElse(1) { "" }

    val visFormatted = UnitUtils.formatVisibility(current.visibility)
    val visParts = visFormatted.split(" ")
    val visValue = visParts.getOrElse(0) { visFormatted }
    val visUnit = visParts.getOrElse(1) { "" }

    val metrics = listOf(
        MetricData(R.drawable.ic_wind, stringResource(R.string.label_wind), windValue, windUnit),
        MetricData(
            R.drawable.ic_humidity,
            stringResource(R.string.label_humidity),
            "${current.main.humidity}",
            "%"
        ),
        MetricData(
            R.drawable.ic_pressure,
            stringResource(R.string.label_pressure),
            "${current.main.pressure}",
            "hPa"
        ),
        MetricData(
            R.drawable.ic_visibility,
            stringResource(R.string.label_visibility),
            visValue,
            visUnit
        )
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        metrics.forEach { metric ->
            MetricChip(
                metric = metric,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricChip(
    metric: MetricData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = metric.iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) { append(metric.value) }
                    if (metric.unit.isNotEmpty()) {
                        append("\u200A")
                        withStyle(
                            SpanStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            )
                        ) { append(metric.unit) }
                    }
                },
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = metric.label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}