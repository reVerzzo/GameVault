package com.example.gamevault.onboarding.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.repositories.UserRepository
import com.example.gamevault.core.repositories.UserService
import com.example.gamevault.onboarding.personal.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    private val service: UserService = UserRepository()
) : ViewModel() {

    private val _saveState = MutableStateFlow<ResponseService<Unit>?>(null)
    val saveState: StateFlow<ResponseService<Unit>?> = _saveState.asStateFlow()

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _saveState.value = ResponseService.Loading
            _saveState.value = service.saveUserInfo(profile)
        }
    }
}
