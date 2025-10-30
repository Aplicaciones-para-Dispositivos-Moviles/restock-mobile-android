package com.uitopic.restockmobile.features.resources.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyFormScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    existingSupply: CustomSupply? = null,
    onBack: () -> Unit
) {
    val supplies by viewModel.supplies.collectAsState()

    var selectedSupply by remember { mutableStateOf<Supply?>(null) }
    var minStock by remember { mutableStateOf("") }
    var maxStock by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val isEditing = existingSupply != null
    val greenColor = Color(0xFF4F8A5B)

    LaunchedEffect(existingSupply, supplies) {
        if (existingSupply != null && supplies.isNotEmpty()) {
            selectedSupply = supplies.find { it.id == existingSupply.supply!!.id }
            minStock = existingSupply.minStock.toString()
            maxStock = existingSupply.maxStock.toString()
            price = existingSupply.price.toString()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Supply" else "Add Supply",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowCircleLeft,
                            contentDescription = "Back",
                            tint = greenColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // --- Supply selection ---
            if (!isEditing) {
                Log.d("SupplyFormScreen", "Selected supply: $selectedSupply")
                Text(
                    text = "Select Base Supply",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                DropdownMenuField(
                    options = supplies.map { it.name },
                    selected = selectedSupply?.name,
                    onSelect = { name ->
                        selectedSupply = supplies.find { it.name == name }
                    }
                )
            } else {
                Log.d("SupplyFormScreen", "Selected supply: $selectedSupply")
                Log.d("SupplyFormScreen", "Base Supply: $existingSupply")
                // --- Supply selection ---
                Text(
                    text = "Select Base Supply",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                DropdownMenuField(
                    options = supplies.map { it.name },
                    selected = selectedSupply?.name,
                    onSelect = { name ->
                        selectedSupply = supplies.find { it.name == name }
                    }
                )
            }

            // --- Form fields ---
            OutlinedTextField(
                value = minStock,
                onValueChange = { minStock = it },
                label = { Text("Minimum Stock") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = maxStock,
                onValueChange = { maxStock = it },
                label = { Text("Maximum Stock") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    Log.d("SupplyFormScreen", "Selected supply: $selectedSupply")
                    if (selectedSupply != null && minStock.isNotBlank() && maxStock.isNotBlank() && price.isNotBlank()) {
                        val newCustom = CustomSupply(
                            id = existingSupply?.id ?: 0,
                            userId = existingSupply?.userId ?: 1,
                            minStock = minStock.toInt(),
                            maxStock = maxStock.toInt(),
                            price = price.toDouble(),
                            supplyId = selectedSupply?.id ?: 0,
                            supply = selectedSupply,
                            unit = existingSupply?.unit ?: UnitModel("Unit", "u"),
                            currencyCode = "PEN",
                            description = "Insumo por defecto",
                        )

                        if (isEditing) viewModel.updateCustomSupply(newCustom)
                        else viewModel.addCustomSupply(newCustom)

                        onBack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth(0.4f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (isEditing) "Update" else "Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DropdownMenuField(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            label = { Text("Select") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            shape = MaterialTheme.shapes.medium
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
