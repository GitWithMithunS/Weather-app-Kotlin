package com.example.weatherapp.model

data class ForecastItem(
    val date: String,
    val day: String,
    val description: String,
    val maxTemp: String,
    val minTemp: String,
    val icon: String
)
