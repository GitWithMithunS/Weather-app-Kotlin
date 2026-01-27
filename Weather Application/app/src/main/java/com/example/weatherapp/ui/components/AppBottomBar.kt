package com.example.weatherapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.LocationCity

enum class BottomNavItem {
    HOME,
    CITIES
}

@Composable
fun AppBottomBar(
    selectedItem: BottomNavItem,
    onHomeClick: () -> Unit,
    onCitiesClick: () -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = selectedItem == BottomNavItem.HOME,
            onClick = onHomeClick,
            label = { Text("Home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = "Home"
                )
            },
        )

        NavigationBarItem(
            selected = selectedItem == BottomNavItem.CITIES,
            onClick = onCitiesClick,
            label = { Text("Cities") },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = "Home"
                )
            },
        )
    }
}
