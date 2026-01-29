package com.example.weatherapp.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object WeatherIconMapper {

    fun getWeatherIcon(iconCode: String): ImageVector {
        return when (iconCode.lowercase()) {
            "01d", "01n" -> Icons.Default.WbSunny // Clear sky
            "02d", "02n" -> Icons.Default.Cloud // Few clouds
            "03d", "03n" -> Icons.Default.Cloud // Scattered clouds
            "04d", "04n" -> Icons.Default.Cloud // Broken clouds
            "09d", "09n" -> Icons.Default.WaterDrop // Shower rain
            "10d", "10n" -> Icons.Default.WaterDrop // Rain
            "11d", "11n" -> Icons.Default.Thunderstorm // Thunderstorm
            "13d", "13n" -> Icons.Default.AcUnit // Snow
            "50d", "50n" -> Icons.Default.Dehaze // Mist
            else -> Icons.Default.Cloud
        }
    }

    fun getIconColor(iconCode: String): Color {
        return when (iconCode.lowercase()) {
            "01d", "01n" -> Color(0xFFFFC107) // Sunny yellow
            "09d", "09n", "10d", "10n" -> Color(0xFF6397E5) // Rainy blue
            else -> Color(color = 0xFF6650a4)
        }
    }
}
