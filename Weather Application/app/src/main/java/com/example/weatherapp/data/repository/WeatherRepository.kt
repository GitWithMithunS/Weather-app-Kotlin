package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.remote.api.OpenWeatherApiService
import com.example.weatherapp.data.remote.model.CurrentWeatherResponse
import com.example.weatherapp.data.remote.model.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: OpenWeatherApiService
) {

    //my actual api-key
    private val apiKey = "0b504b7112bc845d63522c31fdc52ea5"
    private val units = "metric"


    // Get current weather for a city by name
    suspend fun getCurrentWeather(cityName: String): Result<CurrentWeatherResponse> {
        return try {
            Log.d("WeatherRepository", "=== getCurrentWeather Called ===")
            Log.d("WeatherRepository", "City: $cityName")


            Log.d("WeatherRepository", "Calling API Service...")
            val response = apiService.getCurrentWeather(
                cityName = cityName,
                apiKey = apiKey,
                units = units
            )
            Log.d("WeatherRepository", " API Response received")
            Log.d(
                "WeatherRepository",
                "City: ${response.name}, Temp: ${response.mainWeather.temp}°C"
            )
            Result.success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepository", " getCurrentWeather Error: ${e.message}")
            Log.e("WeatherRepository", "Error type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Result.failure(e)
        }
    }


    // Get current weather by coordinates

    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<CurrentWeatherResponse> {
        return try {
            Log.d("WeatherRepository", "=== getCurrentWeatherByCoordinates Called ===")
            Log.d("WeatherRepository", "Latitude: $latitude, Longitude: $longitude")

            val response = apiService.getCurrentWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units
            )
            Log.d("WeatherRepository", " API Response received")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepository", " Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }


    // Get 5-day forecast for a city by name

    suspend fun getForecast(cityName: String): Result<WeatherResponse> {
        return try {
            Log.d("WeatherRepository", "=== getForecast Called ===")
            Log.d("WeatherRepository", "City: $cityName")

            val response = apiService.getForecast(
                cityName = cityName,
                apiKey = apiKey,
                units = units
            )
            Log.d("WeatherRepository", " Forecast received with ${response.list.size} items")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepository", " Forecast Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }


    // Get 5-day forecast by coordinates

    suspend fun getForecastByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse> {
        return try {
            Log.d("WeatherRepository", "=== getForecastByCoordinates Called ===")

            val response = apiService.getForecastByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units
            )
            Log.d("WeatherRepository", " Forecast received")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepository", " Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}