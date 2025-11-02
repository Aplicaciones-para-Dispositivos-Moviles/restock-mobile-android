package com.uitopic.restockmobile.features.resources.orders.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.OrdersView

class OrdersNavigation {
}

sealed class OrdersRoute(val route: String) {
    data object Orders : OrdersRoute("orders")
}

fun NavGraphBuilder.ordersNavGraph(
    navController: NavController
) {
    composable(OrdersRoute.Orders.route) {
        OrdersView(
            //onBack = { navController.popBackStack() }
        )
    }
}
