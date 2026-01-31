package com.example.weatherapp.ui.city

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weatherapp.data.local.room.entity.CityEntity
import com.example.weatherapp.ui.components.AppBottomBar
import com.example.weatherapp.ui.components.AppTopBar
import com.example.weatherapp.ui.components.BottomNavItem

private enum class FocusField {
    ADD_CITY, NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    onCityClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: CityViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var focusedField by rememberSaveable { mutableStateOf(FocusField.NONE) }
    val addCityFocusRequester = remember { FocusRequester() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(Unit) {
        if (focusedField == FocusField.ADD_CITY) {
            addCityFocusRequester.requestFocus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                username = "Search for Cities",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = BottomNavItem.CITIES,
                onHomeClick = onHomeClick,
                onCitiesClick = { /* already on cities */ }
            )
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                OutlinedTextField(
                    value = state.newCity,
                    onValueChange = viewModel::onCityNameChange,
                    label = { Text("Search and Add City") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(addCityFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) focusedField = FocusField.ADD_CITY
                        },
                    singleLine = true,
                    isError = state.error != null

                )
            }

            // Always show the add button if the user is typing
            if (state.newCity.text.isNotBlank()) {
                item {
                    Button(
                        onClick = { viewModel.addCity(state.newCity.text) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add city")
                    }
                }
            }

            // Show suggestions only when the user is typing
            if (state.suggestions.isNotEmpty()) {
                items(state.suggestions) { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.addCity(suggestion) }
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            } else if (state.newCity.text.isBlank()) {
                // Show the rest of the UI only when not searching

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Popular Cities", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.recommendedCities) { city ->
                            Chip(city, onClick = { viewModel.addCity(city) })
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your Cities", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(state.cities) { city ->
                        CityItem(
                            city = city,
                            onClick = { onCityClick(city.cityName) },
                            onDelete = { viewModel.deleteCity(city) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CityItem(
    city: CityEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = city.cityName)

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
