package com.example.weatherapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onCityChange(value: String) {
        _uiState.value = _uiState.value.copy(city = value)
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validation
        if (state.username.isBlank()) {
            _uiState.value = state.copy(error = "Username is required")
            return
        }

        if (state.password.isBlank()) {
            _uiState.value = state.copy(error = "Password is required")
            return
        }

        if (state.city.isBlank()) {
            _uiState.value = state.copy(error = "Default city is required")
            return
        }

        if (state.password.length < 4) {
            _uiState.value = state.copy(error = "Password must be at least 4 characters")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                // Try to save and login user
                val success = userRepository.saveAndLoginUser(
                    username = state.username,
                    password = state.password,
                    city = state.city
                )

                if (success) {
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Failed to login. Please try again."
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
}