package com.uitopic.restockmobile.features.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
<<<<<<< HEAD
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.home.presentation.screens.HomeScreen

sealed class HomeRoute(val route: String) {
    object Home : HomeRoute("home")
=======
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.home.presentation.screens.HomeScreen
import com.uitopic.restockmobile.features.monitoring.presentation.navigation.MonitoringRoute

sealed class HomeRoute(val route: String) {
    data object Home : HomeRoute("home")
>>>>>>> feature/monitoring
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavController
) {
    composable(HomeRoute.Home.route) {
        HomeScreen(
<<<<<<< HEAD
            navController = navController as NavHostController,
            onNavigateToProfile = {
                navController.navigate("profile_graph")
            },
            onNavigateToRecipes = {
                // Abre el grafo de Planning (su startDestination es recipes_list)
                navController.navigate("planning")
            },
            onNavigateToInventory = {
                navController.navigate("inventory")
            },
            onLogout = {
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
=======
            onNavigateToProfile = {
                navController.navigate("profile_graph")
            },
            onNavigateToSales = {
                navController.navigate(MonitoringRoute.Sales.route)
>>>>>>> feature/monitoring
            }
        )
    }
}