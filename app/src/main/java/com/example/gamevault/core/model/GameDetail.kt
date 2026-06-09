package com.example.gamevault.core.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo del detalle de un juego (GET /games/{id}). Contiene los campos
 * más útiles para la pantalla de detalle descrita en el documento.
 */
data class GameDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("description_raw") val descriptionRaw: String? = null,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("background_image_additional") val backgroundImageAdditional: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("ratings_count") val ratingsCount: Int = 0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("playtime") val playtime: Int = 0,
    @SerializedName("platforms") val platforms: List<PlatformWrapper> = emptyList(),
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("developers") val developers: List<Company> = emptyList(),
    @SerializedName("publishers") val publishers: List<Company> = emptyList(),
    @SerializedName("esrb_rating") val esrbRating: EsrbRating? = null
) {
    fun developerName(): String = developers.firstOrNull()?.name ?: "Desconocido"
}

data class Company(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String? = null
)

data class EsrbRating(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String? = null
)
