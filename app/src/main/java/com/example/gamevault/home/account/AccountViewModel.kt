package com.example.gamevault.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.repositories.FavoritesRepository
import com.example.gamevault.core.repositories.FavoritesService
import com.example.gamevault.core.repositories.UserRepository
import com.example.gamevault.core.repositories.UserService
import com.example.gamevault.onboarding.personal.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userService: UserService = UserRepository(),
    private val favoritesService: FavoritesService = FavoritesRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _profileState = MutableStateFlow<ResponseService<UserProfile>?>(null)
    val profileState: StateFlow<ResponseService<UserProfile>?> = _profileState.asStateFlow()

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount.asStateFlow()

    /** Lee el perfil guardado en el registro desde Firestore. */
    fun loadProfile() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _profileState.value = ResponseService.Error("Sesión no válida")
            return
        }
        viewModelScope.launch {
            _profileState.value = ResponseService.Loading
            _profileState.value = userService.getUserInfo(uid)
        }
    }

    /** Cuenta los juegos guardados para la tarjeta de estadísticas. */
    fun loadStats() {
        viewModelScope.launch {
            val result = favoritesService.getFavorites()
            if (result is ResponseService.Success) {
                _favoritesCount.value = result.data.size
            }
        }
    }

    fun logout() = authRepository.signOut()
}
