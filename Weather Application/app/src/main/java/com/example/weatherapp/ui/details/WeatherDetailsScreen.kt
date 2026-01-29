package com.example.weatherapp.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: WeatherDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(cityName) {
        viewModel.loadWeather(cityName)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                username = state.city,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = BottomNavItem.CITIES,
                onHomeClick = onHomeClick,
                onCitiesClick = onCitiesClick
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(state.city, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                DetailUnitOption("°C", !state.isFahrenheit) { if (state.isFahrenheit) viewModel.toggleUnit() }
                                DetailUnitOption("°F", state.isFahrenheit) { if (!state.isFahrenheit) viewModel.toggleUnit() }
                            }
                        }
                    }
                }

                // CURRENT WEATHER CARD WITH COLORED ICON
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = viewModel.getWeatherIcon(state.description),
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = viewModel.getWeatherColor(state.description)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(state.temperature, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                            }
                            Text(state.description, style = MaterialTheme.typography.titleMedium)
                            Text("Feels like ${state.feelsLike}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item { Text("Hourly Breakdown", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.hourlyData) { hour ->
                            Card(modifier = Modifier.width(100.dp)) {
                                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(hour.time, style = MaterialTheme.typography.bodySmall)

                                    // HOURLY COLORED ICON
                                    Icon(
                                        imageVector = viewModel.getWeatherIcon(hour.description),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp).padding(vertical = 4.dp),
                                        tint = viewModel.getWeatherColor(hour.description)
                                    )

                                    Text(hour.temperature, fontWeight = FontWeight.Bold)
                                    Text(hour.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                item {
                    StatsGrid(state.humidity, state.wind, state.pressure, state.visibility, state.cloudiness)
                }

                item { Text("5-Day Forecast", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }

                items(state.dailyForecast) { day ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(day.day, fontWeight = FontWeight.Bold)
                                Text(day.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = viewModel.getWeatherIcon(day.description),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = viewModel.getWeatherColor(day.description)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("${day.maxTemp} / ${day.minTemp}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... Keep DetailUnitOption, StatsGrid, and StatCard the same as your current file ...
@Composable
fun DetailUnitOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun StatsGrid(h: String, w: String, p: String, v: String, c: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Humidity", h, Modifier.weight(1f))
            StatCard("Wind", w, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Pressure", p, Modifier.weight(1f))
            StatCard("Visibility", v, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}