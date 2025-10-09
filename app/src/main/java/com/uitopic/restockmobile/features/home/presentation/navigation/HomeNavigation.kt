package com.uitopic.restockmobile.features.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.home.presentation.screens.HomeScreen

sealed class HomeRoute(val route: String) {
    object Home : HomeRoute("home")
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavController
) {
    composable(HomeRoute.Home.route) {
        HomeScreen(
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
            }
        )
    }
}