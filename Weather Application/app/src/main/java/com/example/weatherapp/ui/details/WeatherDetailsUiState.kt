package com.example.weatherapp.ui.details

data class WeatherDetailsUiState(
    val title: String = "",
    val city: String = "",
    val temperature: String = "",
    val description: String = "",
    val icon: String = "",
    val humidity: String = "",
    val wind: String = "",
    val feelsLike: String = "",
    val visibility: String = "",
    val pressure: String = "",
    val cloudiness: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val hourlyData: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class HourlyForecast(
    val time: String,
    val temperature: String,
    val description: String,
    val icon: String
)

data class DailyForecast(
    val day: String,
    val maxTemp: String,
    val minTemp: String,
    val description: String,
    val icon: String,
    val date: String
)