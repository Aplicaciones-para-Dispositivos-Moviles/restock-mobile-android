package com.uitopic.restockmobile.features.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.home.presentation.screens.HomeScreen
import com.uitopic.restockmobile.features.monitoring.presentation.navigation.MonitoringRoute

sealed class HomeRoute(val route: String) {
    data object Home : HomeRoute("home")
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavController
) {
    composable(HomeRoute.Home.route) {
        HomeScreen(
            onNavigateToProfile = {
                navController.navigate("profile_graph")
            },
            onNavigateToSales = {
                navController.navigate(MonitoringRoute.Sales.route)
            }
        )
    }
}