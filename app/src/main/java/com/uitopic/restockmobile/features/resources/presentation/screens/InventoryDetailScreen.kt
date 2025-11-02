package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

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
    val customSupplies by viewModel.customSupplies.collectAsState()
    val supplies by viewModel.supplies.collectAsState()

    // Encuentra el batch
    val batchRaw = batches.find { it.id == batchId }

    // Mapea batch con CustomSupply y Supply completos
    val batch = batchRaw?.let { b ->
        val fullCustomSupply = customSupplies.find { it.id == b.customSupply?.id }
        val fullSupply = fullCustomSupply?.let { cs ->
            supplies.find { it.id == cs.supplyId } ?: cs.supply
        }
        b.copy(customSupply = fullCustomSupply?.copy(supply = fullSupply))
    }

    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

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
                onEdit = { onEdit(batch.id) },
                onDelete = {
                    viewModel.deleteBatch(batch.id)
                    onBack()
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun BatchDetailContent(batch: Batch, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
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
                Text("Supply Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
                Text("Custom Supply Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
                Text("Batch Data", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Divider()

                DetailRow("Current stock:", batch.stock.toString())
                DetailRow("Expiration date:", batch.expirationDate ?: "-")
                DetailRow("User ID:", (batch.userId ?: 1).toString())
                DetailRow("Batch ID:", batch.id)
            }
        }

        Spacer(Modifier.height(12.dp))

        // === Actions ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(Modifier.width(6.dp))
                Text("Delete")
            }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = greenColor, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(Modifier.width(6.dp))
                Text("Edit")
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
