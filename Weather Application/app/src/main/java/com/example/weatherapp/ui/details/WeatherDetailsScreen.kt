package com.example.weatherapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem


@Composable
fun WeatherDetailsScreen(
    cityName: String,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit
) {
    WeatherDetailsContent(
        cityName = cityName,
        hourly = listOf(
            HourlyWeather("10 AM", 28, "sunny"),
            HourlyWeather("11 AM", 29, "sunny"),
            HourlyWeather("12 PM", 30, "cloudy"),
            HourlyWeather("1 PM", 31, "cloudy")
        ),

        humidity = "65%",
        wind = "12 km/h",
        precipitation = "10%",
        feelsLike = "32°C",
        sunrise = "6:12 AM",
        sunset = "6:45 PM",

        extendedForecast = listOf(
            DailyWeather("Mon", "Sunny", 32, 24, "sunny"),
            DailyWeather("Tue", "Cloudy", 31, 23, "cloudy"),
            DailyWeather("Wed", "Rainy", 30, 22, "rain")
        ),

        onBack = onBackClick,
        onHomeClick = onHomeClick,
        onCitiesClick = onCitiesClick
    )
}


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
    extendedForecast: List<DailyWeather>,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                username = cityName,
                showBackButton = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = BottomNavItem.HOME,
                onHomeClick = onHomeClick,
                onCitiesClick = onCitiesClick
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Hourly Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(hourly) { hour ->
                        Card(
                            modifier = Modifier.width(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(hour.time)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${hour.temp}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
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
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sunrise", style = MaterialTheme.typography.bodySmall)
                            Text(sunrise, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sunset", style = MaterialTheme.typography.bodySmall)
                            Text(sunset, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Extended Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(extendedForecast) { day ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(day.day)
                        Text("${day.maxTemp}° / ${day.minTemp}°")
                    }
                }
            }
        }
    }
}



@Composable
private fun StatsGrid(
    humidity: String,
    wind: String,
    precipitation: String,
    feelsLike: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Humidity",
                value = humidity,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Wind",
                value = wind,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Precipitation",
                value = precipitation,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Feels Like",
                value = feelsLike,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


