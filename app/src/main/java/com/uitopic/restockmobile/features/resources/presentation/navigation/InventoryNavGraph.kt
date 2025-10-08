package com.uitopic.restockmobile.features.resources.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uitopic.restockmobile.features.resources.presentation.InventoryScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.InventoryDetailScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.SupplyFormScreen
import com.uitopic.restockmobile.features.resources.presentation.screens.BatchFormScreen
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

fun NavGraphBuilder.inventoryNavGraph(navController: NavController) {

    composable("inventory") {
        val viewModel: InventoryViewModel = hiltViewModel()
        InventoryScreen(
            viewModel = viewModel,
            onAddSupplyClick = { navController.navigate("supply_form") },
            onEditSupplyClick = { custom ->
                navController.navigate("supply_form/${custom.id}")
            },
            onBatchClick = { batchId ->
                navController.navigate("inventory_detail/$batchId")
            },
            onEditBatchClick = { batchId ->
                navController.navigate("edit_batch/$batchId")
            },
            onAddBatchClick = {
                navController.navigate("add_batch")
            }
        )
    }

    // 🟢 Crear insumo
    composable("supply_form") {
        val viewModel: InventoryViewModel = hiltViewModel()
        SupplyFormScreen(
            viewModel = viewModel,
            existingSupply = null,
            onBack = { navController.popBackStack() }
        )
    }

    // 🟢 Editar insumo
    composable("supply_form/{customSupplyId}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("customSupplyId")
        val viewModel: InventoryViewModel = hiltViewModel()
        val existingSupply = viewModel.getCustomSupplyById(id)

        SupplyFormScreen(
            viewModel = viewModel,
            existingSupply = existingSupply,
            onBack = { navController.popBackStack() }
        )
    }

    // 🟢 Detalle del lote
    composable("inventory_detail/{batchId}") { backStackEntry ->
        val batchId = backStackEntry.arguments?.getString("batchId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()
        InventoryDetailScreen(
            viewModel = viewModel,
            batchId = batchId,
            onBack = { navController.popBackStack() }
        )
    }

    // 🟢 Agregar lote
    composable("add_batch") {
        val viewModel: InventoryViewModel = hiltViewModel()
        BatchFormScreen(
            viewModel = viewModel,
            existingBatch = null,
            onBack = { navController.popBackStack() }
        )
    }

    // 🟢 Editar lote
    composable("edit_batch/{batchId}") { backStackEntry ->
        val batchId = backStackEntry.arguments?.getString("batchId") ?: return@composable
        val viewModel: InventoryViewModel = hiltViewModel()

        // ✅ Coleccionamos el flujo de batches correctamente
        val batches by viewModel.batches.collectAsState()

        // ✅ Buscamos el batch ya fuera del flujo
        val existingBatch = batches.find { it.id == batchId }

        BatchFormScreen(
            viewModel = viewModel,
            existingBatch = existingBatch,
            onBack = { navController.popBackStack() }
        )
    }
}
