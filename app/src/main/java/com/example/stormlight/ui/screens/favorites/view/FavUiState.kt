package com.example.stormlight.ui.screens.favorites.view

import com.example.stormlight.data.model.FavWeather
import com.example.stormlight.data.model.UserPrefrences

sealed class FavUiState {
    data class Success(val favorites: List<FavWeather>, val prefs: UserPrefrences) : FavUiState()
    data object Loading : FavUiState()
    data class Error(val message: String) : FavUiState()
}

sealed class FavoritesUiEvent {
    data class ShowSnackbar(val message: String) : FavoritesUiEvent()
    data class NavigateToDetail(val loc: String) : FavoritesUiEvent()
    data object NavigateToMap : FavoritesUiEvent()
}