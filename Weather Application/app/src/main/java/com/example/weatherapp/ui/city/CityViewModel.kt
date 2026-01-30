package com.example.weatherapp.ui.city

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.room.entity.CityEntity
import com.example.weatherapp.data.repository.CityRepository
import com.example.weatherapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val userRepository: UserRepository
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
        _uiState.value = _uiState.value.copy(newCity = value)
    }

    fun addCity(cityName: String) {
        val state = _uiState.value

        if (cityName.isBlank()) {
            _uiState.value = state.copy(error = "City name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                cityRepository.addCity(
                    cityName = cityName,
                    username = state.username
                )

                loadCities()
                _uiState.value = _uiState.value.copy(newCity = TextFieldValue())

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
