package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ForecastItemRow(text: String) {

    // Expected format:
    // "Day: 2026-01-29 - 27°C - Clear"

    val parts = text.split(" - ")

    val dateText = parts.getOrNull(0)
        ?.replace("Day:", "")
        ?.trim()
        ?: "--"

    val temperature = parts.getOrNull(1) ?: "--"
    val condition = parts.getOrNull(2) ?: "--"

    // Convert date → Day name (Thu, Fri, etc.)
    val dayName = try {
        val date = LocalDate.parse(dateText)
        date.dayOfWeek.name.take(3)
    } catch (e: Exception) {
        "--"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Day (Thu)
            Text(
                text = dayName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            // Weather Icon (static for now)
            Icon(
                imageVector = Icons.Filled.WbSunny,
                contentDescription = condition,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Temperature
            Text(
                text = temperature,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
