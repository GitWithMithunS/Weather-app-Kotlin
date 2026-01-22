package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.weatherapp.data.local.room.AppDatabase
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.navigation.NavGraph // Make sure this is imported
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Database and Repository
        val database = AppDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())

        enableEdgeToEdge()

        setContent {
            WeatherAppTheme {
                // 2. Load the NavGraph instead of a single screen
                // This handles all navigation logic for you
                NavGraph(userRepository = userRepository)
            }
        }
    }
}