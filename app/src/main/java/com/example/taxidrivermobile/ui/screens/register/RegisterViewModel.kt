package com.example.taxidrivermobile.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxidrivermobile.data.model.RegisterRequest
import com.example.taxidrivermobile.data.model.Station
import com.example.taxidrivermobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val licenseNumber: String = "",
    val vehiclePlate: String = "",
    val selectedStation: Station? = null,
    val stations: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingStations: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false
)

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingStations = true)

            repository.getStations()
                .onSuccess { stations ->
                    _uiState.value = _uiState.value.copy(
                        stations = stations,
                        isLoadingStations = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingStations = false,
                        error = "Duraklar yüklenemedi: ${error.message}"
                    )
                }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, error = null)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, error = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onLicenseNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(licenseNumber = value, error = null)
    }

    fun onVehiclePlateChange(value: String) {
        _uiState.value = _uiState.value.copy(vehiclePlate = value, error = null)
    }

    fun onStationSelect(station: Station) {
        _uiState.value = _uiState.value.copy(selectedStation = station, error = null)
    }

    fun register() {
        val state = _uiState.value

        // Validasyon
        when {
            state.fullName.isBlank() -> {
                _uiState.value = state.copy(error = "Ad soyad giriniz")
                return
            }
            state.phone.isBlank() -> {
                _uiState.value = state.copy(error = "Telefon numarası giriniz")
                return
            }
            state.password.length < 6 -> {
                _uiState.value = state.copy(error = "Şifre en az 6 karakter olmalı")
                return
            }
            state.licenseNumber.isBlank() -> {
                _uiState.value = state.copy(error = "Sürücü belgesi numarası giriniz")
                return
            }
            state.vehiclePlate.isBlank() -> {
                _uiState.value = state.copy(error = "Plaka giriniz")
                return
            }
            state.selectedStation == null -> {
                _uiState.value = state.copy(error = "Durak seçiniz")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            val request = RegisterRequest(
                fullName = state.fullName,
                phone = state.phone,
                email = state.email.takeIf { it.isNotBlank() },
                password = state.password,
                licenseNumber = state.licenseNumber,
                vehiclePlate = state.vehiclePlate,
                station = state.selectedStation!!._id
            )

            repository.register(request)
                .onSuccess {
                    _uiState.value = state.copy(
                        isLoading = false,
                        isRegistered = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = error.message ?: "Kayıt başarısız"
                    )
                }
        }
    }
}