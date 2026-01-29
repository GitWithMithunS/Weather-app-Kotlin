package com.example.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

// Main Weather Response
data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class ForecastItem(
    val dt: Long,
    @SerializedName("main")
    val mainWeather: MainWeather,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    @SerializedName("sys")
    val sysInfo: SysInfo,
    @SerializedName("dt_txt")
    val dateText: String
)

data class MainWeather(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

data class Rain(
    @SerializedName("3h")
    val threeHour: Double?
)

data class SysInfo(
    val pod: String
)

// Current Weather Response (for single city)
data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    @SerializedName("main")
    val mainWeather: MainWeather,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    @SerializedName("sys")
    val sysInfo: CurrentSysInfo,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class CurrentSysInfo(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)