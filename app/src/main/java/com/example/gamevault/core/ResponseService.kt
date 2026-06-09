package com.example.gamevault.core

/**
 * Envoltura de estado para exponer el resultado de una operación
 * (red o Firebase) hacia los ViewModels y la UI.
 */
sealed class ResponseService<out T> {
    data class Success<T>(val data: T) : ResponseService<T>()
    data class Error(val error: String) : ResponseService<Nothing>()
    object Loading : ResponseService<Nothing>()
}
