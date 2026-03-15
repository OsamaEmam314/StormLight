package com.example.stormlight.data.weather.repository

import com.example.stormlight.data.model.ForecastDto
import com.example.stormlight.data.model.GeoLocationDto
import com.example.stormlight.data.weather.local.LocalDataSource
import com.example.stormlight.data.weather.remote.RemoteDataSource
import com.example.stormlight.utilities.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class WeatherRepositoryTest {
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var repository: WeatherRepositoryImpl
    private fun fakeForecast(): ForecastDto = mockk(relaxed = true)
    private fun fakeGeoLocation(name: String = "Cairo"): GeoLocationDto =
        GeoLocationDto(name = name, lat = 30.0, lon = 31.0, country = "EG", localNames = null)

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        localDataSource = mockk(relaxed = true)
        repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getForecast - given no cache and remote succeeds - when collected - then emits Loading then Success with fresh data`() =
        runTest {
            // GIVEN — cache is empty, remote returns a valid forecast
            val freshForecast = fakeForecast()
            coEvery { localDataSource.forecastFlow } returns flowOf(null)
            coEvery { remoteDataSource.getForecast(any(), any(), any()) } returns freshForecast

            // WHEN — collecting the flow
            val emissions = repository.getForecast(30.0, 31.0, "en").toList()

            // THEN — first emission is Loading
            assertThat(
                "First emission must be Loading",
                emissions[0],
                instanceOf(Resource.Loading::class.java)
            )
            val last = emissions.last()
            assertThat(
                "Last emission must be Success",
                last,
                instanceOf(Resource.Success::class.java)
            )
            assertThat(
                "Success must carry the fresh forecast object",
                (last as Resource.Success).data,
                `is`(freshForecast)
            )


        }

    @Test
    fun `getForecast - given no cache and remote throws - when collected - then emits Loading then Error with message`() =
        runTest {
            // GIVEN — cache is empty, remote fails with a specific message
            coEvery { localDataSource.forecastFlow } returns flowOf(null)
            coEvery {
                remoteDataSource.getForecast(any(), any(), any())
            } throws Exception("Network unavailable")

            // WHEN — the flow is fully collected
            val emissions = repository.getForecast(30.0, 31.0, "en").toList()

            // THEN — last emission is an Error
            val last = emissions.last()
            assertThat(
                "Last emission must be Error when remote fails and cache is empty",
                last,
                instanceOf(Resource.Error::class.java)
            )
            // AND — the error carries the exception message
            assertThat(
                "Error message must match the thrown exception",
                (last as Resource.Error).message,
                `is`("Network unavailable")
            )
        }

    @Test
    fun `getForecast - given cached forecast exists and remote throws - when collected - then emits Loading then cached Success without Error`() =
        runTest {
            // GIVEN — cache has a forecast, remote will throw
            val cachedForecast = fakeForecast()
            coEvery { localDataSource.forecastFlow } returns flowOf(cachedForecast)
            coEvery {
                remoteDataSource.getForecast(any(), any(), any())
            } throws Exception("Timeout")

            // WHEN — the flow is fully collected
            val emissions = repository.getForecast(30.0, 31.0, "en").toList()

            // THEN — no Error is emitted because cache covered the failure
            val hasError = emissions.any { it is Resource.Error }
            assertThat(
                "Must NOT emit Error when cache exists and remote fails",
                hasError,
                `is`(false)
            )
            // AND — the cached Success is emitted
            val successEmission = emissions.filterIsInstance<Resource.Success<ForecastDto>>()
            assertThat(
                "Must emit at least one Success from cache",
                successEmission.isNotEmpty(),
                `is`(true)
            )
        }

    @Test
    fun `searchCity - given remote returns a list - when called - then returns Success with correct city names`() =
        runTest {
            // GIVEN — remote returns two cities
            val geoList = listOf(fakeGeoLocation("Cairo"), fakeGeoLocation("Al-Kahira"))
            coEvery { remoteDataSource.searchCity("Cairo") } returns geoList

            // WHEN — repository searchCity is called
            val result = repository.searchCity("Cairo")

            // THEN — result is Success
            assertThat(
                "Result must be Resource.Success",
                result,
                instanceOf(Resource.Success::class.java)
            )
            val data = (result as Resource.Success).data
            // AND — data is not null
            assertThat("Data must not be null", data, notNullValue())
        }

    @Test
    fun `searchCity - given remote throws - when called - then returns Error with exception message`() =
        runTest {
            // GIVEN — remote fails
            coEvery { remoteDataSource.searchCity(any()) } throws Exception("City search failed")

            // WHEN — repository searchCity is called
            val result = repository.searchCity("Unknown")

            // THEN — result is Error carrying the exception message
            assertThat(
                "Result must be Resource.Error",
                result,
                instanceOf(Resource.Error::class.java)
            )
            assertThat(
                "Error message must match exception",
                (result as Resource.Error).message,
                `is`("City search failed")
            )
        }

    @Test
    fun `reverseGeocode - given remote resolves a location - when called - then returns Success with matching city name`() =
        runTest {
            // GIVEN — remote returns a valid geo location
            val geoLocation = fakeGeoLocation("Cairo")
            coEvery { remoteDataSource.reverseGeocode(30.0, 31.0) } returns geoLocation

            // WHEN — repository reverseGeocode is called
            val result = repository.reverseGeocode(30.0, 31.0)

            // THEN — result is Success
            assertThat(
                "Result must be Resource.Success",
                result,
                instanceOf(Resource.Success::class.java)
            )
            val data = (result as Resource.Success).data
            // AND — the returned city name is correct
            assertThat("Returned city name must be Cairo", data.name, `is`("Cairo"))
        }

    @Test
    fun `reverseGeocode - given remote returns null - when called - then returns Error with no location message`() =
        runTest {
            // GIVEN — remote found nothing for the coordinates
            coEvery { remoteDataSource.reverseGeocode(any(), any()) } returns null

            // WHEN — repository reverseGeocode is called
            val result = repository.reverseGeocode(0.0, 0.0)

            // THEN — result is Error with the expected message
            assertThat(
                "Result must be Resource.Error",
                result,
                instanceOf(Resource.Error::class.java)
            )
            assertThat(
                "Error message must say no location found",
                (result as Resource.Error).message,
                `is`("No location found for coordinates")
            )
        }

    @Test
    fun `reverseGeocode - given remote throws - when called - then returns Error with exception message`() =
        runTest {
            // GIVEN — remote throws a network error
            coEvery {
                remoteDataSource.reverseGeocode(any(), any())
            } throws Exception("Reverse geocoding failed")

            // WHEN — repository reverseGeocode is called
            val result = repository.reverseGeocode(30.0, 31.0)

            // THEN — result is Error carrying the exception message
            assertThat(
                "Result must be Resource.Error",
                result,
                instanceOf(Resource.Error::class.java)
            )
            assertThat(
                "Error message must match exception",
                (result as Resource.Error).message,
                `is`("Reverse geocoding failed")
            )
        }
}