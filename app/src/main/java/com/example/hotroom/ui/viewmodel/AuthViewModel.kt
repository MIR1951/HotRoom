package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = ""
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { status ->
                when (status) {
                    is io.github.jan.supabase.auth.status.SessionStatus.Authenticated -> {
                        _uiState.value = _uiState.value.copy(isLoggedIn = true, isLoading = false)
                    }
                    is io.github.jan.supabase.auth.status.SessionStatus.NotAuthenticated -> {
                        _uiState.value = _uiState.value.copy(isLoggedIn = false, isLoading = false)
                    }
                    else -> {
                        // For Initializing, LoadingFromStorage, NetworkError, etc
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Email va parolni kiriting")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signIn(state.email, state.password)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Xatolik yuz berdi"
                )
            }
        }
    }

    fun signUp() {
        val state = _uiState.value
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Barcha maydonlarni to'ldiring")
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Parollar mos kelmaydi")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signUp(state.email, state.password, state.name)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Xatolik yuz berdi"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }

    fun getCurrentUserId(): String? = authRepository.getCurrentUserId()

    fun getCurrentUserEmail(): String? = authRepository.getCurrentUserEmail()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
