package com.example.weatherapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem


@Composable
fun WeatherDetailsScreen(
    cityName: String,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit,
    viewModel: WeatherDetailsViewModel = hiltViewModel()  // ← GET VIEWMODEL
) {
    val state by viewModel.uiState.collectAsState()  // ← COLLECT STATE FROM VIEWMODEL

    // ← THIS IS THE KEY! Load weather when city changes
    LaunchedEffect(cityName) {
        viewModel.loadWeather(cityName)
    }

    WeatherDetailsContent(
        state = state,
        onBack = onBackClick,
        onHomeClick = onHomeClick,
        onCitiesClick = onCitiesClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherDetailsContent(
    state: WeatherDetailsUiState,  // ← USE STATE FROM VIEWMODEL, NOT HARDCODED DATA
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                username = state.city,  // ← USE REAL CITY NAME
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

        // ← SHOW LOADING
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // ← SHOW ERROR
        else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        // ← SHOW DATA
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ===== CURRENT WEATHER CARD =====
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.temperature,  // ← REAL TEMPERATURE
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = state.description,  // ← REAL DESCRIPTION
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Feels like ${state.feelsLike}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // ===== HOURLY BREAKDOWN =====
                item {
                    Text(
                        text = "Hourly Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (state.hourlyData.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(state.hourlyData) { hour ->  // ← REAL HOURLY DATA
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
                                            hour.temperature,  // ← REAL TEMP
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            hour.description,  // ← REAL DESCRIPTION
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text("No hourly data available")
                    }
                }

                // ===== STATS GRID =====
                item {
                    StatsGrid(
                        humidity = state.humidity,  // ← REAL DATA
                        wind = state.wind,  // ← REAL DATA
                        feelsLike = state.feelsLike,  // ← REAL DATA
                        pressure = state.pressure,  // ← REAL DATA
                        visibility = state.visibility,  // ← REAL DATA
                        cloudiness = state.cloudiness  // ← REAL DATA
                    )
                }

                // ===== SUNRISE/SUNSET =====
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
                                Text(state.sunrise, style = MaterialTheme.typography.titleMedium)  // ← REAL DATA
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Sunset", style = MaterialTheme.typography.bodySmall)
                                Text(state.sunset, style = MaterialTheme.typography.titleMedium)  // ← REAL DATA
                            }
                        }
                    }
                }

                // ===== 5-DAY FORECAST =====
                item {
                    Text(
                        text = "5-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(state.dailyForecast) { day ->  // ← REAL DAILY DATA
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(day.day, style = MaterialTheme.typography.titleSmall)
                                Text(day.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${day.maxTemp} / ${day.minTemp}")
                        }
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
    feelsLike: String,
    pressure: String,
    visibility: String,
    cloudiness: String
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
                title = "Pressure",
                value = pressure,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Visibility",
                value = visibility,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Cloudiness",
                value = cloudiness,
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