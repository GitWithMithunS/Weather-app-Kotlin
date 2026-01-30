package com.example.weatherapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private var tempInCelsius: Int = 0
    private var feelsLikeInCelsius: Int = 0
    private var hourlyForecasts: List<HourlyForecast> = emptyList()
    private var dailyForecasts: List<DailyForecast> = emptyList()

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val currentWeatherResult = weatherRepository.getCurrentWeather(city)
                val currentWeather = currentWeatherResult.getOrThrow()

                val forecastResult = weatherRepository.getForecast(city)
                val forecastData = forecastResult.getOrThrow()

                tempInCelsius = currentWeather.mainWeather.temp.toInt()
                feelsLikeInCelsius = currentWeather.mainWeather.feelsLike.toInt()

                hourlyForecasts = forecastData.list.take(8).map { item ->
                    HourlyForecast(
                        time = formatTime(item.dateText),
                        temperature = "${item.mainWeather.temp.toInt()}",
                        description = item.weather.firstOrNull()?.main ?: "Unknown",
                        icon = item.weather.firstOrNull()?.icon ?: ""
                    )
                }

                dailyForecasts = forecastData.list.groupBy {
                    it.dateText.substringBefore(" ")
                }.mapNotNull { (date, forecasts) ->
                    val first = forecasts.first()
                    val minTemp = forecasts.minOf { it.mainWeather.tempMin }
                    val maxTemp = forecasts.maxOf { it.mainWeather.tempMax }
                    val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(date)?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: ""

                    DailyForecast(
                        day = dayOfWeek,
                        description = first.weather.firstOrNull()?.description ?: "",
                        minTemp = "${minTemp.toInt()}",
                        maxTemp = "${maxTemp.toInt()}",
                        icon = first.weather.firstOrNull()?.icon ?: "",
                        date = date
                    )
                }.take(5)

                updateTemperatures()

                _uiState.update {
                    it.copy(
                        title = "Today",
                        city = currentWeather.name,
                        description = currentWeather.weather.firstOrNull()?.main ?: "Unknown",
                        icon = currentWeather.weather.firstOrNull()?.icon ?: "",
                        humidity = "${currentWeather.mainWeather.humidity}%",
                        wind = "${currentWeather.wind.speed.toInt()} m/s",
                        visibility = "${currentWeather.visibility / 1000} km",
                        pressure = "${currentWeather.mainWeather.pressure} hPa",
                        cloudiness = "${currentWeather.clouds.all}%",
                        sunrise = formatTime(currentWeather.sysInfo.sunrise),
                        sunset = formatTime(currentWeather.sysInfo.sunset),
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "An unknown error occurred") }
            }
        }
    }

    fun loadWeatherForDate(city: String, date: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val forecastResult = weatherRepository.getForecast(city)
                val forecastData = forecastResult.getOrThrow()

                val forecastsForDate = forecastData.list.filter {
                    it.dateText.startsWith(date)
                }

                if (forecastsForDate.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No data for selected date") }
                    return@launch
                }

                val firstData = forecastsForDate.first()
                tempInCelsius = firstData.mainWeather.temp.toInt()
                feelsLikeInCelsius = firstData.mainWeather.feelsLike.toInt()

                hourlyForecasts = forecastsForDate.map {
                    HourlyForecast(
                        time = formatTime(it.dateText),
                        temperature = "${it.mainWeather.temp.toInt()}",
                        description = it.weather.firstOrNull()?.description ?: "",
                        icon = it.weather.firstOrNull()?.icon ?: ""
                    )
                }

                dailyForecasts = forecastData.list.groupBy {
                    it.dateText.substringBefore(" ")
                }.mapNotNull { (date, forecasts) ->
                    val first = forecasts.first()
                    val minTemp = forecasts.minOf { it.mainWeather.tempMin }
                    val maxTemp = forecasts.maxOf { it.mainWeather.tempMax }
                    val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(date)?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: ""

                    DailyForecast(
                        day = dayOfWeek,
                        description = first.weather.firstOrNull()?.description ?: "",
                        minTemp = "${minTemp.toInt()}",
                        maxTemp = "${maxTemp.toInt()}",
                        icon = first.weather.firstOrNull()?.icon ?: "",
                        date = date
                    )
                }.take(5)

                updateTemperatures()

                val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(date)?.let { SimpleDateFormat("EEEE", Locale.getDefault()).format(it) } ?: ""

                _uiState.update {
                    it.copy(
                        title = dayOfWeek,
                        city = city,
                        description = firstData.weather.firstOrNull()?.description ?: "",
                        icon = firstData.weather.firstOrNull()?.icon ?: "",
                        humidity = "${firstData.mainWeather.humidity}%",
                        wind = "${firstData.wind.speed.toInt()} m/s",
                        visibility = "${firstData.visibility / 1000} km",
                        pressure = "${firstData.mainWeather.pressure} hPa",
                        cloudiness = "${firstData.clouds.all}%",
                        sunrise = formatTime(forecastData.city.sunrise),
                        sunset = formatTime(forecastData.city.sunset),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "An unknown error occurred") }
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
                hourlyData = hourlyForecasts.map {
                    it.copy(temperature = formatTemperature(it.temperature.toInt(), isFahrenheit))
                },
                dailyForecast = dailyForecasts.map {
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

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun formatTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateString.substringAfter(" ").substringBeforeLast(":")
        } catch (_: Exception) {
            dateString.substringAfter(" ").substringBeforeLast(":")
        }
    }
}
