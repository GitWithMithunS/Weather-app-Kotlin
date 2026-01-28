package com.example.weatherapp.ui.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val city: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterMode: Boolean = false,  // Toggle between login and register

    // Validation errors
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val cityError: String? = null,
)