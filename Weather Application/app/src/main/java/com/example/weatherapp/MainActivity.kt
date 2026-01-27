package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.weatherapp.ui.login.LoginScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {

                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    HomeScreen()
                } else {
                    LoginScreen(
                        onLoginClick = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}
