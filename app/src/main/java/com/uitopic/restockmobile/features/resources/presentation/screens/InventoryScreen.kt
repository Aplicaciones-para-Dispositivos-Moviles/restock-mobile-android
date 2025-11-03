package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.presentation.screens.components.BatchListSection
import com.uitopic.restockmobile.features.resources.presentation.screens.components.SupplyCatalogSection
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onAddSupplyClick: () -> Unit,
    onViewSupplyDetails: (CustomSupply) -> Unit,
    onBatchClick: (String) -> Unit,
    onAddBatchClick: () -> Unit
) {
    val customSupplies by viewModel.customSupplies.collectAsState()
    val batches by viewModel.batches.collectAsState()
    val supplies by viewModel.supplies.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val customSuppliesWithNames = customSupplies.map { custom ->
        val fullSupply = supplies.find { it.id == custom.supplyId } ?: custom.supply
        custom.copy(supply = fullSupply)
    }

    val filteredSupplies = customSuppliesWithNames.filter {
        it.supply?.name?.contains(searchQuery, ignoreCase = true) == true
    }

    val batchesWithSupplies = batches.map { batch ->
        val fullCustomSupply = customSupplies.find { it.id == batch.customSupply?.id }
        val fullSupply = fullCustomSupply?.let { cs ->
            supplies.find { it.id == cs.supplyId } ?: cs.supply
        }
        batch.copy(customSupply = fullCustomSupply?.copy(supply = fullSupply))
    }

    val filteredBatches = batchesWithSupplies.filter {
        it.customSupply?.supply?.name?.contains(searchQuery, ignoreCase = true) == true
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadAll()
                }
            }
        )
    }

    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

    Scaffold(
        containerColor = whiteColor,
        topBar = {
            TopAppBar(
                title = { Text("Inventory Management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = whiteColor,
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddBatchClick,
                containerColor = greenColor,
                contentColor = whiteColor,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add batch") },
                text = { Text("New batch") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .background(whiteColor),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            SupplyCatalogSection(
                supplies = filteredSupplies,
                onAddSupplyClick = onAddSupplyClick,
                onViewSupplyDetails = onViewSupplyDetails
            )

            Divider(thickness = 1.dp, color = Color.LightGray)

            BatchListSection(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                batches = filteredBatches,
                onBatchClick = onBatchClick
            )
        }
    }
}
