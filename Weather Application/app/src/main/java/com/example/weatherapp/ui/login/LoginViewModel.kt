package com.example.weatherapp.ui.login

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(
            username = value,
            usernameError = null  // Clears error when user starts typing
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
        val confirmPassword = state.confirmPassword.text
        val city = state.city.text

        Log.d("LoginViewModel", "=== REGISTER ATTEMPT ===")
        Log.d("LoginViewModel", "Username: $username")
        Log.d("LoginViewModel", "City: $city")

        // Validation
        val errors = validateRegister(state)
        if (errors.isNotEmpty()) {
            Log.d("LoginViewModel", "Validation errors: $errors")
            _uiState.value = state.copy(
                usernameError = errors["username"],
                passwordError = errors["password"],
                confirmPasswordError = errors["confirmPassword"],
                cityError = errors["city"],
                error = "Please enter proper details"
            )
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting registration...")
                _uiState.value = state.copy(isLoading = true, error = null)

                // Check if user already exists
                val existingUser = userRepository.getUserByUsername(username)
                if (existingUser != null) {
                    Log.e("LoginViewModel", "User already exists: $username")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Username already exists! Please choose a different username."
                    )
                    return@launch
                }

                // Create new user
                val success = userRepository.saveAndLoginUser(username, password, city)

                if (success) {
                    Log.d("LoginViewModel", " Registration successful!")
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                } else {
                    Log.e("LoginViewModel", " Registration failed")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Registration failed. Please try again."
                    )
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during registration: ${e.message}", e)
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

        Log.d("LoginViewModel", "=== LOGIN ATTEMPT ===")
        Log.d("LoginViewModel", "Username: $username")

        // Validation for login
        val errors = validateLogin(state)
        if (errors.isNotEmpty()) {
            Log.d("LoginViewModel", "Validation errors: $errors")
            _uiState.value = state.copy(
                usernameError = errors["username"],
                passwordError = errors["password"],
                error = "Please enter proper details"
            )
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting login...")
                _uiState.value = state.copy(isLoading = true, error = null)

                // Get user from database
                val user = userRepository.getUserByUsername(username)

                if (user == null) {
                    Log.e("LoginViewModel", "User not found: $username")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                    return@launch
                }

                // Check password
                if (user.password != password) {
                    Log.e("LoginViewModel", "Wrong password for user: $username")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                    return@launch
                }

                // Mark user as logged in
                val success = userRepository.loginUser(username, password)

                if (success) {
                    Log.d("LoginViewModel", " Login successful!")
                    _uiState.value = state.copy(isLoading = false)
                    onSuccess()
                } else {
                    Log.e("LoginViewModel", " Login failed")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Login failed. Please try again."
                    )
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during login: ${e.message}", e)
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
