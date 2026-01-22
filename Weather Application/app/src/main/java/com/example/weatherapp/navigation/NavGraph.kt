package com.example.weatherapp.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.ui.login.LoginScreen
import com.example.weatherapp.ui.login.LoginViewModel
import com.example.weatherapp.ui.signup.SignupScreen
import com.example.weatherapp.ui.signup.SignupViewModel
import com.example.weatherapp.util.ViewModelFactory

@Composable
fun NavGraph(userRepository: UserRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "signup" // Start on Signup for testing
    ) {
        // --- Signup Screen ---
        composable("signup") {
            val signupViewModel: SignupViewModel = viewModel(
                factory = ViewModelFactory(userRepository)
            )
            SignupScreen(
                viewModel = signupViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") // Switches to Login
                }
            )
        }

        // --- Login Screen ---
        composable("login") {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel(
                factory = ViewModelFactory(userRepository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToSignup = {
                    navController.popBackStack() // Goes back to Signup
                },
                onLoginSuccess = {
                    // We will add the Weather Home screen here later!
                    Toast.makeText(context, "Logged Successfully!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}