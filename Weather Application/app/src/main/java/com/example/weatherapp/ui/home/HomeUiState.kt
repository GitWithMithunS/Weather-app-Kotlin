package com.example.weatherapp.ui.home

data class HomeUiState(
    val username: String = "",
    val city: String = "",
    val temperature: String = "--",
    val description: String = "",
    val humidity: String = "--",
    val windSpeed: String = "--",
    val feelsLike: String = "--",
    val forecast: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFahrenheit: Boolean = false // Track unit preference
)