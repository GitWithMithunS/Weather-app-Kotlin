package com.example.weatherapp.ui.login

import android.util.Log
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

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            username = value,
            usernameError = null  // Clear error when user starts typing
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null
        )
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun onCityChange(value: String) {
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

        Log.d("LoginViewModel", "=== REGISTER ATTEMPT ===")
        Log.d("LoginViewModel", "Username: ${state.username}")
        Log.d("LoginViewModel", "City: ${state.city}")

        // Validation
        val errors = validateRegister(state)
        if (errors.isNotEmpty()) {
            Log.d("LoginViewModel", "Validation errors: $errors")
            _uiState.value = state.copy(
                usernameError = errors["username"],
                passwordError = errors["password"],
                confirmPasswordError = errors["confirmPassword"],
                cityError = errors["city"],
                error = "Please fix the errors above"
            )
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting registration...")
                _uiState.value = state.copy(isLoading = true, error = null)

                // Check if user already exists
                val existingUser = userRepository.getUserByUsername(state.username)
                if (existingUser != null) {
                    Log.e("LoginViewModel", "User already exists: ${state.username}")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Username already exists! Please choose a different username."
                    )
                    return@launch
                }

                // Create new user
                val success = userRepository.saveAndLoginUser(
                    username = state.username,
                    password = state.password,
                    city = state.city
                )

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

        Log.d("LoginViewModel", "=== LOGIN ATTEMPT ===")
        Log.d("LoginViewModel", "Username: ${state.username}")

        // Validation for login
        val errors = validateLogin(state)
        if (errors.isNotEmpty()) {
            Log.d("LoginViewModel", "Validation errors: $errors")
            _uiState.value = state.copy(
                usernameError = errors["username"],
                passwordError = errors["password"],
                error = "Please fix the errors above"
            )
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting login...")
                _uiState.value = state.copy(isLoading = true, error = null)

                // Get user from database
                val user = userRepository.getUserByUsername(state.username)

                if (user == null) {
                    Log.e("LoginViewModel", "User not found: ${state.username}")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                    return@launch
                }

                // Check password
                if (user.password != state.password) {
                    Log.e("LoginViewModel", "Wrong password for user: ${state.username}")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid username or password"
                    )
                    return@launch
                }

                // Mark user as logged in
                val success = userRepository.loginUser(state.username, state.password)

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

    // Validation functions
    private fun validateRegister(state: LoginUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Username validation
        when {
            state.username.isBlank() -> errors["username"] = "Username is required"
            state.username.length < 3 -> errors["username"] = "Username must be at least 3 characters"
            state.username.length > 20 -> errors["username"] = "Username must be less than 20 characters"
            !state.username.matches(Regex("^[a-zA-Z0-9_]*$")) ->
                errors["username"] = "Username can only contain letters, numbers, and underscores"
        }

        // Password validation
        when {
            state.password.isBlank() -> errors["password"] = "Password is required"
            state.password.length < 4 -> errors["password"] = "Password must be at least 4 characters"
            state.password.length > 50 -> errors["password"] = "Password must be less than 50 characters"
        }

        // Confirm password validation
        when {
            state.confirmPassword.isBlank() -> errors["confirmPassword"] = "Please confirm your password"
            state.password != state.confirmPassword -> errors["confirmPassword"] = "Passwords do not match"
        }

        // City validation
        when {
            state.city.isBlank() -> errors["city"] = "Default city is required"
            state.city.length < 2 -> errors["city"] = "City name is too short"
            state.city.length > 50 -> errors["city"] = "City name is too long"
        }

        return errors
    }

    private fun validateLogin(state: LoginUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Username validation
        if (state.username.isBlank()) {
            errors["username"] = "Username is required"
        }

        // Password validation
        if (state.password.isBlank()) {
            errors["password"] = "Password is required"
        }

        return errors
    }
}