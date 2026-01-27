package com.example.weatherapp.ui.home

data class ForecastDay(
    val day: String,
    val date: String,
    val temp: String
)

data class HomeUiState(
    val city: String = "New York, USA",
    val temperature: String = "25°C",
    val condition: String = "Partly Cloudy",
    val forecast: List<ForecastDay> = listOf(
        ForecastDay("Mon", "22 Jan", "28° / 18°"),
        ForecastDay("Tue", "23 Jan", "24° / 16°"),
        ForecastDay("Wed", "24 Jan", "22° / 15°"),
        ForecastDay("Thu", "25 Jan", "25° / 17°"),
        ForecastDay("Fri", "26 Jan", "27° / 18°"),
        ForecastDay("Sat", "27 Jan", "29° / 19°"),
        ForecastDay("Sun", "28 Jan", "30° / 20°"),
        ForecastDay("Mon", "29 Jan", "28° / 18°"),
        ForecastDay("Tue", "30 Jan", "26° / 17°"),
        ForecastDay("Wed", "31 Jan", "25° / 16°")
    )
)
