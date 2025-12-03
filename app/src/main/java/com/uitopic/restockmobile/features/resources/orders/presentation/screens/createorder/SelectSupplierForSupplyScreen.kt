package com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch
import com.uitopic.restockmobile.features.resources.inventory.presentation.viewmodels.InventoryViewModel
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSupplierForSupplyScreen(
    modifier: Modifier = Modifier,
    supplyId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: () -> Unit,
    ordersViewModel: OrdersViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val availableBatches by ordersViewModel.availableBatches.collectAsState()
    val isLoading by ordersViewModel.isLoadingBatches.collectAsState()

    val customSupplies by inventoryViewModel.customSupplies.collectAsState()
    val selectedSupply = remember(customSupplies, supplyId) {
        customSupplies.find { it.supplyId == supplyId }
    }
    // ← EXTRAER NOMBRE DEL SUPPLY
    val supplyName = selectedSupply?.supply?.name
        ?: selectedSupply?.description
        ?: "Supply #$supplyId"

    val supplyCategory = selectedSupply?.supply?.category ?: "No category"
    val supplyUnit = selectedSupply?.unit?.name ?: "units"

    val isLoadingSuppliers by ordersViewModel.isLoadingSuppliers.collectAsState()
    val suppliersProfileCache by ordersViewModel.suppliersProfileCache.collectAsState()

    var selectedBatches by remember { mutableStateOf<Set<Batch>>(emptySet()) }

    // Cargar batches cuando se monta la screen
    LaunchedEffect(supplyId) {
        ordersViewModel.loadBatchesForSupply(supplyId)
    }


    //  Agrupar batches por supplier
    val batchesGroupedBySupplier = remember(availableBatches) {
        availableBatches.groupBy { it.userId ?: 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Suppliers") },
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Requesting",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = supplyName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$supplyUnit • $supplyCategory",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                text = "Select one or more suppliers for this supply",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (availableBatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
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
                // Header con información de columnas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Supplier",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = "Stock",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.7f)
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Espacio para checkbox
                }

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(availableBatches) { batch ->

                        // OBTENER PROFILE DEL CACHÉ
                        val supplierProfile = suppliersProfileCache[batch.userId]
                        val supplierName = supplierProfile?.businessName?.takeIf { it.isNotBlank() }
                            ?: "Supplier #${batch.userId}"


                        SupplierBatchRow(
                            batch = batch,
                            supplierName = supplierName,
                            supplierProfile = supplierProfile,
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

            // Información de selección
            if (selectedBatches.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedBatches.size} batch(es) selected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "from ${batchesGroupedBySupplier.filter { (_, batches) ->
                                batches.any { it in selectedBatches }
                            }.size} supplier(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL")
                }
                Button(
                    onClick = {
                        ordersViewModel.addMultipleBatchesToOrder(selectedBatches.toList())
                        onNavigateToOrderDetail()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedBatches.isNotEmpty()
                ) {
                    Text("ADD TO ORDER")
                }
            }
        }
    }
}

@Composable
fun SupplierBatchRow(
    batch: Batch,
    supplierName: String,
    supplierProfile: Profile?,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    val price = batch.customSupply?.price ?: 0.0
    val stock = batch.stock
    val unit = batch.customSupply?.unit?.abbreviation ?: "u"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del supplier
            Column(
                modifier = Modifier.weight(1.2f)
            ) {
                Text(
                    text = supplierName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                supplierProfile?.let { profile ->
                    Text(
                        text = profile.email.takeIf { it.isNotBlank() }
                            ?: profile.phone.takeIf { it.isNotBlank() }
                            ?: "No contact info",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Batch #${batch.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Precio
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Price",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "S/ ${String.format("%.2f", price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Stock
            Column(
                modifier = Modifier.weight(0.7f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stock.toInt()} $unit",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() }
            )
        }
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