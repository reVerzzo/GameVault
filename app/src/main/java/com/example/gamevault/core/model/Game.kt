package com.example.gamevault.core.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de un juego tal como llega en el listado de RAWG (GET /games).
 */
data class Game(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("platforms") val platforms: List<PlatformWrapper> = emptyList()
) {
    /** Hasta 3 géneros separados por " · " para pintar en la card. */
    fun genresText(): String =
        genres.take(3).joinToString(" · ") { it.name }

    /** Hasta 3 plataformas separadas por " · " para pintar en la card. */
    fun platformsText(): String =
        platforms.take(3).joinToString(" · ") { it.platform.name }
}

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String? = null
)

data class PlatformWrapper(
    @SerializedName("platform") val platform: Platform
)

data class Platform(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String? = null
)
