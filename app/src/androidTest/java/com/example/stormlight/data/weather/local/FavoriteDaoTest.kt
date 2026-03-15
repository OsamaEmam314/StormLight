package com.example.stormlight.data.weather.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stormlight.data.db.StormLightDatabase
import com.example.stormlight.data.db.WeatherTypeConverters
import com.example.stormlight.data.model.CurrentWeatherDto
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.supportdto.CloudsDto
import com.example.stormlight.data.model.supportdto.CoordDto
import com.example.stormlight.data.model.supportdto.ForecastCityDto
import com.example.stormlight.data.model.supportdto.MainDto
import com.example.stormlight.data.model.supportdto.SysDto
import com.example.stormlight.data.model.supportdto.WeatherDescDto
import com.example.stormlight.data.model.supportdto.WindDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {
    private lateinit var database: StormLightDatabase
    private lateinit var dao: FavoriteDao
    private fun fakeCurrentWeather(cityName: String = "Cairo") = CurrentWeatherDto(
        coord = CoordDto(lon = 31.0, lat = 30.0),
        weather = listOf(
            WeatherDescDto(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        main = MainDto(
            temp = 28.0,
            feelsLike = 27.0,
            tempMin = 25.0,
            tempMax = 30.0,
            pressure = 1013,
            humidity = 40
        ),
        visibility = 10000,
        wind = WindDto(speed = 3.5, deg = 180),
        clouds = CloudsDto(all = 0),
        dt = 1_700_000_000L,
        sys = SysDto(country = "EG", sunrise = 1_699_990_000L, sunset = 1_700_030_000L),
        timezone = 7200,
        name = cityName
    )

    private fun fakeForecast() = ForecastDto(
        cod = "200",
        cnt = 0,
        list = emptyList(),
        city = ForecastCityDto(
            id = 1,
            name = "Cairo",
            coord = CoordDto(lon = 31.0, lat = 30.0),
            country = "EG",
            timezone = 7200,
            sunrise = 1_699_990_000L,
            sunset = 1_700_030_000L
        )
    )

    private fun fakeFavWeather(
        loc: String = "Cairo",
        lat: Double = 30.0,
        lon: Double = 31.0
    ) = FavWeather(
        loc = loc,
        lat = lat,
        lon = lon,
        temp = 28.0,
        currentWeather = fakeCurrentWeather(loc),
        forecast = fakeForecast()
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StormLightDatabase::class.java
        )
            .addTypeConverter(WeatherTypeConverters())
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavorite_given_new_entry_when_inserted_then_getAllFavorites_contains_it() = runTest {
        // GIVEN — a valid FavWeather object for Cairo
        val cairo = fakeFavWeather(loc = "Cairo")

        // WHEN — inserted into the DAO
        dao.insertFavorite(cairo)

        // THEN — getAllFavorites flow emits a list containing Cairo
        val list = dao.getAllFavorites().first()
        assertThat("List must not be empty after insert", list.isEmpty(), `is`(false))
        assertThat("List size must be 1", list.size, `is`(1))
        assertThat("Inserted city loc must be Cairo", list[0].loc, `is`("Cairo"))
    }

    @Test
    fun insertFavorite_given_duplicate_loc_when_inserted_twice_then_replaces_existing_entry() =
        runTest {
            // GIVEN — same loc inserted twice with different temps
            val first = fakeFavWeather(loc = "Cairo").copy(temp = 20.0)
            val second = fakeFavWeather(loc = "Cairo").copy(temp = 35.0)

            // WHEN — both are inserted (OnConflictStrategy.REPLACE)
            dao.insertFavorite(first)
            dao.insertFavorite(second)

            // THEN — only one row exists and it holds the latest temp
            val list = dao.getAllFavorites().first()
            assertThat("Must still be only 1 row due to REPLACE", list.size, `is`(1))
            assertThat("Temp must be updated to latest value", list[0].temp, `is`(35.0))
        }

    @Test
    fun deleteFavorite_given_existing_entry_when_deleted_then_getAllFavorites_is_empty() = runTest {
        // GIVEN — Cairo is already inserted
        val cairo = fakeFavWeather(loc = "Cairo")
        dao.insertFavorite(cairo)

        // WHEN — it is deleted
        dao.deleteFavorite(cairo)

        // THEN — the favorites list is empty
        val list = dao.getAllFavorites().first()
        assertThat("List must be empty after deletion", list.isEmpty(), `is`(true))
    }

    @Test
    fun deleteFavorite_given_two_entries_when_one_deleted_then_only_one_remains() = runTest {
        // GIVEN — Cairo and Alexandria are both inserted
        val cairo = fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0)
        val alexandria = fakeFavWeather(loc = "Alexandria", lat = 31.2, lon = 29.9)
        dao.insertFavorite(cairo)
        dao.insertFavorite(alexandria)

        // WHEN — only Cairo is deleted
        dao.deleteFavorite(cairo)

        // THEN — only Alexandria remains
        val list = dao.getAllFavorites().first()
        assertThat("Only 1 entry must remain", list.size, `is`(1))
        assertThat("Remaining entry must be Alexandria", list[0].loc, `is`("Alexandria"))
    }

    @Test
    fun isFavorite_given_inserted_entry_when_queried_with_same_coords_then_returns_true() =
        runTest {
            // GIVEN — Cairo is in the favorites table
            dao.insertFavorite(fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0))

            // WHEN — isFavorite is called with Cairo's exact coordinates
            val result = dao.isFavorite(lat = 30.0, lon = 31.0)

            // THEN — result is true
            assertThat("isFavorite must return true for an existing entry", result, `is`(true))
        }

    @Test
    fun isFavorite_given_empty_table_when_queried_then_returns_false() = runTest {
        // GIVEN — the table is empty (nothing inserted)

        // WHEN — isFavorite is called with any coordinates
        val result = dao.isFavorite(lat = 30.0, lon = 31.0)

        // THEN — result is false
        assertThat("isFavorite must return false when table is empty", result, `is`(false))
    }

    @Test
    fun isFavorite_given_different_coords_when_queried_then_returns_false() = runTest {
        // GIVEN — Cairo is inserted with specific coords
        dao.insertFavorite(fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0))

        // WHEN — isFavorite is called with different coordinates (Luxor)
        val result = dao.isFavorite(lat = 25.6, lon = 32.6)

        // THEN — result is false because Luxor was never inserted
        assertThat("isFavorite must return false for coords not in table", result, `is`(false))
    }
}