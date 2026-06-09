package com.example.gamevault.home.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.FavoriteGame
import com.example.gamevault.core.repositories.FavoritesRepository
import com.example.gamevault.core.repositories.FavoritesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val service: FavoritesService = FavoritesRepository()
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<ResponseService<List<FavoriteGame>>?>(null)
    val favoritesState: StateFlow<ResponseService<List<FavoriteGame>>?> =
        _favoritesState.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = ResponseService.Loading
            _favoritesState.value = service.getFavorites()
        }
    }

    fun removeFavorite(gameId: Int) {
        viewModelScope.launch {
            val result = service.removeFavorite(gameId)
            if (result is ResponseService.Success) {
                loadFavorites()
            }
        }
    }
}
