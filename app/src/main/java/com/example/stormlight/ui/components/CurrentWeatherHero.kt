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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.stormlight.utilities.Constants
import com.example.stormlight.utilities.UnitUtils
import com.example.stormlight.utilities.enums.TemperatureUnit

private fun Modifier.ambientGlow(
    color: Color,
    radius: Dp = 120.dp,
    alpha: Float = 0.25f
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = android.graphics.Color.TRANSPARENT
                setShadowLayer(
                    radius.toPx(), 0f, 0f,
                    color.copy(alpha = alpha).toArgb()
                )
            }
        }
        canvas.drawCircle(
            center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f),
            radius = radius.toPx() * 0.6f,
            paint = paint
        )
    }
}


@Composable
fun CurrentWeatherHero(
    temperature: Double,
    tempMin: Double,
    tempMax: Double,
    description: String,
    iconCode: String,
    temperatureUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val symbol = UnitUtils.tempSymbol(temperatureUnit)
    val tempInt = UnitUtils.convertTemp(temperature, temperatureUnit.symbol).toInt().toString()
    val maxStr = "${UnitUtils.convertTemp(tempMax, temperatureUnit.symbol).toInt()}$symbol"
    val minStr = "${UnitUtils.convertTemp(tempMin, temperatureUnit.symbol).toInt()}$symbol"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .ambientGlow(color = primary, radius = 140.dp, alpha = 0.3f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AsyncImage(
            model = Constants.weatherIconUrl(iconCode),
            contentDescription = description,
            modifier = Modifier.size(110.dp),
            contentScale = ContentScale.Fit
        )

        androidx.compose.runtime.CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = tempInt,
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 110.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = symbol,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Light,
                    color = primary,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }

        Text(
            text = description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        androidx.compose.runtime.CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "H:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = maxStr,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "L:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = minStr,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}