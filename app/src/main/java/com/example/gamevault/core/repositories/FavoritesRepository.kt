package com.example.gamevault.core.repositories

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.FavoriteGame
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repositorio del CRUD de favoritos en la colección
 * users/{uid}/favoritos de Firestore. Cada documento usa el id del juego
 * como identificador para evitar duplicados.
 */
class FavoritesRepository : FavoritesService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun favoritesCollection() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("favoritos")
    }

    override suspend fun addFavorite(
        favorite: FavoriteGame
    ): ResponseService<Unit> = withContext(Dispatchers.IO) {
        val collection = favoritesCollection()
            ?: return@withContext ResponseService.Error("Sesión no válida")
        try {
            collection.document(favorite.gameId.toString())
                .set(favorite)
                .await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo guardar en favoritos: ${e.localizedMessage}")
        }
    }

    override suspend fun removeFavorite(
        gameId: Int
    ): ResponseService<Unit> = withContext(Dispatchers.IO) {
        val collection = favoritesCollection()
            ?: return@withContext ResponseService.Error("Sesión no válida")
        try {
            collection.document(gameId.toString()).delete().await()
            ResponseService.Success(Unit)
        } catch (e: Exception) {
            ResponseService.Error("No se pudo eliminar de favoritos: ${e.localizedMessage}")
        }
    }

    override suspend fun isFavorite(
        gameId: Int
    ): ResponseService<Boolean> = withContext(Dispatchers.IO) {
        val collection = favoritesCollection()
            ?: return@withContext ResponseService.Error("Sesión no válida")
        try {
            val snapshot = collection.document(gameId.toString()).get().await()
            ResponseService.Success(snapshot.exists())
        } catch (e: Exception) {
            ResponseService.Error("No se pudo consultar favoritos: ${e.localizedMessage}")
        }
    }

    override suspend fun getFavorites(): ResponseService<List<FavoriteGame>> =
        withContext(Dispatchers.IO) {
            val collection = favoritesCollection()
                ?: return@withContext ResponseService.Error("Sesión no válida")
            try {
                val snapshot = collection
                    .orderBy("addedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val favorites = snapshot.toObjects(FavoriteGame::class.java)
                ResponseService.Success(favorites)
            } catch (e: Exception) {
                ResponseService.Error("No se pudieron leer los favoritos: ${e.localizedMessage}")
            }
        }
}
