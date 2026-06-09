package com.example.gamevault.core.repositories

import com.example.gamevault.core.ResponseService
import com.example.gamevault.onboarding.personal.model.UserProfile

/**
 * Contrato para persistir y recuperar el perfil del usuario en Firestore.
 */
interface UserService {
    suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit>
    suspend fun getUserInfo(uid: String): ResponseService<UserProfile>
}
