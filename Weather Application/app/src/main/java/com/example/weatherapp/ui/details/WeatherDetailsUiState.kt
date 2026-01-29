package com.example.weatherapp.ui.details

data class WeatherDetailsUiState(
    val city: String = "",
    val temperature: String = "",
    val description: String = "",
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
    val error: String? = null,
    val isFahrenheit: Boolean = false // New: Track unit
)

data class HourlyForecast(
    val time: String,
    val temperature: String,
    val rawTemp: Double, // New: Store raw number
    val description: String,
    val icon: String
)

data class DailyForecast(
    val day: String,
    val maxTemp: String,
    val minTemp: String,
    val rawMax: Double, // New: Store raw number
    val rawMin: Double, // New: Store raw number
    val description: String,
    val icon: String
)