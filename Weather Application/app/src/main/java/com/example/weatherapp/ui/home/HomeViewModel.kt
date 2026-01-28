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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // ===== STEP 1: Check User =====
                Log.d("API_TEST", "=== STEP 1: Checking User ===")
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val user = userRepository.getCurrentUser()
                Log.d("API_TEST", "Step 1 Result: user = $user")
                Log.d("API_TEST", "Step 1 Result: username = ${user?.username}")
                Log.d("API_TEST", "Step 1 Result: defaultCity = ${user?.defaultCity}")

                if (user == null) {
                    Log.e("API_TEST", " STEP 1 FAILED: User is null!")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = " STEP 1: User not found in database"
                    )
                    return@launch
                }

                Log.d("API_TEST", " STEP 1 PASSED: User found")

                val city = user.defaultCity
                Log.d("API_TEST", "City to fetch: $city")

                // ===== STEP 2: Fetch Current Weather =====
                Log.d("API_TEST", "=== STEP : Fetching Current Weather ===")
                Log.d("API_TEST", "Calling: weatherRepository.getCurrentWeather(\"$city\")")

                val weatherResult = weatherRepository.getCurrentWeather(city)

                Log.d("API_TEST", "Step 2 Result isSuccess: ${weatherResult.isSuccess}")
                Log.d("API_TEST", "Step 2 Result exception: ${weatherResult.exceptionOrNull()?.message}")
                Log.d("API_TEST", "Step 2 Result exception class: ${weatherResult.exceptionOrNull()?.javaClass?.simpleName}")

                if (weatherResult.isFailure) {
                    val error = weatherResult.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e("API_TEST", " STEP 2 FAILED: $error")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = " STEP 2: API Call Failed\n$error"
                    )
                    return@launch
                }

                Log.d("API_TEST", " STEP 2 PASSED: API returned data")

                // ===== STEP 3: Parse Weather Data =====
                Log.d("API_TEST", "=== STEP : Parsing Weather Data ===")

                val weatherData = weatherResult.getOrNull()
                Log.d("API_TEST", "Step 3: weatherData is null? ${weatherData == null}")

                if (weatherData == null) {
                    Log.e("API_TEST", " STEP 3 FAILED: Weather data is null!")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = " STEP 3: Response parsing failed"
                    )
                    return@launch
                }

                Log.d("API_TEST", "Step 3: cityName = ${weatherData.name}")
                Log.d("API_TEST", "Step 3: temperature = ${weatherData.mainWeather.temp}")
                Log.d("API_TEST", "Step 3: weather = ${weatherData.weather.firstOrNull()?.main}")
                Log.d("API_TEST", " STEP 3 PASSED: Data parsed successfully")

                // ===== STEP 4: Extract Values =====
                Log.d("API_TEST", "=== STEP 4: Extracting Values ===")

                val temperature = "${weatherData.mainWeather.temp.toInt()}°C"
                val description = weatherData.weather.firstOrNull()?.main ?: "Unknown"
                val humidity = "${weatherData.mainWeather.humidity}%"
                val windSpeed = "${weatherData.wind.speed.toInt()} m/s"
                val feelsLike = "${weatherData.mainWeather.feelsLike.toInt()}°C"

                Log.d("API_TEST", "Step 4: temperature = $temperature")
                Log.d("API_TEST", "Step 4: description = $description")
                Log.d("API_TEST", "Step 4: humidity = $humidity")
                Log.d("API_TEST", " STEP 4 PASSED: Values extracted")

                // ===== STEP 5: Fetch Forecast =====
                Log.d("API_TEST", "=== STEP 5: Fetching Forecast ===")

                val forecastResult = weatherRepository.getForecast(city)
                Log.d("API_TEST", "Step 5: forecastResult.isSuccess = ${forecastResult.isSuccess}")

                val forecast = mutableListOf<String>()

                if (forecastResult.isSuccess) {
                    val forecastData = forecastResult.getOrNull()
                    Log.d("API_TEST", "Step 5: forecast items count = ${forecastData?.list?.size}")

                    forecastData?.list?.take(5)?.forEach { item ->
                        val day = item.dateText.split(" ")[0]
                        val temp = "${item.mainWeather.temp.toInt()}°C"
                        val weather = item.weather.firstOrNull()?.main ?: "Unknown"
                        forecast.add("$day - $temp - $weather")
                        Log.d("API_TEST", "Step 5: added forecast: $day - $temp - $weather")
                    }
                    Log.d("API_TEST", " STEP 5 PASSED: Forecast fetched")
                } else {
                    Log.e("API_TEST", " STEP 5 WARNING: Forecast fetch failed, but will continue")
                }

                // ===== STEP 6: Update UI =====
                Log.d("API_TEST", "=== STEP 6: Updating UI State ===")

                _uiState.value = HomeUiState(
                    username = user.username,
                    city = city,
                    temperature = temperature,
                    description = description,
                    humidity = humidity,
                    windSpeed = windSpeed,
                    feelsLike = feelsLike,
                    forecast = forecast,
                    isLoading = false
                )

                Log.d("API_TEST", " STEP 6 PASSED: UI State updated")
                Log.d("API_TEST", "=== ALL STEPS PASSED  ===")

            } catch (e: Exception) {
                Log.e("API_TEST", " EXCEPTION: ${e.message}")
                Log.e("API_TEST", "Exception class: ${e.javaClass.simpleName}")
                e.printStackTrace()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = " Exception: ${e.message}"
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