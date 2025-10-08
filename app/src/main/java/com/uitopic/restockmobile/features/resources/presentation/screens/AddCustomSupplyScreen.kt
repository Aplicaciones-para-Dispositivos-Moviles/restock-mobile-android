package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomSupplyScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val supplies by viewModel.supplies.collectAsState()

    var selectedSupply by remember { mutableStateOf<Supply?>(null) }
    var minStock by remember { mutableStateOf("") }
    var maxStock by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Insumo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 🔹 Select de Supply
            Text("Seleccionar insumo base")
            DropdownMenuField(
                options = supplies.map { it.name },
                selected = selectedSupply?.name,
                onSelect = { name -> selectedSupply = supplies.find { it.name == name } }
            )

            OutlinedTextField(
                value = minStock,
                onValueChange = { minStock = it },
                label = { Text("Stock mínimo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxStock,
                onValueChange = { maxStock = it },
                label = { Text("Stock máximo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (selectedSupply != null && minStock.isNotBlank() && maxStock.isNotBlank() && price.isNotBlank()) {
                        val newCustom = CustomSupply(
                            id = "",
                            userId = "demoUser",
                            minStock = minStock.toInt(),
                            maxStock = maxStock.toInt(),
                            price = price.toDouble(),
                            supply = selectedSupply!!,
                            unit = UnitModel("Unidad", "u")
                        )
                        viewModel.addCustomSupply(newCustom)
                        onBack()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}

