package com.example.gamevault.home.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.network.GameRepository
import com.example.gamevault.core.network.GameService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class GamesViewModel(
    private val service: GameService = GameRepository()
) : ViewModel() {

    private val _gamesState = MutableStateFlow<ResponseService<List<Game>>?>(null)
    val gamesState: StateFlow<ResponseService<List<Game>>?> = _gamesState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val initObserver = viewModelScope.launch {
        // Debounce de 400ms para no saturar la API mientras se escribe.
        queryFlow
            .debounce(400)
            .distinctUntilChanged()
            .collect { query -> fetch(query) }
    }

    /** Lo llama el buscador en cada cambio de texto. */
    fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    /** Pull-to-refresh: recarga con la búsqueda activa. */
    fun refresh() {
        viewModelScope.launch { fetch(queryFlow.value) }
    }

    private suspend fun fetch(query: String) {
        _gamesState.value = ResponseService.Loading
        val cleaned = query.trim().takeIf { it.isNotEmpty() }
        _gamesState.value = service.listGames(
            search = cleaned,
            pageSize = 20,
            ordering = if (cleaned == null) "-added" else null
        )
    }
}
