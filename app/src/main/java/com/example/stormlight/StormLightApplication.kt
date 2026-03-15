package com.example.stormlight

import android.app.Application
import androidx.work.Configuration
import com.example.stormlight.alarmmanager.WeatherAlertWorkerFactory
import com.example.stormlight.data.datastore.StormLightPreferencesDataStore
import com.example.stormlight.data.datastore.WeatherDataStore
import com.example.stormlight.data.db.StormLightDatabase
import com.example.stormlight.data.network.RetrofitClient
import com.example.stormlight.data.prefrences.local.PreferencesLocalDataSource
import com.example.stormlight.data.prefrences.repository.PrefrencesRepository
import com.example.stormlight.data.weather.local.WeatherLocalDataSource
import com.example.stormlight.data.weather.remote.WeatherRemoteDataSource
import com.example.stormlight.data.weather.repository.WeatherRepositoryImpl

class StormLightApplication : Application(), Configuration.Provider {

    private val preferencesRepository by lazy {
        PrefrencesRepository(
            PreferencesLocalDataSource(
                StormLightPreferencesDataStore(applicationContext)
            )
        )
    }

    private val weatherRepository by lazy {
        WeatherRepositoryImpl(
            WeatherRemoteDataSource(RetrofitClient.weatherApiService),
            WeatherLocalDataSource(
                WeatherDataStore(applicationContext),
                StormLightDatabase.getInstance(applicationContext).favoriteDao()
            )
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                WeatherAlertWorkerFactory(
                    weatherRepository = weatherRepository,
                    preferencesRepository = preferencesRepository
                )
            )
            .build()
}
