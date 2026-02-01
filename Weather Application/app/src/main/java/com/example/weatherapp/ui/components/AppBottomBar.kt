package com.example.weatherapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    CITIES(Icons.Default.LocationCity)
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
                    imageVector = BottomNavItem.HOME.icon,
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
                    imageVector = BottomNavItem.CITIES.icon,
                    contentDescription = "Cities"
                )
            },
        )
    }
}
