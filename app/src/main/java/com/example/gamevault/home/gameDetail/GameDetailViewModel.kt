package com.example.gamevault.home.gameDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.FavoriteGame
import com.example.gamevault.core.model.GameDetail
import com.example.gamevault.core.network.GameRepository
import com.example.gamevault.core.network.GameService
import com.example.gamevault.core.repositories.FavoritesRepository
import com.example.gamevault.core.repositories.FavoritesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val service: GameService = GameRepository(),
    private val favorites: FavoritesService = FavoritesRepository()
) : ViewModel() {

    private val _detailState = MutableStateFlow<ResponseService<GameDetail>?>(null)
    val detailState: StateFlow<ResponseService<GameDetail>?> = _detailState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Mensaje puntual para mostrar en Snackbar tras un toggle.
    private val _favoriteMessage = MutableStateFlow<String?>(null)
    val favoriteMessage: StateFlow<String?> = _favoriteMessage.asStateFlow()

    fun loadDetail(gameId: Int) {
        viewModelScope.launch {
            _detailState.value = ResponseService.Loading
            _detailState.value = service.getGameDetail(gameId.toString())
        }
        checkFavorite(gameId)
    }

    private fun checkFavorite(gameId: Int) {
        viewModelScope.launch {
            val result = favorites.isFavorite(gameId)
            if (result is ResponseService.Success) {
                _isFavorite.value = result.data
            }
        }
    }

    /** Alterna el juego en la colección de favoritos de Firebase. */
    fun toggleFavorite(detail: GameDetail) {
        viewModelScope.launch {
            val result = if (_isFavorite.value) {
                favorites.removeFavorite(detail.id)
            } else {
                favorites.addFavorite(FavoriteGame.fromDetail(detail))
            }
            when (result) {
                is ResponseService.Success -> {
                    val nowFavorite = !_isFavorite.value
                    _isFavorite.value = nowFavorite
                    _favoriteMessage.value =
                        if (nowFavorite) "Agregado a tu lista" else "Eliminado de tu lista"
                }
                is ResponseService.Error -> _favoriteMessage.value = result.error
                else -> {}
            }
        }
    }

    fun consumeMessage() {
        _favoriteMessage.value = null
    }
}
