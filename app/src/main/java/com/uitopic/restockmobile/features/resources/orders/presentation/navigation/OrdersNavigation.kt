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
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.OrdersScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.CreateOrderScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.SelectCategoryScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.SelectSupplierForSupplyScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder.SelectSupplyScreen
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.CreateOrderViewModel
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel

sealed class OrdersRoute(val route: String) {
    data object Orders : OrdersRoute("orders")

    object CreateOrderSelectCategory : OrdersRoute("orders/create/category")
    object CreateOrderSelectSupply : OrdersRoute("orders/create/supply/{category}") {
        fun createRoute(category: String) = "orders/create/supply/$category"
    }
    object CreateOrderSelectSupplier : OrdersRoute("orders/create/supplier/{supplyId}") {
        fun createRoute(supplyId: Int) = "orders/create/supplier/$supplyId"
    }
    object CreateOrderDetail : OrdersRoute("orders/create/detail/{adminRestaurantId}") {
        fun createRoute(adminRestaurantId: Int) = "orders/create/detail/$adminRestaurantId"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.ordersNavGraph(
    navController: NavController,
    adminRestaurantId: Int
) {
    composable(OrdersRoute.Orders.route) {
        val ordersViewModel: OrdersViewModel = viewModel()

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
                navController.navigate(OrdersRoute.CreateOrderSelectCategory.route)
            }
        )
    }

    //===============================================

    // ===== FLUJO DE CREACIÓN DE ORDEN =====
    // Compartiendo el mismo CreateOrderViewModel

    // 1. Seleccionar categoría
    composable(OrdersRoute.CreateOrderSelectCategory.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.CreateOrderSelectCategory.route)
        }
        val createOrderViewModel: CreateOrderViewModel = viewModel(parentEntry)

        SelectCategoryScreen(
            viewModel = createOrderViewModel,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToSelectSupply = { category ->
                navController.navigate(OrdersRoute.CreateOrderSelectSupply.createRoute(category))
            }
        )
    }

    // 2. Seleccionar supply
    composable(
        route = OrdersRoute.CreateOrderSelectSupply.route,
        arguments = listOf(
            navArgument("category") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: "all categories"
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.CreateOrderSelectCategory.route)
        }
        val createOrderViewModel: CreateOrderViewModel = viewModel(parentEntry)

        SelectSupplyScreen(
            viewModel = createOrderViewModel,
            category = category,
            userId = adminRestaurantId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToSelectSuppliers = { supplyId ->
                navController.navigate(OrdersRoute.CreateOrderSelectSupplier.createRoute(supplyId))
            }
        )
    }

    // 3. Seleccionar proveedores para un supply
    composable(
        route = OrdersRoute.CreateOrderSelectSupplier.route,
        arguments = listOf(
            navArgument("supplyId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val supplyId = backStackEntry.arguments?.getInt("supplyId") ?: 0
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.CreateOrderSelectCategory.route)
        }
        val createOrderViewModel: CreateOrderViewModel = viewModel(parentEntry)

        SelectSupplierForSupplyScreen(
            viewModel = createOrderViewModel,
            supplyId = supplyId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToOrderDetail = {
                navController.navigate(OrdersRoute.CreateOrderDetail.route)
            }
        )
    }

    // 4. Detalles de la orden y confirmación
    composable(OrdersRoute.CreateOrderDetail.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(OrdersRoute.CreateOrderSelectCategory.route)
        }
        val createOrderViewModel: CreateOrderViewModel = viewModel(parentEntry)

        CreateOrderScreen(
            viewModel = createOrderViewModel,
            adminRestaurantId = adminRestaurantId,
            onNavigateBack = {
                navController.popBackStack()
            },
            onAddMoreSupplies = {
                // Volver a la selección de categoría para agregar más supplies
                navController.navigate(OrdersRoute.CreateOrderSelectCategory.route) {
                    popUpTo(OrdersRoute.CreateOrderDetail.route) {
                        inclusive = true
                    }
                }
            },
            onRequestSuccess = {
                // Volver a la lista de órdenes y limpiar el back stack
                navController.navigate(OrdersRoute.Orders.route) {
                    popUpTo(OrdersRoute.Orders.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}