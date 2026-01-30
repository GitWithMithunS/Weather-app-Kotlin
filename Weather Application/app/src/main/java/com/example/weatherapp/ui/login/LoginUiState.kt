package com.example.weatherapp.ui.login

import androidx.compose.ui.text.input.TextFieldValue

data class LoginUiState(
    val username: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue(),
    val city: TextFieldValue = TextFieldValue(),
    val confirmPassword: TextFieldValue = TextFieldValue(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterMode: Boolean = false,  // Toggle between login and register

    // Validation errors
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val cityError: String? = null,
)
