package com.example.weatherapp.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.room.UserEntity
import com.example.weatherapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState: StateFlow<SignupUiState> = _uiState

    fun register(userName: String, email: String, pass: String) {
        if (userName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _uiState.value = SignupUiState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignupUiState.Loading
            try {
                val user = UserEntity(userName = userName, email = email, password = pass)
                repository.registerUser(user)
                _uiState.value = SignupUiState.Success
            } catch (e: Exception) {
                _uiState.value = SignupUiState.Error("Registration failed: ${e.message}")
            }
        }
    }
}