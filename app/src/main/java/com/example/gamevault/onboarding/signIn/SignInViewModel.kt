package com.example.gamevault.onboarding.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.Authentication
import com.example.gamevault.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val auth: Authentication = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val loginState: StateFlow<ResponseService<FirebaseUser>?> = _loginState.asStateFlow()

    private val _resetState = MutableStateFlow<ResponseService<Unit>?>(null)
    val resetState: StateFlow<ResponseService<Unit>?> = _resetState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ResponseService.Loading
            _loginState.value = auth.requestLogin(email.trim(), password)
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetState.value = ResponseService.Loading
            _resetState.value = auth.requestPasswordReset(email.trim())
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}
