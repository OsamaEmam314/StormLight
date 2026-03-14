package com.example.stormlight.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stormlight.data.alerts.local.AlertDao
import com.example.stormlight.data.model.AlertEntity
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.weather.local.FavoriteDao

@Database(
    entities = [AlertEntity::class , FavWeather::class ],
    version = 2,
    exportSchema = false
)
@TypeConverters(WeatherTypeConverters::class)
abstract class StormLightDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao

    abstract fun favoriteDao(): FavoriteDao
    companion object {
        const val DATABASE_NAME = "stormlight_db"
        private var instance: StormLightDatabase? = null
        fun getInstance(context: Context): StormLightDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    StormLightDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                newInstance
            }


        }

    }
}
