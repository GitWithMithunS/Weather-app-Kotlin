package com.example.weatherapp.ui.city

import com.example.weatherapp.data.local.room.entity.CityEntity

data class CityUiState(
    val username: String = "",
    val cities: List<CityEntity> = emptyList(),
    val newCity: String = "",
    val recommendedCities: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
