package com.example.weatherapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// -------------------- DATA MODELS (Hardcoded) --------------------

data class HourlyWeather(
    val time: String,
    val temperature: Int
)

data class DailyForecast(
    val date: String,
    val maxTemp: Int,
    val minTemp: Int
)

// -------------------- MAIN SCREEN --------------------

@Composable
fun WeatherDetailsScreen(
    onBack: () -> Unit
) {
    WeatherDetailsContent(
        cityName = "Bangalore",
        hourly = listOf(
            HourlyWeather("10 AM", 28),
            HourlyWeather("11 AM", 29),
            HourlyWeather("12 PM", 30),
            HourlyWeather("1 PM", 31)
        ),
        humidity = "65%",
        wind = "12 km/h",
        precipitation = "10%",
        feelsLike = "32°C",
        sunrise = "6:12 AM",
        sunset = "6:45 PM",
        extendedForecast = listOf(
            DailyForecast("Mon", 32, 24),
            DailyForecast("Tue", 31, 23),
            DailyForecast("Wed", 30, 22)
        ),
        onBack = onBack
    )
}

// -------------------- CONTENT --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherDetailsContent(
    cityName: String,
    hourly: List<HourlyWeather>,
    humidity: String,
    wind: String,
    precipitation: String,
    feelsLike: String,
    sunrise: String,
    sunset: String,
    extendedForecast: List<DailyForecast>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(cityName) }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                })
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text("Hourly Breakdown", style = MaterialTheme.typography.titleMedium)
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(hourly) { hour ->
                        Card(
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(hour.time)
                                Spacer(Modifier.height(4.dp))
                                Text("${hour.temperature}°C")
                            }
                        }
                    }
                }
            }


            item {
                StatsGrid(
                    humidity = humidity,
                    wind = wind,
                    precipitation = precipitation,
                    feelsLike = feelsLike
                )
            }

            item {
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sunrise")
                            Text(sunrise)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sunset")
                            Text(sunset)
                        }
                    }
                }
            }

            item {
                Text("Extended Forecast", style = MaterialTheme.typography.titleMedium)
            }

            items(extendedForecast) { day ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(day.date)
                        Text("${day.maxTemp}° / ${day.minTemp}°")
                    }
                }
            }
        }
    }
}

// -------------------- STATS GRID --------------------

@Composable
private fun StatsGrid(
    humidity: String,
    wind: String,
    precipitation: String,
    feelsLike: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Humidity", humidity)
            StatCard("Wind", wind)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Precipitation", precipitation)
            StatCard("Feels Like", feelsLike)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// -------------------- PREVIEW --------------------

@Preview(showBackground = true)
@Composable
fun WeatherDetailsScreenPreview() {
    MaterialTheme {
        WeatherDetailsScreen(onBack = {})
    }
}
