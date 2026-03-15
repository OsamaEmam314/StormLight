package com.example.stormlight.alarmmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.utilities.NotificationHelper
import com.example.stormlight.utilities.Resource
import com.example.stormlight.utilities.UnitUtils
import kotlinx.coroutines.flow.first

class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: IPrefrencesRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val alertId = inputData.getInt("ALERT_ID", -1)
        if (alertId == -1) return Result.failure()

        val message = inputData.getString("ALERT_message") ?: return Result.failure()
        val isNotification = inputData.getBoolean("ALERT_type", true)
        val hour = inputData.getInt("ALERT_hour", -1)
        val minute = inputData.getInt("ALERT_minute", -1)

        val prefs = preferencesRepository.userPreferences.first()
        val lat = prefs.lat.toDoubleOrNull()
        val lon = prefs.lon.toDoubleOrNull()

        var temp = ""
        var weatherDesc = ""
        var weatherIcon = ""

        if (lat != null && lon != null) {
            val result = weatherRepository
                .getCurrentWeather(lat, lon, prefs.language.language)
                .first { it !is Resource.Loading }

            if (result is Resource.Success) {
                val data = result.data
                val convertedTemp = UnitUtils.convertTemp(
                    data.main.temp,
                    prefs.temperatureUnit.symbol
                ).toInt()
                val symbol = UnitUtils.tempSymbol(prefs.temperatureUnit)
                temp = "$convertedTemp$symbol"
                weatherDesc = data.weather.firstOrNull()?.description
                    ?.replaceFirstChar { it.uppercase() }
                    .orEmpty()
                weatherIcon = data.weather.firstOrNull()?.icon.orEmpty()
            }
        }
        if (isNotification) {
            NotificationHelper.sendNotification(
                context, alertId, message, temp, weatherDesc, weatherIcon
            )
        } else {
            NotificationHelper.sendAlarm(
                context, alertId, message, hour, minute, temp, weatherDesc, weatherIcon
            )
        }
        return Result.success()
    }
}