package com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSupplierForSupplyScreen(
    modifier: Modifier = Modifier,
    supplyId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: () -> Unit,
    ordersViewModel: OrdersViewModel = hiltViewModel() // 👈 Solo OrdersViewModel
) {
    val availableBatches by ordersViewModel.availableBatches.collectAsState()
    val isLoading by ordersViewModel.isLoadingBatches.collectAsState()

    var selectedBatches by remember { mutableStateOf<Set<Batch>>(emptySet()) }
    var sortByPriceDesc by remember { mutableStateOf(false) }

    // Cargar batches cuando se monta la screen
    LaunchedEffect(supplyId) {
        ordersViewModel.loadBatchesForSupply(supplyId)
    }

    val sortedBatches = if (sortByPriceDesc) {
        availableBatches.sortedByDescending { it.customSupply?.price ?: 0.0 }
    } else {
        availableBatches.sortedBy { it.customSupply?.price ?: 0.0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create order") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ... resto del UI (igual que antes)

            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (sortedBatches.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No suppliers available for this supply",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedBatches) { batch ->
                        SupplierBatchRow(
                            batch = batch,
                            isSelected = selectedBatches.contains(batch),
                            onToggleSelection = {
                                selectedBatches = if (selectedBatches.contains(batch)) {
                                    selectedBatches - batch
                                } else {
                                    selectedBatches + batch
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL")
                }
                Button(
                    onClick = {
                        ordersViewModel.addMultipleBatchesToOrder(selectedBatches.toList())
                        onNavigateToOrderDetail()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.weight(1f),
                    enabled = selectedBatches.isNotEmpty()
                ) {
                    Text("NEXT")
                }
            }
        }
    }
}

@Composable
fun SupplierBatchRow(
    batch: Batch,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    val supplierName = "Supplier ${batch.userId}"
    val price = batch.customSupply?.price ?: 0.0
    val stock = batch.stock

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = supplierName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = "S/ ${String.format("%.2f", price)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stock.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.7f)
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggleSelection() }
        )
    }
}

@Preview
@Composable
fun SelectSupplierForSupplyPreview() {
    RestockmobileTheme {
        SelectSupplierForSupplyScreen(
            supplyId = 1,
            onNavigateBack = {},
            onNavigateToOrderDetail = {}
        )
    }
}