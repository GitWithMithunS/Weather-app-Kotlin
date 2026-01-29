package com.example.weatherapp.modelyy

data class DailyWeather(
    val day: String,
    val condition: String,
    val maxTemp: Int,
    val minTemp: Int,
    val icon: String
)
