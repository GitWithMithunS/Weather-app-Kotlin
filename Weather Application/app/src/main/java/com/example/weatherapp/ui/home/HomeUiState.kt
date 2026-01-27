package com.example.weatherapp.ui.home

data class HomeUiState(
    val username: String = "",
    val city: String = "",
    val temperature: String = "--",
    val description: String = "",
    val forecast: List<String> = emptyList(),
    val isLoading: Boolean = true
)
