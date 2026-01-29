package com.example.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.ForecastItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var tempInCelsius: Int = 0
    private var feelsLikeInCelsius: Int = 0
    private var forecastItems: List<ForecastItem> = emptyList()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val user = userRepository.getCurrentUser() ?: throw Exception("User not found")
                val city = user.defaultCity

                val weatherResult = weatherRepository.getCurrentWeather(city)
                val weatherData = weatherResult.getOrThrow()

                val forecastResult = weatherRepository.getForecast(city)
                val forecastData = forecastResult.getOrThrow()

                tempInCelsius = weatherData.mainWeather.temp.toInt()
                feelsLikeInCelsius = weatherData.mainWeather.feelsLike.toInt()

                forecastItems = forecastData.list.groupBy {
                    it.dateText.substringBefore(" ")
                }.mapNotNull { (date, forecasts) ->
                    val first = forecasts.first()
                    val minTemp = forecasts.minOf { it.mainWeather.tempMin }
                    val maxTemp = forecasts.maxOf { it.mainWeather.tempMax }
                    val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(date)?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: ""

                    ForecastItem(
                        date = date,
                        day = dayOfWeek,
                        description = first.weather.firstOrNull()?.description ?: "",
                        minTemp = "${minTemp.toInt()}",
                        maxTemp = "${maxTemp.toInt()}",
                        icon = first.weather.firstOrNull()?.icon ?: ""
                    )
                }.take(5)

                updateTemperatures()

                _uiState.update {
                    it.copy(
                        username = user.username,
                        city = city,
                        description = weatherData.weather.firstOrNull()?.main ?: "Unknown",
                        icon = weatherData.weather.firstOrNull()?.icon ?: "",
                        humidity = "${weatherData.mainWeather.humidity}%",
                        windSpeed = "${weatherData.wind.speed.toInt()} m/s",
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }

    fun toggleTemperatureUnit() {
        _uiState.update { it.copy(isFahrenheit = !it.isFahrenheit) }
        updateTemperatures()
    }

    private fun updateTemperatures() {
        val isFahrenheit = _uiState.value.isFahrenheit
        _uiState.update {
            it.copy(
                temperature = formatTemperature(tempInCelsius, isFahrenheit),
                feelsLike = formatTemperature(feelsLikeInCelsius, isFahrenheit),
                forecast = forecastItems.map {
                    it.copy(
                        minTemp = formatTemperature(it.minTemp.toInt(), isFahrenheit),
                        maxTemp = formatTemperature(it.maxTemp.toInt(), isFahrenheit)
                    )
                }
            )
        }
    }

    private fun formatTemperature(tempInCelsius: Int, isFahrenheit: Boolean): String {
        return if (isFahrenheit) {
            val fahrenheit = (tempInCelsius * 9 / 5) + 32
            "${fahrenheit}°F"
        } else {
            "${tempInCelsius}°C"
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onComplete()
        }
    }

    fun refreshWeather() {
        loadHomeData()
    }
}
