package com.example.weatherapp.model

data class HourlyWeather(
    val time: String,
    val temp: Int,
    val icon: String
)