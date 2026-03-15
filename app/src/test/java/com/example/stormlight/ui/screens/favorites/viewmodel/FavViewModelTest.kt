package com.example.stormlight.ui.screens.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.data.weather.repository.WeatherRepository
import com.example.stormlight.ui.screens.favorites.view.FavoritesUiEvent
import com.example.stormlight.utilities.Resource
import com.example.stormlight.utilities.enums.Language
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var prefrencesRepository: IPrefrencesRepository
    private lateinit var viewModel: FavViewModel
    private fun fakeFavWeather(loc: String = "Cairo") = mockk<FavWeather>(relaxed = true) {
        every { this@mockk.loc } returns loc
    }

    private val defaultPrefs = UserPrefrences(language = Language.ENGLISH)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        weatherRepository = mockk()
        prefrencesRepository = mockk()
        every { weatherRepository.getAllFavorites() } returns flowOf(emptyList())
        every { prefrencesRepository.userPreferences } returns flowOf(defaultPrefs)
        viewModel = FavViewModel(weatherRepository, prefrencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun removeFavorite_given_existing_favorite_when_called_then_delegates_to_repository() =
        runTest {
            // GIVEN — a favorite city and the repository is ready to remove it
            val cairo = fakeFavWeather(loc = "Cairo")
            coEvery { weatherRepository.removeFavorite(cairo) } returns Unit

            // WHEN — removeFavorite is called on the ViewModel
            viewModel.removeFavorite(cairo)
            advanceUntilIdle()

            // THEN — repository.removeFavorite was called exactly once
            coVerify(exactly = 1) { weatherRepository.removeFavorite(cairo) }
        }

    @Test
    fun removeFavorite_given_existing_favorite_when_called_then_emits_snackbar_with_loc_name() =
        runTest {
            // GIVEN
            val cairo = fakeFavWeather(loc = "Cairo")
            coEvery { weatherRepository.removeFavorite(cairo) } returns Unit

            // Subscribe BEFORE triggering
            var capturedEvent: FavoritesUiEvent? = null
            val job = launch { capturedEvent = viewModel.uiEvent.first() }

            // WHEN
            viewModel.removeFavorite(cairo)
            advanceUntilIdle()
            job.cancel()

            // THEN — ShowSnackbar was emitted with the city name
            assertThat(
                "uiEvent must be ShowSnackbar",
                capturedEvent,
                instanceOf(FavoritesUiEvent.ShowSnackbar::class.java)
            )
            assertThat(
                "Snackbar message must mention Cairo",
                (capturedEvent as FavoritesUiEvent.ShowSnackbar).message,
                `is`("Cairo removed")
            )
        }


    @Test
    fun onLocationConfirmed_given_location_already_favorite_when_called_then_emits_already_in_favorites_snackbar() =
        runTest {
            // GIVEN — the coordinates are already saved
            coEvery { weatherRepository.isFavorite(30.0, 31.0) } returns true

            var capturedEvent: FavoritesUiEvent? = null
            val job = launch { capturedEvent = viewModel.uiEvent.first() }

            // WHEN
            viewModel.onLocationConfirmed(lat = 30.0, lon = 31.0, cityName = "Cairo")
            advanceUntilIdle()
            job.cancel()

            // THEN — snackbar saying already in favorites
            assertThat(
                "Event must be ShowSnackbar",
                capturedEvent,
                instanceOf(FavoritesUiEvent.ShowSnackbar::class.java)
            )
            assertThat(
                "Message must say already in favorites",
                (capturedEvent as FavoritesUiEvent.ShowSnackbar).message,
                `is`("Cairo is already in favorites")
            )
        }

    @Test
    fun onLocationConfirmed_given_already_favorite_when_called_then_getFavoriteWeather_never_called() =
        runTest {
            // GIVEN — already a favorite
            coEvery { weatherRepository.isFavorite(30.0, 31.0) } returns true

            // Subscribe to drain the event
            val job = launch { viewModel.uiEvent.first() }

            // WHEN
            viewModel.onLocationConfirmed(lat = 30.0, lon = 31.0, cityName = "Cairo")
            advanceUntilIdle()
            job.cancel()

            // THEN — getFavoriteWeather was NEVER called (early return)
            coVerify(exactly = 0) {
                weatherRepository.getFavoriteWeather(any(), any(), any(), any())
            }
        }


    @Test
    fun onFavoriteClicked_given_a_favorite_when_clicked_then_emits_NavigateToDetail_with_correct_loc() =
        runTest {
            // GIVEN — a favorite city card is tapped
            val cairo = fakeFavWeather(loc = "Cairo")

            var capturedEvent: FavoritesUiEvent? = null
            val job = launch { capturedEvent = viewModel.uiEvent.first() }

            // WHEN
            viewModel.onFavoriteClicked(cairo)
            advanceUntilIdle()
            job.cancel()

            // THEN — NavigateToDetail emitted with Cairo's loc
            assertThat(
                "Event must be NavigateToDetail",
                capturedEvent,
                instanceOf(FavoritesUiEvent.NavigateToDetail::class.java)
            )
            assertThat(
                "loc must be Cairo",
                (capturedEvent as FavoritesUiEvent.NavigateToDetail).loc,
                `is`("Cairo")
            )
        }

}