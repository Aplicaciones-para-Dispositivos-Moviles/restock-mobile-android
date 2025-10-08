package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailScreen(
    batchId: String,
    viewModel: InventoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val batches by viewModel.batches.collectAsState()

    val batch = batches.find { it.id == batchId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Lote") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
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
                Text("Lote no encontrado", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            BatchDetailContent(batch = batch, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun BatchDetailContent(batch: Batch, modifier: Modifier = Modifier) {
    val customSupply = batch.customSupply
    val supply = customSupply?.supply
    val unit = customSupply?.unit

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Información del Lote", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Divider()

        DetailRow("Nombre del insumo:", supply?.name ?: "Sin nombre")
        DetailRow("Descripción:", supply?.description ?: "-")
        DetailRow("Categoría:", supply?.category ?: "-")
        DetailRow("Perecible:", if (supply?.perishable == true) "Sí" else "No")

        Spacer(Modifier.height(8.dp))
        Text("Datos personalizados del insumo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Divider()

        DetailRow("Stock mínimo:", customSupply?.minStock?.toString() ?: "-")
        DetailRow("Stock máximo:", customSupply?.maxStock?.toString() ?: "-")
        DetailRow("Precio:", customSupply?.price?.toString() ?: "-")
        DetailRow("Unidad:", "${unit?.name ?: "-"} (${unit?.abbreviation ?: ""})")

        Spacer(Modifier.height(8.dp))
        Text("Datos del lote", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Divider()

        DetailRow("Stock actual:", batch.stock.toString())
        DetailRow("Fecha de vencimiento:", batch.expirationDate ?: "-")
        DetailRow("ID del usuario:", batch.userId ?: "-")
        DetailRow("ID del lote:", batch.id)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}