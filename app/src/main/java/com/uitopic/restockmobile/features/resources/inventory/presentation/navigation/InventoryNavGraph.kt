package com.uitopic.restockmobile.features.resources.inventory.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.uitopic.restockmobile.features.resources.inventory.presentation.screens.InventoryDetailScreen
import com.uitopic.restockmobile.features.resources.inventory.presentation.screens.SupplyFormScreen
import com.uitopic.restockmobile.features.resources.inventory.presentation.screens.BatchFormScreen
import com.uitopic.restockmobile.features.resources.inventory.presentation.screens.InventoryScreen
import com.uitopic.restockmobile.features.resources.inventory.presentation.screens.SupplyDetailScreen
import com.uitopic.restockmobile.features.resources.inventory.presentation.viewmodels.InventoryViewModel

fun NavGraphBuilder.inventoryNavGraph(navController: NavController) {

    composable("inventory") {
        val viewModel: InventoryViewModel = hiltViewModel()
        InventoryScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onAddSupplyClick = { navController.navigate("supply_form") },
            onViewSupplyDetails = { custom ->
                navController.navigate("supply_detail/${custom.id}")
            },
            onBatchClick = { batchId ->
                navController.navigate("inventory_detail/$batchId")
            },
            onAddBatchClick = {
                navController.navigate("add_batch")
            }
        )
    }

    composable("supply_form") {
        val viewModel: InventoryViewModel = hiltViewModel()
        SupplyFormScreen(
            viewModel = viewModel,
            existingSupply = null,
            onBack = { navController.popBackStack() }
        )
    }

    composable("supply_form/{customSupplyId}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("customSupplyId")!!.toInt()
        val viewModel: InventoryViewModel = hiltViewModel()

        val customSupplies by viewModel.customSupplies.collectAsState()
        val existingSupply = customSupplies.find { it.id == id }

        if (existingSupply == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@composable
        }

        SupplyFormScreen(
            viewModel = viewModel,
            existingSupply = existingSupply,
            onBack = { navController.popBackStack() }
        )
    }

    composable("supply_detail/{customSupplyId}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("customSupplyId")!!.toInt()
        val viewModel: InventoryViewModel = hiltViewModel()

        val customSupplies by viewModel.customSupplies.collectAsState()
        val supply = customSupplies.find { it.id == id }

        if (supply == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@composable
        }

        SupplyDetailScreen(
            customSupply = supply,
            onBack = { navController.popBackStack() },
            onEditClick = { custom ->
                navController.navigate("supply_form/${custom.id}")
            },
            onDeleteClick = { custom ->
                viewModel.deleteCustomSupply(custom)
                navController.popBackStack()
            }
        )
    }

    composable("inventory_detail/{batchId}") { backStackEntry ->
        val batchId = backStackEntry.arguments?.getString("batchId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()

        val batches by viewModel.batches.collectAsState()
        val batch = batches.find { it.id == batchId }

        // Loader mientras batch aún no está cargado
        if (batch == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@composable
        }

        InventoryDetailScreen(
            viewModel = viewModel,
            batchId = batchId,
            onBack = { navController.popBackStack() },
            onEdit = { id ->
                navController.navigate("edit_batch/$id")
            }
        )
    }

    composable("add_batch") {
        val viewModel: InventoryViewModel = hiltViewModel()
        BatchFormScreen(
            viewModel = viewModel,
            existingBatch = null,
            onBack = { navController.popBackStack() }
        )
    }

    composable("edit_batch/{batchId}") { backStackEntry ->
        val batchId = backStackEntry.arguments?.getString("batchId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()

        val batches by viewModel.batches.collectAsState()
        val existingBatch = batches.find { it.id == batchId }

        BatchFormScreen(
            viewModel = viewModel,
            existingBatch = existingBatch,
            onBack = { navController.popBackStack() }
        )
    }
}
