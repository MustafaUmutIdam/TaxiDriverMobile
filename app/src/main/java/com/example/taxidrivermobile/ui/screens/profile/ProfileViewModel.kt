package com.example.taxidrivermobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxidrivermobile.data.model.Driver
import com.example.taxidrivermobile.data.model.UpdateProfileRequest
import com.example.taxidrivermobile.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val driver: Driver? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdating: Boolean = false,
    val isChangingPassword: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val showEditDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showLogoutDialog: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getProfile()
                .onSuccess { driver ->
                    _uiState.value = _uiState.value.copy(
                        driver = driver,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateProfile(
        fullName: String? = null,
        email: String? = null,
        phone: String? = null,
        licenseNumber: String? = null,
        vehiclePlate: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

            val request = UpdateProfileRequest(
                fullName = fullName,
                email = email,
                phone = phone,
                licenseNumber = licenseNumber,
                vehiclePlate = vehiclePlate
            )

            repository.updateProfile(request)
                .onSuccess { 
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        showEditDialog = false
                    )
                    loadProfile()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = error.message
                    )
                }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChangingPassword = true, error = null)

            repository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        showPasswordDialog = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateStatus(status: String) {
        val driverId = _uiState.value.driver?._id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStatus = true, error = null)

            repository.updateStatus(driverId, status)
                .onSuccess { statusUpdate ->
                    val currentDriver = _uiState.value.driver
                    val newDriver = currentDriver?.copy(status = statusUpdate.status)
                    _uiState.value = _uiState.value.copy(
                        driver = newDriver,
                        isUpdatingStatus = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isUpdatingStatus = false,
                        error = error.message
                    )
                }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLogoutComplete()
        }
    }

    fun showEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = true)
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false)
    }

    fun showPasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = true)
    }

    fun hidePasswordDialog() {
        _uiState.value = _uiState.value.copy(showPasswordDialog = false)
    }

    fun showLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = true)
    }

    fun hideLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}