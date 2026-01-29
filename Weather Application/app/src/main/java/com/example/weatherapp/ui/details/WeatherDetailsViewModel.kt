package com.example.weatherapp.ui.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    private var rawMainTemp: Double = 0.0
    private var rawFeelsLike: Double = 0.0

    // NEW: Helper to get Icon based on description
    fun getWeatherIcon(description: String): ImageVector {
        return when (description.lowercase()) {
            "clear" -> Icons.Default.WbSunny
            "haze", "mist", "fog" -> Icons.Default.CloudQueue
            "clouds" -> Icons.Default.Cloud
            "rain", "drizzle" -> Icons.Default.Umbrella
            "thunderstorm" -> Icons.Default.FlashOn
            "snow" -> Icons.Default.AcUnit
            else -> Icons.Default.WbCloudy
        }
    }

    // NEW: Helper to get Color based on description
    fun getWeatherColor(description: String): Color {
        return when (description.lowercase()) {
            "clear" -> Color(0xFFFFD700) // Gold
            "clouds" -> Color.Gray
            "haze", "mist", "fog" -> Color(0xFFB0C4DE) // Light Steel Blue
            "rain", "drizzle" -> Color(0xFF4682B4) // Steel Blue
            else -> Color(0xFF81D4FA) // Light Blue
        }
    }

    fun toggleUnit() {
        val newIsFahrenheit = !_uiState.value.isFahrenheit
        updateDisplayUnits(newIsFahrenheit)
    }

    private fun updateDisplayUnits(isFahrenheit: Boolean) {
        val unit = if (isFahrenheit) "°F" else "°C"
        fun convert(c: Double) = if (isFahrenheit) (c * 9/5 + 32).toInt() else c.toInt()

        val updatedHourly = _uiState.value.hourlyData.map {
            it.copy(temperature = "${convert(it.rawTemp)}$unit")
        }

        val updatedDaily = _uiState.value.dailyForecast.map {
            it.copy(
                maxTemp = "${convert(it.rawMax)}$unit",
                minTemp = "${convert(it.rawMin)}$unit"
            )
        }

        _uiState.value = _uiState.value.copy(
            isFahrenheit = isFahrenheit,
            temperature = "${convert(rawMainTemp)}$unit",
            feelsLike = "${convert(rawFeelsLike)}$unit",
            hourlyData = updatedHourly,
            dailyForecast = updatedDaily
        )
    }

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val currentWeatherResult = weatherRepository.getCurrentWeather(city)
                val forecastResult = weatherRepository.getForecast(city)

                if (currentWeatherResult.isSuccess) {
                    val currentWeather = currentWeatherResult.getOrNull()!!
                    rawMainTemp = currentWeather.mainWeather.temp
                    rawFeelsLike = currentWeather.mainWeather.feelsLike

                    var hourlyData = emptyList<HourlyForecast>()
                    var dailyForecast = emptyList<DailyForecast>()

                    if (forecastResult.isSuccess) {
                        val forecast = forecastResult.getOrNull()!!
                        hourlyData = forecast.list.take(8).map { item ->
                            val desc = item.weather.firstOrNull()?.main ?: "Unknown"
                            HourlyForecast(
                                time = formatTime(item.dt),
                                temperature = "${item.mainWeather.temp.toInt()}°C",
                                rawTemp = item.mainWeather.temp,
                                description = desc,
                                icon = item.weather.firstOrNull()?.icon ?: ""
                            )
                        }

                        val dailyMap = mutableMapOf<String, DailyForecast>()
                        forecast.list.forEach { item ->
                            val day = formatDate(item.dt)
                            if (!dailyMap.containsKey(day)) {
                                dailyMap[day] = DailyForecast(
                                    day = day,
                                    maxTemp = "${item.mainWeather.tempMax.toInt()}°C",
                                    minTemp = "${item.mainWeather.tempMin.toInt()}°C",
                                    rawMax = item.mainWeather.tempMax,
                                    rawMin = item.mainWeather.tempMin,
                                    description = item.weather.firstOrNull()?.main ?: "Unknown",
                                    icon = item.weather.firstOrNull()?.icon ?: ""
                                )
                            }
                        }
                        dailyForecast = dailyMap.values.toList().take(5)
                    }

                    _uiState.value = WeatherDetailsUiState(
                        city = currentWeather.name,
                        temperature = "${rawMainTemp.toInt()}°C",
                        description = currentWeather.weather.firstOrNull()?.main ?: "Unknown",
                        humidity = "${currentWeather.mainWeather.humidity}%",
                        wind = "${currentWeather.wind.speed.toInt()} m/s",
                        feelsLike = "${rawFeelsLike.toInt()}°C",
                        visibility = "${(currentWeather.visibility / 1000).toInt()} km",
                        pressure = "${currentWeather.mainWeather.pressure} hPa",
                        cloudiness = "${currentWeather.clouds.all}%",
                        sunrise = formatTime(currentWeather.sysInfo.sunrise),
                        sunset = formatTime(currentWeather.sysInfo.sunset),
                        hourlyData = hourlyData,
                        dailyForecast = dailyForecast,
                        isLoading = false,
                        isFahrenheit = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp * 1000))
    private fun formatDate(timestamp: Long): String = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp * 1000))
}