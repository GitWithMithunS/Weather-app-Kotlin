package com.example.weatherapp.ui.login

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            username = value,
            usernameError = null
        )
    }

    fun onPasswordChange(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null
        )
    }

    fun onConfirmPasswordChange(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun onCityChange(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            city = value,
            cityError = null
        )
    }

    fun toggleRegisterMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            error = null,
            usernameError = null,
            passwordError = null,
            confirmPasswordError = null,
            cityError = null
        )
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        val username = state.username.text
        val password = state.password.text
        val city = state.city.text

        // 1. Local Validation
        val localErrors = validateRegister(state)
        if (localErrors.isNotEmpty()) {
            _uiState.value = state.copy(
                usernameError = localErrors["username"],
                passwordError = localErrors["password"],
                confirmPasswordError = localErrors["confirmPassword"],
                cityError = localErrors["city"],
                error = "Please enter proper details"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                // 2. API-based City Validation
                val weatherResult = weatherRepository.getCurrentWeather(city)
                if (weatherResult.isFailure) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        cityError = "Invalid city name."
                    )
                    return@launch
                }

                // 3. Check if user already exists
                val existingUser = userRepository.getUserByUsername(username)
                if (existingUser != null) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Username already exists! Please choose a different username."
                    )
                    return@launch
                }

                // 4. Create new user
                val success = userRepository.saveAndLoginUser(username, password, city)

                if (success) {
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Registration failed. Please try again."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        val username = state.username.text
        val password = state.password.text

        val errors = validateLogin(state)
        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(
                usernameError = errors["username"],
                passwordError = errors["password"],
                error = "Please enter proper details"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)
                val user = userRepository.getUserByUsername(username)

                if (user == null || user.password != password) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                    return@launch
                }

                val success = userRepository.loginUser(username, password)

                if (success) {
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Login failed. Please try again."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun getPasswordValidationError(password: String): String? {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        if (password.length < 8 || !hasLetter || !hasDigit || !hasSpecial) {
            return "Password must be at least 8 characters and contain a mix of letters, numbers, and special characters."
        }

        return null
    }

    // Validation functions
    private fun validateRegister(state: LoginUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val username = state.username.text
        val password = state.password.text
        val confirmPassword = state.confirmPassword.text
        val city = state.city.text

        // Username validation
        when {
            username.isBlank() -> errors["username"] = "Username is required"
            username.length < 3 -> errors["username"] = "Username must be at least 3 characters"
            username.length > 20 -> errors["username"] = "Username must be less than 20 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]*$")) ->
                errors["username"] = "Username can only contain letters, numbers, and underscores"
        }

        // Password validation
        if (password.isBlank()) {
            errors["password"] = "Password is required"
        } else {
            getPasswordValidationError(password)?.let {
                errors["password"] = it
            }
        }

        // Confirm password validation
        when {
            confirmPassword.isBlank() -> errors["confirmPassword"] = "Please confirm your password"
            password != confirmPassword -> errors["confirmPassword"] = "Passwords do not match"
        }

        // City validation
        when {
            city.isBlank() -> errors["city"] = "Default city is required"
            city.length < 2 -> errors["city"] = "City name is too short"
            city.length > 50 -> errors["city"] = "City name is too long"
        }

        return errors
    }

    private fun validateLogin(state: LoginUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val username = state.username.text
        val password = state.password.text

        // Username validation
        if (username.isBlank()) {
            errors["username"] = "Username is required"
        }

        // Password validation
        if (password.isBlank()) {
            errors["password"] = "Password is required"
        }

        return errors
    }
}
