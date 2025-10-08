package com.uitopic.restockmobile.features.resources.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onAddSupplyClick: () -> Unit,
    onEditSupplyClick: (CustomSupply) -> Unit,
    onBatchClick: (String) -> Unit,
    onEditBatchClick: (String) -> Unit,
    onAddBatchClick: () -> Unit
) {
    val customSupplies by viewModel.customSupplies.collectAsState()
    val batches by viewModel.batches.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredSupplies = customSupplies.filter {
        it.supply.name.contains(searchQuery, ignoreCase = true)
    }
    val filteredBatches = batches.filter {
        it.customSupply?.supply?.name?.contains(searchQuery, ignoreCase = true) == true
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadAll()
                }
            }
        )
    }

    // 🎨 Colores personalizados
    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

    Scaffold(
        containerColor = whiteColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Inventario",
                        fontWeight = FontWeight.Bold
                    )
                },
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
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar lote") },
                text = { Text("Nuevo lote") }
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
            // 🔹 Sección de insumos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Catálogo de insumos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onAddSupplyClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = greenColor,
                            contentColor = whiteColor
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                        Spacer(Modifier.width(4.dp))
                        Text("Insumo")
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSupplies) { custom ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(custom.supply.name, fontWeight = FontWeight.SemiBold)
                                Text("Precio: S/. ${custom.price}")
                                Text("Stock min: ${custom.minStock}")
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { onEditSupplyClick(custom) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = greenColor)
                                    }
                                    IconButton(onClick = { viewModel.deleteCustomSupply(custom) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Divider(thickness = 1.dp, color = Color.LightGray)

            // 🔹 Sección de lotes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Inventario (Lotes)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    placeholder = { Text("Buscar insumo o lote...") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBatches) { batch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(batch.customSupply?.supply?.name ?: "Sin nombre", fontWeight = FontWeight.SemiBold)
                                    Text("Stock: ${batch.stock}")
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { onBatchClick(batch.id) }) {
                                        Icon(Icons.Default.Search, contentDescription = "Detalles", tint = greenColor)
                                    }
                                    IconButton(onClick = { onEditBatchClick(batch.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = greenColor)
                                    }
                                    IconButton(onClick = { viewModel.deleteBatch(batch.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
