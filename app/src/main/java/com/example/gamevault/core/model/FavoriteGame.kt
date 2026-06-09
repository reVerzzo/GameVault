package com.example.gamevault.core.model

/**
 * Juego guardado por el usuario en la colección favoritos de Firestore
 * (users/{uid}/favoritos/{gameId}). El constructor vacío con valores por
 * defecto es necesario para que Firestore deserialice el documento.
 */
data class FavoriteGame(
    val gameId: Int = 0,
    val name: String = "",
    val backgroundImage: String = "",
    val rating: Double = 0.0,
    val genres: String = "",
    val released: String = "",
    val status: String = "POR_JUGAR",
    val addedAt: Long = 0L
) {
    companion object {
        /** Crea un favorito a partir del detalle del juego. */
        fun fromDetail(detail: GameDetail): FavoriteGame = FavoriteGame(
            gameId = detail.id,
            name = detail.name,
            backgroundImage = detail.backgroundImage.orEmpty(),
            rating = detail.rating,
            genres = detail.genres.take(3).joinToString(" · ") { it.name },
            released = detail.released.orEmpty(),
            status = "POR_JUGAR",
            addedAt = System.currentTimeMillis()
        )
    }
}
