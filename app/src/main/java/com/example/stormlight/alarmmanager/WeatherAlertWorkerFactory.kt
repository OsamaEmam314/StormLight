package com.example.stormlight.alarmmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository

class WeatherAlertWorkerFactory(
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: PrefrencesRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WeatherAlertWorker::class.java.name -> {
                WeatherAlertWorker(
                    appContext,
                    workerParameters,
                    weatherRepository,
                    preferencesRepository
                )
            }

            else -> null
        }

    }
}