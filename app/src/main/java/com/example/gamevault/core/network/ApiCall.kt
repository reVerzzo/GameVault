package com.example.gamevault.core.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException

/** Data class para parsear el body de error que devuelve RAWG. */
data class RawgErrorBody(
    @SerializedName("detail") val detail: String? = null
)

/** Excepción tipada con el código HTTP, más fácil de manejar en la UI. */
class ApiException(
    message: String,
    val statusCode: Int
) : Exception(message)

private val gson = Gson()

/**
 * Envuelve una llamada a la API y la convierte en un Result<T>.
 * Traduce HttpException en una ApiException con su código y mensaje.
 */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    val raw = e.response()?.errorBody()?.string()
    val message = try {
        gson.fromJson(raw, RawgErrorBody::class.java)?.detail
    } catch (_: Exception) {
        null
    } ?: "Error HTTP ${e.code()}"
    Result.failure(ApiException(message, e.code()))
} catch (e: Exception) {
    Result.failure(e)
}

/** Traduce una excepción de red en un mensaje legible para el usuario. */
fun messageFrom(e: Throwable): String = when {
    e is ApiException && e.statusCode == 404 -> "No encontrado"
    e is ApiException && e.statusCode == 429 -> "Demasiadas peticiones, espera un momento"
    e is ApiException && e.statusCode == 401 -> "API key inválida"
    e is ApiException -> e.message ?: "Error del servidor"
    else -> "Sin conexión o error inesperado"
}
