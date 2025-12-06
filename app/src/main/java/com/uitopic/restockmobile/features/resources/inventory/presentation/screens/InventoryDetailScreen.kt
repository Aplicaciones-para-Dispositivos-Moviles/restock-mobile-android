package com.uitopic.restockmobile.features.resources.inventory.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch
import com.uitopic.restockmobile.features.resources.inventory.presentation.viewmodels.InventoryViewModel

//Inventory detail information
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailScreen(
    batchId: String,
    viewModel: InventoryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {}
) {
    val batches by viewModel.batches.collectAsState()
    val batch = batches.find { it.id == batchId }

    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

    var menuExpanded by remember { mutableStateOf(false) } // Menú de 3 puntos
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = whiteColor,
        topBar = {
            TopAppBar(
                title = { Text("Batch Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = greenColor)
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                batch?.let { onEdit(it.id) }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                menuExpanded = false
                                showDeleteDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = whiteColor,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        if (batch == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Batch not found", color = Color.Gray)
            }
        } else {
            BatchDetailContent(
                batch = batch,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && batch != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete batch") },
            text = { Text("Are you sure you want to delete this batch? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteBatch(batch.id)
                        onBack()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BatchDetailContent(batch: Batch, modifier: Modifier = Modifier) {
    val customSupply = batch.customSupply
    val supply = customSupply?.supply
    val unit = customSupply?.unit
    val greenColor = Color(0xFF4F8A5B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // === Supply Info Card ===
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Supply Information",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()

                DetailRow("Name:", supply?.name ?: "-")
                DetailRow("Description:", supply?.description ?: "-")
                DetailRow("Category:", supply?.category ?: "-")
                DetailRow("Perishable:", if (supply?.perishable == true) "Yes" else "No")
            }
        }

        // === Custom Supply Info ===
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Custom Supply Details",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()

                DetailRow("Min stock:", customSupply?.minStock?.toString() ?: "-")
                DetailRow("Max stock:", customSupply?.maxStock?.toString() ?: "-")
                DetailRow("Price:", customSupply?.price?.toString() ?: "-")
                DetailRow("Unit:", "${unit?.name ?: "-"} (${unit?.abbreviation ?: ""})")
            }
        }

        // === Batch Info ===
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Batch Data",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()

                DetailRow("Current stock:", batch.stock.toString())

                // Solo mostrar Expiration date si es perecible
                if (supply?.perishable == true) {
                    DetailRow("Expiration date:", batch.expirationDate ?: "-")
                }

                DetailRow("Batch ID:", batch.id)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
