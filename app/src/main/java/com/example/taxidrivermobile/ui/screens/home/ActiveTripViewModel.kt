package com.example.taxidrivermobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxidrivermobile.data.model.Trip
import com.example.taxidrivermobile.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveTripUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAccepting: Boolean = false,
    val isRejecting: Boolean = false,
    val isStarting: Boolean = false,
    val isCompleting: Boolean = false
)

@HiltViewModel
class ActiveTripViewModel @Inject constructor(
    private val repository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveTripUiState())
    val uiState: StateFlow<ActiveTripUiState> = _uiState

    init {
        startPolling()
    }

    // 5 saniyede bir trip kontrol et
    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                fetchActiveTrip()
                delay(5000) // 5 saniye
            }
        }
    }

    private fun fetchActiveTrip() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getActiveTrip()
                .onSuccess { trip ->
                    _uiState.value = _uiState.value.copy(
                        trip = trip,
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

    fun acceptTrip() {
        val tripId = _uiState.value.trip?._id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAccepting = true, error = null)

            repository.acceptTrip(tripId)
                .onSuccess { trip ->
                    _uiState.value = _uiState.value.copy(
                        trip = trip,
                        isAccepting = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isAccepting = false,
                        error = error.message
                    )
                }
        }
    }

    fun rejectTrip() {
        val tripId = _uiState.value.trip?._id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRejecting = true, error = null)

            repository.rejectTrip(tripId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        trip = null,
                        isRejecting = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isRejecting = false,
                        error = error.message
                    )
                }
        }
    }

    fun startTrip() {
        val tripId = _uiState.value.trip?._id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isStarting = true, error = null)

            repository.startTrip(tripId)
                .onSuccess { trip ->
                    _uiState.value = _uiState.value.copy(
                        trip = trip,
                        isStarting = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isStarting = false,
                        error = error.message
                    )
                }
        }
    }

    fun completeTrip(actualFare: Double) {
        val tripId = _uiState.value.trip?._id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true, error = null)

            repository.completeTrip(tripId, actualFare)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        trip = null,
                        isCompleting = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isCompleting = false,
                        error = error.message
                    )
                }
        }
    }
}