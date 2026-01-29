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
                                time = formatTime(item.dateText),
                                temperature = "${item.mainWeather.temp.toInt()}°C",
                                description = item.weather.firstOrNull()?.main ?: "Unknown",
                                icon = item.weather.firstOrNull()?.icon ?: ""
                            )
                        }

                        // Group by day for daily forecast
                        dailyForecast = forecast.list.groupBy {
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
                                minTemp = "${minTemp.toInt()}°C",
                                maxTemp = "${maxTemp.toInt()}°C",
                                icon = first.weather.firstOrNull()?.icon ?: "",
                                date = date
                            )
                        }.take(5)
                    }
                }

                // Convert timestamps to readable format
                val sunriseTime = formatTime(currentWeather.sysInfo.sunrise)
                val sunsetTime = formatTime(currentWeather.sysInfo.sunset)

                _uiState.value = WeatherDetailsUiState(
                    title = "Today",
                    city = currentWeather.name,
                    temperature = "${currentWeather.mainWeather.temp.toInt()}°C",
                    description = currentWeather.weather.firstOrNull()?.main ?: "Unknown",
                    icon = currentWeather.weather.firstOrNull()?.icon ?: "",
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

    fun loadWeatherForDate(city: String, date: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val forecastResult = weatherRepository.getForecast(city)
                val forecastData = forecastResult.getOrThrow()

                val forecastsForDate = forecastData.list.filter {
                    it.dateText.startsWith(date)
                }

                if (forecastsForDate.isEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "No data for selected date")
                    return@launch
                }

                val firstData = forecastsForDate.first()

                val hourlyData = forecastsForDate.map {
                    HourlyForecast(
                        time = formatTime(it.dateText),
                        temperature = "${it.mainWeather.temp.toInt()}°C",
                        description = it.weather.firstOrNull()?.description ?: "",
                        icon = it.weather.firstOrNull()?.icon ?: ""
                    )
                }

                val dailyForecasts = forecastData.list.groupBy {
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
                        minTemp = "${minTemp.toInt()}°C",
                        maxTemp = "${maxTemp.toInt()}°C",
                        icon = first.weather.firstOrNull()?.icon ?: "",
                        date = date
                    )
                }.take(5)

                val sunriseTime = formatTime(forecastData.city.sunrise)
                val sunsetTime = formatTime(forecastData.city.sunset)
                val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(date)?.let { SimpleDateFormat("EEEE", Locale.getDefault()).format(it) } ?: ""

                _uiState.value = WeatherDetailsUiState(
                    title = dayOfWeek,
                    city = city,
                    temperature = "${firstData.mainWeather.temp.toInt()}°C",
                    description = firstData.weather.firstOrNull()?.description ?: "",
                    icon = firstData.weather.firstOrNull()?.icon ?: "",
                    humidity = "${firstData.mainWeather.humidity}%",
                    wind = "${firstData.wind.speed.toInt()} m/s",
                    feelsLike = "${firstData.mainWeather.feelsLike.toInt()}°C",
                    visibility = "${(firstData.visibility / 1000)} km",
                    pressure = "${firstData.mainWeather.pressure} hPa",
                    cloudiness = "${firstData.clouds.all}%",
                    sunrise = sunriseTime,
                    sunset = sunsetTime,
                    hourlyData = hourlyData,
                    dailyForecast = dailyForecasts,
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

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun formatTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateString.substringAfter(" ").substringBeforeLast(":")
        } catch (e: Exception) {
            dateString.substringAfter(" ").substringBeforeLast(":")
        }
    }

    private fun formatDateToDay(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val format = SimpleDateFormat("EEE", Locale.getDefault())
        return format.format(calendar.time)
    }
}