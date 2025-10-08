package com.uitopic.restockmobile.features.resources.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.resources.presentation.InventoryScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.AddCustomSupplyScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.InventoryDetailScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.SupplyFormScreen
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

fun NavGraphBuilder.inventoryNavGraph(navController: NavController) {

    composable("inventory") {
        val viewModel: InventoryViewModel = hiltViewModel()
        InventoryScreen(
            viewModel = viewModel,
            onAddSupplyClick = { navController.navigate("add_custom_supply") },
            onEditSupplyClick = { custom ->
                navController.navigate("edit_custom_supply/${custom.id}")
            },
            onBatchClick = { batchId ->
                navController.navigate("inventory_detail/$batchId")
            }
        )
    }

    composable("add_custom_supply") {
        val viewModel: InventoryViewModel = hiltViewModel()
        AddCustomSupplyScreen(
            onBack = { navController.popBackStack() }
        )
    }

    composable("edit_custom_supply/{customSupplyId}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("customSupplyId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()
        val existingSupply = viewModel.getCustomSupplyById(id)

        SupplyFormScreen(
            existingSupply = existingSupply,
            onBack = { navController.popBackStack() }
        )
    }


    composable("inventory_detail/{batchId}") { backStackEntry ->
        val batchId = backStackEntry.arguments?.getString("batchId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()
        InventoryDetailScreen(
            viewModel = viewModel,
            batchId = batchId,
            onBack = { navController.popBackStack() }
        )
    }
}
