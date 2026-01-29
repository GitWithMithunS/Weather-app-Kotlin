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
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onCitiesClick = {
                    navController.navigate(Routes.CITIES)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onWeatherClick = { city, date ->
                    val route = if (date != null) {
                        "${Routes.DETAILS}/$city?date=$date"
                    } else {
                        "${Routes.DETAILS}/$city"
                    }
                    navController.navigate(route)
                }
            )
        }

        composable(Routes.CITIES) {
            CityListScreen(
                onCityClick = { city ->
                    navController.navigate("${Routes.DETAILS}/$city")
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.DETAILS}/{city}?date={date}",
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
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onCitiesClick = {
                    navController.navigate(Routes.CITIES) {
                        popUpTo(Routes.HOME)
                    }
                },
                onDayClick = { newCity, newDate ->
                    navController.navigate("${Routes.DETAILS}/$newCity?date=$newDate") {
                        popUpTo("${Routes.DETAILS}/$city?date=$date") { inclusive = true }
                    }
                }
            )
        }
    }
}
