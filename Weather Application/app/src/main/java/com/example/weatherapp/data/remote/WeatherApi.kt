package com.example.weatherapp.data.remote

data class WeatherDto(
    val temperature: String,
    val description: String,
    val humidity: String,
    val wind: String,
    val feelsLike: String
)
