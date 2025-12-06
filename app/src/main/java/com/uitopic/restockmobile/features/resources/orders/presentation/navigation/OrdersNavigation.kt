package com.uitopic.restockmobile.features.resources.orders.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.uitopic.restockmobile.features.resources.inventory.presentation.viewmodels.InventoryViewModel

sealed class OrdersRoute(val route: String) {
    data object Orders : OrdersRoute("orders")
    object CreateOrderSelectCustomSupply : OrdersRoute("orders/create/search")

    object CreateOrderSelectSupplier : OrdersRoute("orders/create/supplier/{supplyId}") {
        fun createRoute(supplyId: Int) = "orders/create/supplier/$supplyId"
    }

    object CreateOrderDetail : OrdersRoute("orders/create/detail/{supplierId}/{adminRestaurantId}") {
        fun createRoute(supplierId: Int, adminRestaurantId: Int) =
            "orders/create/detail/$supplierId/$adminRestaurantId"
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
    // PANTALLA PRINCIPAL DE ÓRDENES
    composable(OrdersRoute.Orders.route) { backStackEntry ->
        val ordersViewModel: OrdersViewModel = hiltViewModel(backStackEntry)

        OrdersScreen(
            viewModel = ordersViewModel,
            userName = ordersViewModel.getCurrentUsername() ?: "username123",
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

    // 1. SELECCIONAR SUPPLY
    composable(
        route = OrdersRoute.CreateOrderSelectCustomSupply.route
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = hiltViewModel(parentEntry)
        val inventoryViewModel: InventoryViewModel = hiltViewModel()

        SelectCustomSupplyScreen(
            ordersViewModel = ordersViewModel,
            inventoryViewModel = inventoryViewModel,
            onNavigateBack = {
                navController.popBackStack()
            },
            onSupplySelected = { supplyId ->
                navController.navigate(OrdersRoute.CreateOrderSelectSupplier.createRoute(supplyId))
            }
        )
    }

    // 2. SELECCIONAR PROVEEDORES PARA UN SUPPLY
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
        val ordersViewModel: OrdersViewModel = hiltViewModel(parentEntry)
        val inventoryViewModel: InventoryViewModel = hiltViewModel()

        SelectSupplierForSupplyScreen(
            ordersViewModel = ordersViewModel,
            inventoryViewModel = inventoryViewModel,
            supplyId = supplyId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToOrderDetail = {
                // Obtener el supplierId del primer batch seleccionado
                val selectedBatches = ordersViewModel.orderBatchItems.value
                val supplierId = selectedBatches.firstOrNull()?.batch?.userId ?: 0

                navController.navigate(
                    OrdersRoute.CreateOrderDetail.createRoute(supplierId, adminRestaurantId)
                )
            }
        )
    }

    // 3. DETALLES DE LA ORDEN Y CONFIRMACIÓN
    composable(
        route = OrdersRoute.CreateOrderDetail.route,
        arguments = listOf(
            navArgument("supplierId") { type = NavType.IntType },
            navArgument("adminRestaurantId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        //val supplierId = backStackEntry.arguments?.getInt("supplierId") ?: 0
        val adminRestaurantId = backStackEntry.arguments?.getInt("adminRestaurantId") ?: 0

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.Orders.route)
        }
        val ordersViewModel: OrdersViewModel = hiltViewModel(parentEntry)

        CreateOrderScreen(
            viewModel = ordersViewModel,
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
                    inclusive = false
                )
            }
        )
    }

    // 4. PANTALLA DE DETALLE DE ORDEN EXISTENTE
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
        val ordersViewModel: OrdersViewModel = hiltViewModel(parentEntry)

        OrderDetailScreen(
            viewModel = ordersViewModel,
            orderId = orderId,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}