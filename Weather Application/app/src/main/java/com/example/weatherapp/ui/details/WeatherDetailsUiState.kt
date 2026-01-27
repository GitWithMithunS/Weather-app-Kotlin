package com.example.weatherapp.ui.details

data class WeatherDetailsUiState(
    val city: String = "",
    val temperature: String = "",
    val description: String = "",
    val humidity: String = "",
    val wind: String = "",
    val feelsLike: String = "",
    val isLoading: Boolean = true
)
