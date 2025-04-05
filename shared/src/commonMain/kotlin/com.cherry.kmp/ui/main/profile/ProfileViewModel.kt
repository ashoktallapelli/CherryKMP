package com.cherry.kmp.ui.main.profile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.domain.model.UserProfile
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import com.cherry.kmp.ui.component.UIComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileState(
    val id: Long = 0L,
    val name: String = "",
    val email: String = "",
    val image: ImageBitmap? = null,
    val permissionDialog: UIComponentState = UIComponentState.Hide,
)

/**
 * A ViewModel that manages the user's profile data.
 *
 * This ViewModel is responsible for loading, saving, and updating the user's profile information.
 * It exposes the current profile state as a [StateFlow] of [UserProfileState].
 *
 * @property localDataUseCase The use case for accessing and storing local data.
 */

class ProfileViewModel(private val localDataUseCase: LocalDataUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updateProfileImage(image: ImageBitmap?) {
        _uiState.update { it.copy(image = image) }
    }

    fun updatePermissionDialogState(uiState: UIComponentState) {
        _uiState.update { it.copy(permissionDialog = uiState) }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            localDataUseCase.getAllUserProfiles().firstOrNull()?.let { profile ->
                _uiState.update {
                    it.copy(
                        name = profile.name,
                        image = profile.image,
                        email = profile.email,
                        id = profile.id
                    )
                }
            }
        }
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            val currentUiState = uiState.value
            val profile = UserProfile(
                id = currentUiState.id,
                name = currentUiState.name,
                email = currentUiState.email,
                image = currentUiState.image
            )
            localDataUseCase.saveUserProfile(profile)
        }
    }
}