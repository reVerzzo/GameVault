package com.example.gamevault.core.network

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.model.GameDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio que consume la API de RAWG y mapea el resultado a
 * ResponseService para la capa de presentación.
 */
class GameRepository : GameService {
    private val api = ApiClient.rawgApi

    override suspend fun listGames(
        search: String?,
        page: Int,
        pageSize: Int,
        ordering: String?
    ): ResponseService<List<Game>> = withContext(Dispatchers.IO) {
        apiCall {
            api.listGames(
                apiKey = ApiClient.API_KEY,
                page = page,
                pageSize = pageSize,
                search = search,
                ordering = ordering
            )
        }.fold(
            onSuccess = { ResponseService.Success(it.results) },
            onFailure = { ResponseService.Error(messageFrom(it)) }
        )
    }

    override suspend fun getGameDetail(id: String): ResponseService<GameDetail> =
        withContext(Dispatchers.IO) {
            apiCall {
                api.getGame(id = id, apiKey = ApiClient.API_KEY)
            }.fold(
                onSuccess = { ResponseService.Success(it) },
                onFailure = { ResponseService.Error(messageFrom(it)) }
            )
        }
}
