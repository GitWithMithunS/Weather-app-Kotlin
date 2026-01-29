package com.example.weatherapp.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

object WeatherIconMapper {

    fun getWeatherIcon(iconCode: String): ImageVector {
        return when (iconCode.lowercase()) {
            "01d", "01n" -> Icons.Default.WbSunny
            "02d", "02n" -> Icons.Default.Cloud
            "03d", "03n" -> Icons.Default.Cloud
            "04d", "04n" -> Icons.Default.Cloud
            "09d", "09n" -> Icons.Default.WaterDrop
            "10d", "10n" -> Icons.Default.WaterDrop
            "11d", "11n" -> Icons.Default.Thunderstorm
            "13d", "13n" -> Icons.Default.AcUnit
            "50d", "50n" -> Icons.Default.Dehaze
            else -> Icons.Default.Cloud
        }
    }
}
