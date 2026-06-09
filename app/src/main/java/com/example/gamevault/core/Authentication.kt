package com.example.gamevault.core

import com.google.firebase.auth.FirebaseUser

/**
 * Contrato de autenticación con Firebase Auth.
 */
interface Authentication {
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestSignUp(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestPasswordReset(email: String): ResponseService<Unit>
}
