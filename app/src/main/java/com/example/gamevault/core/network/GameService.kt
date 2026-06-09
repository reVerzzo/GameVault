package com.example.gamevault.core.network

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.model.GameDetail

/**
 * Abstracción del origen de datos de juegos para que los ViewModels no
 * dependan directamente de Retrofit.
 */
interface GameService {
    suspend fun listGames(
        search: String? = null,
        page: Int = 1,
        pageSize: Int = 20,
        ordering: String? = "-added"
    ): ResponseService<List<Game>>

    suspend fun getGameDetail(id: String): ResponseService<GameDetail>
}
