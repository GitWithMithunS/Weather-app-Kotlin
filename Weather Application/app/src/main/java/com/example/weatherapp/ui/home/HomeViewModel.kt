package com.example.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()

            _uiState.value = HomeUiState(
                username = user?.username ?: "",
                city = user?.defaultCity ?: "",
                temperature = "28°C",
                description = "Partly Cloudy",
                forecast = listOf(
                    "Mon - 28°C",
                    "Tue - 30°C",
                    "Wed - 27°C",
                    "Thu - 29°C",
                    "Fri - 31°C"
                ),
                isLoading = false
            )
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onComplete()
        }
    }
}
