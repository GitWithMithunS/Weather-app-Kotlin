package com.example.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // Store raw values to prevent rounding errors during repeated conversions
    private var lastCelsiusTemp: Double = 0.0
    private var lastForecastCelsius: List<Double> = emptyList()

    init { loadHomeData() }

    fun toggleUnit() {
        val newIsFahrenheit = !_uiState.value.isFahrenheit
        val unitLabel = if (newIsFahrenheit) "°F" else "°C"

        // Convert Main Temp
        val convertedTemp = if (newIsFahrenheit) (lastCelsiusTemp * 9/5) + 32 else lastCelsiusTemp

        // Convert Forecast List
        val updatedForecast = _uiState.value.forecast.mapIndexed { index, currentString ->
            val tempC = lastForecastCelsius.getOrNull(index) ?: 0.0
            val converted = if (newIsFahrenheit) (tempC * 9/5) + 32 else tempC
            val parts = currentString.split(" - ")
            "${parts[0]} - ${converted.toInt()}$unitLabel - ${parts.last()}"
        }

        _uiState.value = _uiState.value.copy(
            isFahrenheit = newIsFahrenheit,
            temperature = "${convertedTemp.toInt()}$unitLabel",
            forecast = updatedForecast
        )
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = userRepository.getCurrentUser() ?: return@launch

            val weatherResult = weatherRepository.getCurrentWeather(user.defaultCity)
            val forecastResult = weatherRepository.getForecast(user.defaultCity)

            if (weatherResult.isSuccess && forecastResult.isSuccess) {
                val weather = weatherResult.getOrNull()!!
                val forecastData = forecastResult.getOrNull()!!

                lastCelsiusTemp = weather.mainWeather.temp
                lastForecastCelsius = forecastData.list.take(5).map { it.mainWeather.temp }

                _uiState.value = HomeUiState(
                    username = user.username,
                    city = user.defaultCity,
                    temperature = "${lastCelsiusTemp.toInt()}°C",
                    description = weather.weather.firstOrNull()?.main ?: "",
                    forecast = forecastData.list.take(5).mapIndexed { i, item ->
                        "${item.dateText.split(" ")[0]} - ${lastForecastCelsius[i].toInt()}°C - ${item.weather.firstOrNull()?.main}"
                    },
                    isLoading = false
                )
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch { userRepository.logout(); onComplete() }
    }
}