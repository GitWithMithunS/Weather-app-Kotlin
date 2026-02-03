package com.example.weatherapp.ui.city

import androidx.compose.ui.text.input.TextFieldValue
import com.example.weatherapp.data.local.room.entity.CityEntity

data class CityUiState(
    val username: String = "",
    val cities: List<CityEntity> = emptyList(),
    val newCity: TextFieldValue = TextFieldValue(),
    val recommendedCities: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
