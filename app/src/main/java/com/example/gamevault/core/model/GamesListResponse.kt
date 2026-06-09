package com.example.gamevault.core.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta paginada de GET /games.
 */
data class GamesListResponse(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("next") val next: String? = null,
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("results") val results: List<Game> = emptyList()
)
