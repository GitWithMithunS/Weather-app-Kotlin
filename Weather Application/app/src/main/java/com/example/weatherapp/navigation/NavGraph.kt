package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.weatherapp.ui.login.LoginScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.city.CityListScreen
import com.example.weatherapp.ui.details.WeatherDetailsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onCitiesClick = {
                    navController.navigate(Routes.Cities.route)
                },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                },
                onWeatherClick = { city, date ->
                    val route = if (date != null) {
                        "${Routes.Details.route}/$city?date=$date"
                    } else {
                        "${Routes.Details.route}/$city"
                    }
                    navController.navigate(route)
                }
            )
        }

        composable(Routes.Cities.route) {
            CityListScreen(
                onCityClick = { city ->
                    navController.navigate("${Routes.Details.route}/$city")
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.Details.route}/{city}?date={date}",
            arguments = listOf(
                navArgument("city") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: ""
            val date = backStackEntry.arguments?.getString("date")
            WeatherDetailsScreen(
                cityName = city,
                date = date,
                onBackClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                },
                onCitiesClick = {
                    navController.navigate(Routes.Cities.route) {
                        popUpTo(Routes.Home.route)
                    }
                },
                onDayClick = { newCity, newDate ->
                    val route = "${Routes.Details.route}/$newCity?date=$newDate"
                    navController.navigate(route) {
                        // Pop up to the details screen for the original city and date
                        val originalRoute = "${Routes.Details.route}/$city?date=$date"
                        popUpTo(originalRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}
