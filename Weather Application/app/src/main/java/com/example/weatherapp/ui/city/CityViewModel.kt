package com.example.weatherapp.ui.city

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

    init {
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val user = userRepository.getCurrentUser()
            val username = user?.username ?: return@launch

            val cities = cityRepository.getCities(username)

            _uiState.value = CityUiState(
                username = username,
                cities = cities,
                isLoading = false
            )
        }
    }

    fun onCityNameChange(value: String) {
        _uiState.value = _uiState.value.copy(newCity = value)
    }

    fun addCity() {
        val state = _uiState.value

        if (state.newCity.isBlank()) return

        viewModelScope.launch {
            cityRepository.addCity(
                cityName = state.newCity,
                username = state.username
            )
            loadCities()
            _uiState.value = _uiState.value.copy(newCity = "")
        }
    }

    fun deleteCity(city: CityEntity) {
        viewModelScope.launch {
            cityRepository.deleteCity(city)
            loadCities()
        }
    }
}
