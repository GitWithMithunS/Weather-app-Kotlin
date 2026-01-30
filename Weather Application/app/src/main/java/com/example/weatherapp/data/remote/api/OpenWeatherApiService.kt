package com.example.weatherapp.data.remote.api

import com.example.weatherapp.data.remote.model.CurrentWeatherResponse
import com.example.weatherapp.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApiService {

    /**
     * Get current weather for a city
     * @param cityName: Name of the city
     * @param apiKey:  OpenWeather API key
     * @param units: Units of measurement (metric for Celsius, imperial for Fahrenheit)
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): CurrentWeatherResponse

    /**
     * Get current weather by coordinates
     * @param latitude: Latitude of the location
     * @param longitude: Longitude of the location
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): CurrentWeatherResponse

    /**
     * Get 5-day forecast for a city
     * @param cityName: Name of the city
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    /**
     * Get 5-day forecast by coordinates
     */
    @GET("forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}