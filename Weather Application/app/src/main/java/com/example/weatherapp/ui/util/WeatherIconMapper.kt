package com.example.weatherapp.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object WeatherIconMapper {

    fun getWeatherIcon(iconCode: String): ImageVector {
        val isNight = iconCode.endsWith("n")

        return when (iconCode.lowercase().dropLast(1)) {
            "01" -> if (isNight) Icons.Default.NightsStay else Icons.Default.WbSunny
            "02", "03", "04" -> Icons.Default.Cloud
            "09", "10" -> Icons.Default.WaterDrop
            "11" -> Icons.Default.Thunderstorm
            "13" -> Icons.Default.AcUnit
            "50" -> Icons.Default.Dehaze
            else -> Icons.Default.Cloud
        }
    }

    @Composable
    fun getIconColor(iconCode: String): Color {
        val isDarkTheme = isSystemInDarkTheme()
        return when (iconCode.lowercase()) {
            "01d" -> Color(0xFFFFC107) // Sunny yellow
            "01n" -> if (isDarkTheme) Color(0xFFF0F0F0) else Color(0xFF455A64) // Moon: light in dark theme, dark in light theme
            "09d", "09n", "10d", "10n" -> Color(0xFF6397E5) // Rainy blue
            else -> MaterialTheme.colorScheme.primary // Use theme color for others
        }
    }
}
