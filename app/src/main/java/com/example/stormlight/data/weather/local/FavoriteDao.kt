package com.example.stormlight.data.weather.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stormlight.data.model.FavWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY loc ASC")
    fun getAllFavorites(): Flow<List<FavWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favWeather: FavWeather)

    @Update
    suspend fun updateFavorite(favWeather: FavWeather)

    @Delete
    suspend fun deleteFavorite(favWeather: FavWeather)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE lat = :lat AND lon = :lon)")
    suspend fun isFavorite(lat: Double, lon: Double): Boolean

    @Query("SELECT * FROM favorites WHERE loc = :loc")
    suspend fun getFavoriteByLoc(loc: String): FavWeather?
}