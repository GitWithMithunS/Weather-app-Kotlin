package com.example.weatherapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailsUiState())
    val uiState: StateFlow<WeatherDetailsUiState> = _uiState

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Fetch current weather
                val currentWeatherResult = weatherRepository.getCurrentWeather(city)

                if (!currentWeatherResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch current weather"
                    )
                    return@launch
                }

                val currentWeather = currentWeatherResult.getOrNull() ?: return@launch

                // Fetch forecast
                val forecastResult = weatherRepository.getForecast(city)
                var hourlyData = emptyList<HourlyForecast>()
                var dailyForecast = emptyList<DailyForecast>()

                if (forecastResult.isSuccess) {
                    val forecast = forecastResult.getOrNull()
                    if (forecast != null) {
                        hourlyData = forecast.list.take(8).map { item ->
                            HourlyForecast(
                                time = formatTime(item.dt),
                                temperature = "${item.mainWeather.temp.toInt()}°C",
                                description = item.weather.firstOrNull()?.main ?: "Unknown",
                                icon = item.weather.firstOrNull()?.icon ?: ""
                            )
                        }

                        // Group by day for daily forecast
                        val dailyMap = mutableMapOf<String, DailyForecast>()
                        forecast.list.forEach { item ->
                            val day = formatDate(item.dt)
                            val existing = dailyMap[day]
                            val maxTemp = "${item.mainWeather.tempMax.toInt()}°C"
                            val minTemp = "${item.mainWeather.tempMin.toInt()}°C"
                            val description = item.weather.firstOrNull()?.main ?: "Unknown"
                            val icon = item.weather.firstOrNull()?.icon ?: ""

                            if (existing == null) {
                                dailyMap[day] = DailyForecast(
                                    day = day,
                                    maxTemp = maxTemp,
                                    minTemp = minTemp,
                                    description = description,
                                    icon = icon
                                )
                            }
                        }
                        dailyForecast = dailyMap.values.toList().take(5)
                    }
                }

                // Convert timestamps to readable format
                val sunriseTime = formatTime(currentWeather.sysInfo.sunrise)
                val sunsetTime = formatTime(currentWeather.sysInfo.sunset)

                _uiState.value = WeatherDetailsUiState(
                    city = currentWeather.name,
                    temperature = "${currentWeather.mainWeather.temp.toInt()}°C",
                    description = currentWeather.weather.firstOrNull()?.main ?: "Unknown",
                    humidity = "${currentWeather.mainWeather.humidity}%",
                    wind = "${currentWeather.wind.speed.toInt()} m/s",
                    feelsLike = "${currentWeather.mainWeather.feelsLike.toInt()}°C",
                    visibility = "${(currentWeather.visibility / 1000).toInt()} km",
                    pressure = "${currentWeather.mainWeather.pressure} hPa",
                    cloudiness = "${currentWeather.clouds.all}%",
                    sunrise = sunriseTime,
                    sunset = sunsetTime,
                    hourlyData = hourlyData,
                    dailyForecast = dailyForecast,
                    isLoading = false
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val format = SimpleDateFormat("EEE", Locale.getDefault())
        return format.format(calendar.time)
    }
}