package com.example.weatherapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailsUiState())
    val uiState: StateFlow<WeatherDetailsUiState> = _uiState

    fun loadWeather(city: String) {
        viewModelScope.launch {
            // Fake delay / fake API
            _uiState.value = WeatherDetailsUiState(
                city = city,
                temperature = "29°C",
                description = "Sunny",
                humidity = "65%",
                wind = "12 km/h",
                feelsLike = "31°C",
                isLoading = false
            )
        }
    }
}
