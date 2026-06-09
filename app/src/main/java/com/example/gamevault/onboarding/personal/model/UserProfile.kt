package com.example.gamevault.onboarding.personal.model

/**
 * Perfil del usuario que se guarda y se lee de Firestore en
 * users/{uid}. El constructor vacío es requerido por Firestore para
 * deserializar el documento.
 */
data class UserProfile(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val email: String = ""
) {
    fun fullName(): String = "$firstName $lastName".trim()
}
