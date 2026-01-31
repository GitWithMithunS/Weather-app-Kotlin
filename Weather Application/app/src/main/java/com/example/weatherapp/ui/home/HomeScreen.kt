package com.example.weatherapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem
import com.example.weatherapp.ui.util.WeatherIconMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCitiesClick: () -> Unit,
    onLogout: () -> Unit,
    onWeatherClick: (String, String?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Confirm Logout") },
            text = { Text(text = "Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout(onLogout)
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                username = state.username,
                showLogout = true,
                onLogoutClick = {
                    showLogoutDialog = true
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = BottomNavItem.HOME,
                onHomeClick = {},
                onCitiesClick = onCitiesClick
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
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
                        TemperatureToggle(isFahrenheit = state.isFahrenheit, onToggle = viewModel::toggleTemperatureUnit)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // City
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = state.city,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                item {
                    // Weather Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onWeatherClick(state.city, null) },
                        shape = RoundedCornerShape(20.dp),
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
                            Icon(
                                imageVector = WeatherIconMapper.getWeatherIcon(state.icon),
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(72.dp),
                                tint = WeatherIconMapper.getIconColor(state.icon)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.temperature,
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.description,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                item {
                    // Forecast Title
                    Text(
                        text = "5-Day Forecast",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(state.forecast) { day ->
                    ForecastItemRow(
                        day = day,
                        onClick = { onWeatherClick(state.city, day.date) }
                    )
                }
            }
        }
    }
}

@Composable
fun TemperatureToggle(isFahrenheit: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("", color = if (isFahrenheit) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary)
        Switch(
            checked = isFahrenheit,
            onCheckedChange = { onToggle() },
            thumbContent = {
                Text(if (isFahrenheit) "F" else "C")
            }
        )
        Text("", color = if (!isFahrenheit) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ForecastItemRow(
    day: ForecastItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
