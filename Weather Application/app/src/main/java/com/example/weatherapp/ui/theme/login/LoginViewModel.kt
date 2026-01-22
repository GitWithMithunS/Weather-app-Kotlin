package com.example.weatherapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _uiState.value = LoginUiState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val user = repository.getUserByEmail(email)

            if (user != null && user.password == pass) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Invalid email or password")
            }
        }
    }
}