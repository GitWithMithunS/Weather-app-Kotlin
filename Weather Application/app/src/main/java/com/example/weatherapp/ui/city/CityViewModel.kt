package com.example.weatherapp.ui.city

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.CityDataSource
import com.example.weatherapp.data.local.room.entity.CityEntity
import com.example.weatherapp.data.repository.CityRepository
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState

    private val recommendedCities = listOf(
        // Indian Cities
        "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata",
        // Foreign Cities
        "New York", "London", "Tokyo", "Paris", "Sydney"
    )

    init {
        loadCities()
        _uiState.value = _uiState.value.copy(recommendedCities = recommendedCities)
    }

    private fun loadCities() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val user = userRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                    return@launch
                }

                val cities = cityRepository.getCities(user.username)

                _uiState.value = _uiState.value.copy(
                    username = user.username,
                    cities = cities,
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

    fun onCityNameChange(value: TextFieldValue) {
        val query = value.text
        val suggestions = if (query.isNotBlank()) {
            CityDataSource.cities.filter { it.startsWith(query, ignoreCase = true) && it != query }
        } else {
            emptyList()
        }

        _uiState.value = _uiState.value.copy(newCity = value, suggestions = suggestions)
//        _uiState.value = _uiState.value.copy(newCity = value, error = null)
    }

    fun addCity(cityName: String) {
        val state = _uiState.value

        if (cityName.isBlank()) {
            _uiState.value = state.copy(error = "City name cannot be empty")
            return
        }

        // Pre-check if city already exists in local list (to avoid unnecessary API calls)
        val cityExists = state.cities.any { it.cityName.equals(cityName.trim(), ignoreCase = true) }
        if (cityExists) {
            _uiState.value = state.copy(error = "City already added", newCity = TextFieldValue())
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                // Validate city with Weather API before adding
                val weatherResult = weatherRepository.getCurrentWeather(cityName.trim())

                if (weatherResult.isSuccess) {
                    val weatherResponse = weatherResult.getOrNull()
                    val validatedCityName = weatherResponse?.name ?: cityName.trim()

                    // Double check with validated name from API
                    if (state.cities.any { it.cityName.equals(validatedCityName, ignoreCase = true) }) {
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = "City already added",
                            newCity = TextFieldValue()
                        )
                        return@launch
                    }

                    cityRepository.addCity(
                        cityName = validatedCityName,
                        username = state.username,
                        latitude = weatherResponse?.coord?.lat ?: 0.0,
                        longitude = weatherResponse?.coord?.lon ?: 0.0
                    )

                loadCities()
                // Clear the search field and suggestions after adding a city
                _uiState.value = _uiState.value.copy(newCity = TextFieldValue(), suggestions = emptyList())
                    loadCities()
                    _uiState.value = _uiState.value.copy(newCity = TextFieldValue())
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid city name. Please try again."
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun deleteCity(city: CityEntity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                cityRepository.deleteCity(city)

                loadCities()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
}
