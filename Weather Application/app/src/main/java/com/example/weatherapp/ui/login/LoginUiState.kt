package com.example.weatherapp.ui.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val city: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
