package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun ForecastItem(item: ForecastDay) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${item.day}, ${item.date}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = item.temp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
