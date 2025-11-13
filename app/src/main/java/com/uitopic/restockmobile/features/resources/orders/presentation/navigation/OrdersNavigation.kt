package com.uitopic.restockmobile.features.resources.orders.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.currentorders.OrderDetailScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.currentorders.OrdersScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.CreateOrderScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.SelectSupplierForSupplyScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.SelectCustomSupplyScreen

import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel

sealed class OrdersRoute(val route: String) {
    data object Orders : OrdersRoute("orders")
    object CreateOrderSelectCustomSupply : OrdersRoute("orders/create/search")

    object CreateOrderSelectSupplier : OrdersRoute("orders/create/supplier/{supplyId}") {
        fun createRoute(supplyId: Int) = "orders/create/supplier/$supplyId"
    }
    object CreateOrderDetail : OrdersRoute("orders/create/detail/{adminRestaurantId}") {
        fun createRoute(adminRestaurantId: Int) = "orders/create/detail/$adminRestaurantId"
    }

    object OrderDetail : OrdersRoute("orders/detail/{orderId}") {
        fun createRoute(orderId: Int) = "orders/detail/$orderId"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.ordersNavGraph(
    navController: NavController,
    adminRestaurantId: Int
) {
    composable(OrdersRoute.Orders.route) { backStackEntry ->
        val ordersViewModel: OrdersViewModel = viewModel(backStackEntry)

        OrdersScreen(
            viewModel = ordersViewModel,
            userName = "",
            userEmail = "",
            userAvatar = "",
            onNavigateToProfile = {
                navController.navigate("profile_detail")
            },
            onNavigateToInventory = {
                navController.navigate("inventory")
            },
            onNavigateToRecipes = {
                navController.navigate("planning")
            },
            onNavigateToHome = {
                navController.navigate("home")
            },
            onNavigateToSales = {
                navController.navigate("monitoring_sales")
            },
            onLogout = {
                navController.navigate("sign_in")
            },
            onCreateOrder = {
                navController.navigate(OrdersRoute.CreateOrderSelectCustomSupply.route)
            },
            onOrderClick = { orderId ->
                navController.navigate(OrdersRoute.OrderDetail.createRoute(orderId))
            }
        )
    }

    // 1. Seleccionar supply
    composable(
        route = OrdersRoute.CreateOrderSelectCustomSupply.route
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = viewModel(parentEntry)

        SelectCustomSupplyScreen(
            viewModel = ordersViewModel,
            userId = adminRestaurantId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onSupplySelected = { supplyId ->
                navController.navigate(OrdersRoute.CreateOrderSelectSupplier.createRoute(supplyId))
            }
        )
    }

    // 2. Seleccionar proveedores para un supply
    composable(
        route = OrdersRoute.CreateOrderSelectSupplier.route,
        arguments = listOf(
            navArgument("supplyId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val supplyId = backStackEntry.arguments?.getInt("supplyId") ?: 0
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = viewModel(parentEntry)

        SelectSupplierForSupplyScreen(
            viewModel = ordersViewModel,
            supplyId = supplyId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToOrderDetail = {
                navController.navigate(OrdersRoute.CreateOrderDetail.route)
            }
        )
    }

    // 3. Detalles de la orden y confirmación
    composable(
        route = OrdersRoute.CreateOrderDetail.route
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = viewModel(parentEntry)

        CreateOrderScreen(
            viewModel = ordersViewModel,
            adminRestaurantId = adminRestaurantId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onAddMoreSupplies = {
                navController.navigate(OrdersRoute.CreateOrderSelectCustomSupply.route) {
                    popUpTo(OrdersRoute.CreateOrderDetail.route) {
                        inclusive = false
                    }
                }
            },
            onRequestSuccess = {
                navController.popBackStack(
                    route = OrdersRoute.Orders.route,
                    inclusive = false  // NO destruir Orders
                )
            }
        )
    }

    // 4. Pantalla de detalle de orden existente
    composable(
        route = OrdersRoute.OrderDetail.route,
        arguments = listOf(
            navArgument("orderId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = viewModel(parentEntry)

        OrderDetailScreen(
            viewModel = ordersViewModel,
            orderId = orderId,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}