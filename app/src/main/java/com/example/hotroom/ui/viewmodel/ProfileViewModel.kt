package com.example.hotroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotroom.data.model.Profile
import com.example.hotroom.data.repository.AuthRepository
import com.example.hotroom.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val email: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = authRepository.getCurrentUserId() ?: return
        val email = authRepository.getCurrentUserEmail() ?: ""
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, email = email)
            val result = profileRepository.getProfile(userId)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    profile = result.getOrNull()
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun updateNotificationSettings(notifyWatering: Boolean, notifyTemperature: Boolean) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            profileRepository.updateNotificationSettings(userId, notifyWatering, notifyTemperature)
            // Lokalni yangilash
            _uiState.value = _uiState.value.copy(
                profile = _uiState.value.profile?.copy(
                    notifyWatering = notifyWatering,
                    notifyTemperature = notifyTemperature
                )
            )
        }
    }

    fun updateProfile(name: String, location: String?, type: String?, area: Float?) {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val updates = mutableMapOf<String, Any?>()
            updates["name"] = name
            if (location != null) updates["greenhouse_location"] = location
            if (type != null) updates["greenhouse_type"] = type
            if (area != null) updates["greenhouse_area"] = area

            val result = profileRepository.updateProfile(userId, updates)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isSaving = false,
                    profile = _uiState.value.profile?.copy(
                        name = name,
                        greenhouseLocation = location ?: _uiState.value.profile?.greenhouseLocation,
                        greenhouseType = type ?: _uiState.value.profile?.greenhouseType,
                        greenhouseArea = area ?: _uiState.value.profile?.greenhouseArea
                    )
                )
            } else {
                _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun getInitials(): String {
        val name = _uiState.value.profile?.name ?: return "?"
        return name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .ifEmpty { "?" }
    }
}
