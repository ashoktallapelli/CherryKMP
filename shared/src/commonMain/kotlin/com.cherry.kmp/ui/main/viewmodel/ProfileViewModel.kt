package com.cherry.kmp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Uri
import com.cherry.kmp.domain.model.UserProfile
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import com.cherry.kmp.ui.component.UIComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileState(
    val id: Long = 0L, val name: String = "", val email: String = "", val image: String? = null,
    val permissionDialog: UIComponentState = UIComponentState.Hide,
)

class ProfileViewModel(private val localDataUseCase: LocalDataUseCase) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state

    fun setName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun setProfileImageUri(uri: Uri?) {
        _state.update { it.copy(image = uri.toString()) }
    }

    fun setPermissionDialogState(uiState: UIComponentState) {
        _state.update { it.copy(permissionDialog = uiState) }
    }

    fun load() {
        viewModelScope.launch {
            val profiles = localDataUseCase.getAllUserProfiles()

            if (profiles.isNotEmpty()) {
                val profile = profiles.first()

                _state.update {
                    it.copy(
                        name = profile.name, image = null, email = profile.email, id = profile.id
                    )
                }

//                setProfileImageUri(profile.image)
            }

        }
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            val state = state.value
            val profile = UserProfile(
                id = state.id, name = state.name, email = state.email, image = state.image
            )
            localDataUseCase.saveUserProfile(profile)
        }
    }
}