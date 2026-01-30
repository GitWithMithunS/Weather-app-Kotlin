package com.example.weatherapp.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem
import com.example.weatherapp.ui.home.TemperatureToggle
import com.example.weatherapp.ui.util.WeatherIconMapper

@Composable
fun WeatherDetailsScreen(
    cityName: String,
    date: String?,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit,
    onDayClick: (String, String) -> Unit,
    viewModel: WeatherDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(cityName, date) {
        if (date != null) {
            viewModel.loadWeatherForDate(cityName, date)
        } else {
            viewModel.loadWeather(cityName)
        }
    }

    WeatherDetailsContent(
        state = state,
        onBack = onBackClick,
        onHomeClick = onHomeClick,
        onCitiesClick = onCitiesClick,
        onDayClick = {
            onDayClick(cityName, it)
        },
        onToggleTemperature = viewModel::toggleTemperatureUnit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherDetailsContent(
    state: WeatherDetailsUiState,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit,
    onDayClick: (String) -> Unit,
    onToggleTemperature: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                username = state.city,
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

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.title,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            TemperatureToggle(isFahrenheit = state.isFahrenheit, onToggle = onToggleTemperature)
                        }
                    }

                    /* ===== CURRENT WEATHER ===== */
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = WeatherIconMapper.getWeatherIcon(state.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(72.dp),
                                        tint = WeatherIconMapper.getIconColor(state.icon)
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = state.temperature,
                                        style = MaterialTheme.typography.displayLarge
                                    )

                                    Text(
                                        text = state.description,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeviceThermostat,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Feels like ${state.feelsLike}")
                                    }
                                }
                            }
                        }
                    }

                    /* ===== HOURLY BREAKDOWN ===== */
                    item {
                        Text(
                            text = "Hourly Breakdown",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(state.hourlyData) { hour ->
                                    Card(
                                        modifier = Modifier.width(120.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(hour.time)

                                                Spacer(Modifier.height(4.dp))

                                                Icon(
                                                    imageVector = WeatherIconMapper.getWeatherIcon(hour.icon),
                                                    contentDescription = null,
                                                    tint = WeatherIconMapper.getIconColor(hour.icon)
                                                )

                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    hour.temperature,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    /* ===== STATS GRID ===== */
                    item {
                        StatsGrid(
                            humidity = state.humidity,
                            wind = state.wind,
                            pressure = state.pressure,
                            visibility = state.visibility,
                            cloudiness = state.cloudiness
                        )
                    }

                    /* ===== SUNRISE / SUNSET ===== */
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
                                SunInfo(
                                    title = "Sunrise",
                                    time = state.sunrise,
                                    icon = Icons.Filled.WbSunny,
                                    tint = Color(0xFFFFC107) // Sunny yellow
                                )
                                SunInfo(
                                    title = "Sunset",
                                    time = state.sunset,
                                    icon = Icons.Filled.LightMode,
                                    tint = Color(0xFFFFC107) // Sunny yellow
                                )
                            }
                        }
                    }

                    /* ===== 5-DAY FORECAST ===== */
                    item {
                        Text(
                            text = "5-Day Forecast",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(state.dailyForecast) { day ->
                        Card(
                            modifier = Modifier.clickable { onDayClick(day.date) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = WeatherIconMapper.getWeatherIcon(day.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = WeatherIconMapper.getIconColor(day.icon)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(day.day)
                                        Text(
                                            day.description,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Text("${day.maxTemp} / ${day.minTemp}")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ================= COMPONENTS ================= */

@Composable
private fun StatsGrid(
    humidity: String,
    wind: String,
    pressure: String,
    visibility: String,
    cloudiness: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Humidity", humidity, Icons.Filled.WaterDrop, Modifier.weight(1f))
            StatCard("Wind", wind, Icons.Filled.Air, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Pressure", pressure, Icons.Filled.Speed, Modifier.weight(1f))
            StatCard("Visibility", visibility, Icons.Filled.Visibility, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Cloudiness", cloudiness, Icons.Filled.Cloud, Modifier.weight(1f), iconTint = Color(0xFF81D4FA))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconTint)
                Spacer(Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SunInfo(
    title: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color.Unspecified
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = tint)
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        Text(time, style = MaterialTheme.typography.titleMedium)
    }
}
