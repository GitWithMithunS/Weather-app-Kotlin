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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val user = userRepository.getCurrentUser() ?: throw Exception("User not found")
                val city = user.defaultCity

                val weatherResult = weatherRepository.getCurrentWeather(city)
                val weatherData = weatherResult.getOrThrow()

                val forecastResult = weatherRepository.getForecast(city)
                val forecastData = forecastResult.getOrThrow()

                val dailyForecasts = forecastData.list.groupBy {
                    it.dateText.substringBefore(" ")
                }.mapNotNull { (date, forecasts) ->
                    val first = forecasts.first()
                    val minTemp = forecasts.minOf { it.mainWeather.tempMin }
                    val maxTemp = forecasts.maxOf { it.mainWeather.tempMax }
                    val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(date)?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: ""

                    ForecastItem(
                        day = dayOfWeek,
                        description = first.weather.firstOrNull()?.description ?: "",
                        minTemp = "${minTemp.toInt()}°C",
                        maxTemp = "${maxTemp.toInt()}°C"
                    )
                }.take(5)


                _uiState.value = HomeUiState(
                    username = user.username,
                    city = city,
                    temperature = "${weatherData.mainWeather.temp.toInt()}°C",
                    description = weatherData.weather.firstOrNull()?.main ?: "Unknown",
                    humidity = "${weatherData.mainWeather.humidity}%",
                    windSpeed = "${weatherData.wind.speed.toInt()} m/s",
                    feelsLike = "${weatherData.mainWeather.feelsLike.toInt()}°C",
                    forecast = dailyForecasts,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unknown error occurred"
                )
            }
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
