package com.example.gamevault.core.network

import com.example.gamevault.core.model.GameDetail
import com.example.gamevault.core.model.GamesListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato HTTP de los endpoints de RAWG que consume la app.
 */
interface RawgApi {

    @GET("games")
    suspend fun listGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("search") search: String? = null,
        @Query("ordering") ordering: String? = null
    ): GamesListResponse

    @GET("games/{id}")
    suspend fun getGame(
        @Path("id") id: String,
        @Query("key") apiKey: String
    ): GameDetail
}
