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

        if (state.username.isBlank() || state.password.isBlank() || state.city.isBlank()) {
            _uiState.value = state.copy(error = "All fields are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            // ✅ Uses saveAndLoginUser to mark the user as active
            userRepository.saveAndLoginUser(
                username = state.username,
                password = state.password,
                city = state.city
            )

            _uiState.value = state.copy(isLoading = false)
            onSuccess()
        }
    }
}
