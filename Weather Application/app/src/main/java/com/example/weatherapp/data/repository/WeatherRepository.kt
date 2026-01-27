package com.example.weatherapp.data.repository

class WeatherRepository {

    fun getWeatherDetails(city: String): WeatherDetails {
        return WeatherDetails(
            cityName = city,
            humidity = 75,
            dewPoint = "18°C",
            windSpeed = 15,
            windGust = 25,
            precipitationChance = 20,
            precipitationDesc = "Light rain expected",
            feelsLike = 26,
            sunrise = "05:30 AM",
            sunset = "07:45 PM",
            hourly = listOf(
                HourlyWeather("10 AM", 25, ""),
                HourlyWeather("11 AM", 26, ""),
                HourlyWeather("12 PM", 27, "")
            ),
            daily = listOf(
                DailyWeather("Today", "Sunny", 28, 20, ""),
                DailyWeather("Tue", "Partly Cloudy", 27, 19, "")
            )
        )
    }
}


data class WeatherDetails(
    val cityName: String,
    val humidity: Int,
    val dewPoint: String,
    val windSpeed: Int,
    val windGust: Int,
    val precipitationChance: Int,
    val precipitationDesc: String,
    val feelsLike: Int,
    val sunrise: String,
    val sunset: String,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)

data class HourlyWeather(
    val time: String,
    val temp: Int,
    val icon: String
)

data class DailyWeather(
    val day: String,
    val condition: String,
    val maxTemp: Int,
    val minTemp: Int,
    val icon: String
)
