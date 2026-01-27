package com.example.weatherapp.model

data class DailyWeather(
    val day: String,
    val condition: String,
    val maxTemp: Int,
    val minTemp: Int,
    val icon: String
)
