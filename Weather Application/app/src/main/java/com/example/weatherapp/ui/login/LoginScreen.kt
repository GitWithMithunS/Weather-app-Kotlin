package com.example.weatherapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


private enum class FocusField {
    USERNAME, PASSWORD, CONFIRM_PASSWORD, CITY, NONE
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    //States to preserve focus on config changes(on activity recreation)
    var focusedField by rememberSaveable { mutableStateOf(FocusField.NONE) }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }

    // Restores focus when the composable is recomposed
    LaunchedEffect(Unit) {
        when (focusedField) {
            FocusField.USERNAME -> usernameFocusRequester.requestFocus()
            FocusField.PASSWORD -> passwordFocusRequester.requestFocus()
            FocusField.CONFIRM_PASSWORD -> confirmPasswordFocusRequester.requestFocus()
            FocusField.CITY -> cityFocusRequester.requestFocus()
            FocusField.NONE -> {}
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = Icons.Filled.Cloud,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (state.isRegisterMode) "Create Account" else "Weather App Login",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))


            state.error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }


            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester)
                    .onFocusChanged {
                        if (it.isFocused) focusedField = FocusField.USERNAME
                    },
                isError = state.usernameError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (state.usernameError != null) {
                Text(
                    text = state.usernameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged {
                        if (it.isFocused) focusedField = FocusField.PASSWORD
                    },
                isError = state.passwordError != null,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }


            if (state.isRegisterMode) {
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmPasswordFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) focusedField = FocusField.CONFIRM_PASSWORD
                        },
                    isError = state.confirmPasswordError != null,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (state.confirmPasswordError != null) {
                    Text(
                        text = state.confirmPasswordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }


            if (state.isRegisterMode) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = viewModel::onCityChange,
                    label = { Text("Default City") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(cityFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) focusedField = FocusField.CITY
                        },
                    isError = state.cityError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                if (state.cityError != null) {
                    Text(
                        text = state.cityError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {
                    if (state.isRegisterMode) {
                        viewModel.register(onLoginSuccess)
                    } else {
                        viewModel.login(onLoginSuccess)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (state.isRegisterMode) "Create Account" else "Login",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TOGGLE MODE BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isRegisterMode) "Already have an account? " else "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = viewModel::toggleRegisterMode) {
                    Text(
                        text = if (state.isRegisterMode) "Login" else "Register",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
