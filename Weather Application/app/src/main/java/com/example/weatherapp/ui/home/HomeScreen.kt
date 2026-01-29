package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCitiesClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                username = state.username,
                showLogout = true,
                onLogoutClick = {
                    viewModel.logout(onLogout)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
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
                        modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Filled.Cloud,
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
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
                    ForecastItemRow(day)
                }
            }
        }
    }
}

@Composable
private fun ForecastItemRow(day: ForecastItem) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Cloud,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
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
