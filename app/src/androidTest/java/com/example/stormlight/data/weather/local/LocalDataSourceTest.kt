package com.example.stormlight.data.weather.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.stormlight.data.datastore.WeatherDataStore
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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test


class LocalDataSourceTest {
    private lateinit var database: StormLightDatabase
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var weatherDataStore: WeatherDataStore


    private fun fakeCurrentWeather(name: String = "Cairo") = CurrentWeatherDto(
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
        name = name
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

        favoriteDao = database.favoriteDao()
        weatherDataStore = mockk(relaxed = true)

        coEvery { weatherDataStore.currentWeatherFlow } returns flowOf(null)
        coEvery { weatherDataStore.forecastFlow } returns flowOf(null)

        localDataSource = WeatherLocalDataSource(
            weatherDataStore = weatherDataStore,
            favoriteDao = favoriteDao
        )
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertFavorite_given_valid_entry_when_inserted_then_getAllFavorites_contains_it() =
        runTest {
            // GIVEN — a valid FavWeather for Cairo
            val cairo = fakeFavWeather(loc = "Cairo")

            // WHEN — inserted through the LocalDataSource
            localDataSource.insertFavorite(cairo)

            // THEN — getAllFavorites flow emits a list with that entry
            val list = localDataSource.getAllFavorites().first()
            assertThat("List must not be empty after insert", list.isEmpty(), `is`(false))
            assertThat("List size must be 1", list.size, `is`(1))
            assertThat("Inserted loc must be Cairo", list[0].loc, `is`("Cairo"))
        }

    @Test
    fun insertFavorite_given_two_cities_when_both_inserted_then_getAllFavorites_returns_both() =
        runTest {
            // GIVEN — two distinct FavWeather entries
            val cairo = fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0)
            val alexandria = fakeFavWeather(loc = "Alexandria", lat = 31.2, lon = 29.9)

            // WHEN — both inserted
            localDataSource.insertFavorite(cairo)
            localDataSource.insertFavorite(alexandria)

            // THEN — both are present in the list
            val list = localDataSource.getAllFavorites().first()
            assertThat("List must contain 2 entries", list.size, `is`(2))
            val locs = list.map { it.loc }
            assertThat("Cairo must be in the list", locs.contains("Cairo"), `is`(true))
            assertThat("Alexandria must be in the list", locs.contains("Alexandria"), `is`(true))
        }

    @Test
    fun deleteFavorite_given_inserted_entry_when_deleted_then_getAllFavorites_is_empty() = runTest {
        // GIVEN — Cairo inserted first
        val cairo = fakeFavWeather(loc = "Cairo")
        localDataSource.insertFavorite(cairo)

        // WHEN — deleted through LocalDataSource
        localDataSource.deleteFavorite(cairo)

        // THEN — list is empty
        val list = localDataSource.getAllFavorites().first()
        assertThat("List must be empty after delete", list.isEmpty(), `is`(true))
    }

    @Test
    fun deleteFavorite_given_two_entries_when_one_deleted_then_only_the_other_remains() = runTest {
        // GIVEN — Cairo and Alexandria both inserted
        val cairo = fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0)
        val alexandria = fakeFavWeather(loc = "Alexandria", lat = 31.2, lon = 29.9)
        localDataSource.insertFavorite(cairo)
        localDataSource.insertFavorite(alexandria)

        // WHEN — only Cairo is deleted
        localDataSource.deleteFavorite(cairo)

        // THEN — only Alexandria remains
        val list = localDataSource.getAllFavorites().first()
        assertThat("Must have exactly 1 entry after delete", list.size, `is`(1))
        assertThat("Remaining entry must be Alexandria", list[0].loc, `is`("Alexandria"))
    }

    @Test
    fun isFavorite_given_inserted_entry_when_queried_with_same_coords_then_returns_true() =
        runTest {
            // GIVEN — Cairo is in the favorites table
            localDataSource.insertFavorite(fakeFavWeather(loc = "Cairo", lat = 30.0, lon = 31.0))

            // WHEN — isFavorite called with Cairo's exact coords
            val result = localDataSource.isFavorite(lat = 30.0, lon = 31.0)

            // THEN — true
            assertThat("isFavorite must be true for an existing entry", result, `is`(true))
        }

    @Test
    fun isFavorite_given_empty_table_when_queried_then_returns_false() = runTest {
        // GIVEN — nothing inserted

        // WHEN — isFavorite is called
        val result = localDataSource.isFavorite(lat = 30.0, lon = 31.0)

        // THEN — false
        assertThat("isFavorite must be false on empty table", result, `is`(false))
    }
}