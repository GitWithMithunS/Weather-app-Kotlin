package com.example.weatherapp.ui.home

import com.example.weatherapp.model.ForecastItem

data class HomeUiState(
    val title: String = "Today",
    val username: String = "",
    val city: String = "",
    val temperature: String = "--",
    val description: String = "",
    val icon: String = "",
    val humidity: String = "--",
    val windSpeed: String = "--",
    val feelsLike: String = "--",
    val forecast: List<ForecastItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
