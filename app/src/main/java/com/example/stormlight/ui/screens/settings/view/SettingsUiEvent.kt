package com.example.stormlight.ui.screens.settings.view

sealed class SettingsUiEvent {
    data object NavigateToMap : SettingsUiEvent()
}