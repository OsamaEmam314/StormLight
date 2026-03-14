package com.example.stormlight.ui.screens.settings.view

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stormlight.R
import com.example.stormlight.data.prefrences.PrefrencesRepository
import com.example.stormlight.ui.screens.map.MapPickerActivity
import com.example.stormlight.ui.screens.settings.viewmodel.SettingsViewModel
import com.example.stormlight.ui.screens.settings.viewmodel.SettingsViewModelFactory
import com.example.stormlight.utilities.enums.Language
import com.example.stormlight.utilities.enums.LocationSource
import com.example.stormlight.utilities.enums.TemperatureUnit
import com.example.stormlight.utilities.enums.ThemeMode
import com.example.stormlight.utilities.enums.WindSpeedUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val repository = PrefrencesRepository(context)
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModelFactory(repository))
    val prefs by viewModel.userPrefrencesState.collectAsState()
    val mapLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lat = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LAT, 0.0) ?: 0.0
            val lon = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LON, 0.0) ?: 0.0
            val cityName = result.data
                ?.getStringExtra(MapPickerActivity.EXTRA_CITY_NAME)
                .orEmpty()
             viewModel.setLatLong(lat, lon)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsUiEvent.NavigateToMap -> {
                    mapLauncher.launch(Intent(context, MapPickerActivity::class.java))
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {

        SettingsSection(title = stringResource(R.string.settings_location)) {
            StormSegmentedRow(
                options = LocationSource.entries,
                selected = prefs.locationSource,
                label = {
                    when (it) {
                        LocationSource.GPS -> stringResource(R.string.settings_gps)
                        LocationSource.Map -> stringResource(R.string.settings_map)
                    }
                },
                onSelect = {
                    viewModel.setLocationSource(it)
                    if (it == LocationSource.Map) {
                        viewModel.onMapClicked()
                    }
                }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_temperature)) {
            StormSegmentedRow(
                options = TemperatureUnit.entries,
                selected = prefs.temperatureUnit,
                label = {
                    when (it) {
                        TemperatureUnit.CELSIUS -> stringResource(R.string.settings_celsius)
                        TemperatureUnit.FAHRENHEIT -> stringResource(R.string.settings_fahrenheit)
                        TemperatureUnit.KELVIN -> stringResource(R.string.settings_kelvin)
                    }
                },
                onSelect = {
                    viewModel.setTemperatureUnit(it)
                }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_wind)) {
            StormSegmentedRow(
                options = WindSpeedUnit.entries,
                selected = prefs.windSpeedUnit,
                label = {
                    when (it) {
                        WindSpeedUnit.METER_PER_SEC -> stringResource(R.string.settings_meter_sec)
                        WindSpeedUnit.MILES_PER_HOUR -> stringResource(R.string.settings_mph)
                    }
                },
                onSelect = {
                    viewModel.setWindSpeedUnit(it)
                }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_language)) {
            StormSegmentedRow(
                options = Language.entries,
                selected = prefs.language,
                label = {
                    when (it) {
                        Language.ENGLISH -> stringResource(R.string.settings_english)
                        Language.ARABIC -> stringResource(R.string.settings_arabic)
                    }
                },
                onSelect = {
                    viewModel.setLanguage(it)
                }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_theme)) {
            StormSegmentedRow(
                options = ThemeMode.entries,
                selected = prefs.themeMode,
                label = {
                    when (it) {
                        ThemeMode.LIGHT -> stringResource(R.string.settings_light)
                        ThemeMode.DARK -> stringResource(R.string.settings_dark)
                    }
                },
                onSelect = {
                    viewModel.setThemeMode(it)
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> StormSegmentedRow(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selected == option
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                selected = isSelected,
                onClick = { onSelect(option) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outline
                ),
                label = {
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            )
        }
    }
}