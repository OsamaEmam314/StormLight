package com.example.stormlight.ui.screens.settings.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stormlight.data.model.UserPrefrences
import com.example.stormlight.data.prefrences.repository.IPrefrencesRepository
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.ThemeMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: IPrefrencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk {
            every { userPreferences } returns flowOf(UserPrefrences())
        }
        viewModel = SettingsViewModel(repository)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setThemeMode_given_dark_theme_when_called_then_delegates_to_repository_with_dark() =
        runTest {
            // GIVEN — repository is ready to accept DARK
            coEvery { repository.setThemeMode(ThemeMode.DARK) } returns Unit

            // WHEN — ViewModel's setThemeMode is triggered
            viewModel.setThemeMode(ThemeMode.DARK)
            advanceUntilIdle()

            // THEN — repository received DARK exactly once
            coVerify(exactly = 1) { repository.setThemeMode(ThemeMode.DARK) }
        }
    @Test
    fun setThemeMode_given_light_theme_when_called_then_does_not_call_dark() = runTest {
        // GIVEN — repository is ready to accept LIGHT
        coEvery { repository.setThemeMode(ThemeMode.LIGHT) } returns Unit

        // WHEN
        viewModel.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()

        // THEN — LIGHT was passed, DARK was never touched
        coVerify(exactly = 1) { repository.setThemeMode(ThemeMode.LIGHT) }
        coVerify(exactly = 0) { repository.setThemeMode(ThemeMode.DARK) }
    }
    @Test
    fun setThemeMode_given_dark_theme_when_called_twice_then_repository_called_twice() = runTest {
        // GIVEN
        coEvery { repository.setThemeMode(ThemeMode.DARK) } returns Unit

        // WHEN — called twice
        viewModel.setThemeMode(ThemeMode.DARK)
        viewModel.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        // THEN — repository called exactly twice
        coVerify(exactly = 2) { repository.setThemeMode(ThemeMode.DARK) }
    }


    @Test
    fun setLanguage_given_arabic_when_called_then_delegates_arabic_to_repository() = runTest {
        // GIVEN — user selected Arabic
        coEvery { repository.setLanguage(Language.ARABIC) } returns Unit

        // WHEN
        viewModel.setLanguage(Language.ARABIC)
        advanceUntilIdle()

        // THEN — repository.setLanguage called once with ARABIC
        coVerify(exactly = 1) { repository.setLanguage(Language.ARABIC) }
    }

    @Test
    fun setLanguage_given_english_when_called_then_never_calls_arabic() = runTest {
        // GIVEN — user selected English
        coEvery { repository.setLanguage(Language.ENGLISH) } returns Unit

        // WHEN
        viewModel.setLanguage(Language.ENGLISH)
        advanceUntilIdle()

        // THEN — ENGLISH passed, ARABIC never touched
        coVerify(exactly = 1) { repository.setLanguage(Language.ENGLISH) }
        coVerify(exactly = 0) { repository.setLanguage(Language.ARABIC) }
    }

    @Test
    fun setLanguage_given_arabic_then_english_when_called_sequentially_then_each_delegated_once() =
        runTest {
            // GIVEN
            coEvery { repository.setLanguage(any()) } returns Unit

            // WHEN — language changed twice
            viewModel.setLanguage(Language.ARABIC)
            viewModel.setLanguage(Language.ENGLISH)
            advanceUntilIdle()

            // THEN — each was forwarded exactly once
            coVerify(exactly = 1) { repository.setLanguage(Language.ARABIC) }
            coVerify(exactly = 1) { repository.setLanguage(Language.ENGLISH) }
        }


    @Test
    fun onMapClicked_given_user_taps_map_when_called_then_sets_location_source_to_map() = runTest {
        // GIVEN
        coEvery { repository.setLocationSource(LocationSource.Map) } returns Unit

        // WHEN
        viewModel.onMapClicked()
        advanceUntilIdle()

        // THEN — LocationSource updated to Map
        coVerify(exactly = 1) { repository.setLocationSource(LocationSource.Map) }
    }
    @Test
    fun onMapClicked_given_user_taps_map_when_called_then_never_sets_GPS_location_source() =
        runTest {
            // GIVEN
            coEvery { repository.setLocationSource(any()) } returns Unit

            // WHEN
            viewModel.onMapClicked()
            advanceUntilIdle()

            // THEN — Map was set, GPS was never set
            coVerify(exactly = 1) { repository.setLocationSource(LocationSource.Map) }
            coVerify(exactly = 0) { repository.setLocationSource(LocationSource.GPS) }
        }
}