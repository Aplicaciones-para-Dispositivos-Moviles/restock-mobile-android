package com.uitopic.restockmobile.features.monitoring.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.monitoring.presentation.screens.RegisterSaleScreen

sealed class MonitoringRoute(val route: String) {
    data object Sales : MonitoringRoute("monitoring_sales")
}

fun NavGraphBuilder.monitoringNavGraph(
    navController: NavController
) {
    composable(MonitoringRoute.Sales.route) {
        RegisterSaleScreen(
            onBack = { navController.popBackStack() }
        )
    }
}
