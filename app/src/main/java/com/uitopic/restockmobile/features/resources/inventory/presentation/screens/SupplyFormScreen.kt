package com.uitopic.restockmobile.features.resources.inventory.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.uitopic.restockmobile.features.resources.inventory.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Supply
import com.uitopic.restockmobile.features.resources.inventory.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.inventory.presentation.viewmodels.InventoryViewModel

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
    var description by remember { mutableStateOf(existingSupply?.description ?: "") }

    val categoryOptions = supplies
        .mapNotNull { it.category }
        .distinct()
        .sorted()

    var selectedCategory by remember { mutableStateOf(existingSupply?.supply?.category ?: "") }

    val unitOptions = listOf("Kilogram" to "kg", "Gram" to "g", "Liter" to "L", "Unit" to "u")
    var selectedUnit by remember { mutableStateOf(unitOptions.first()) }

    val isEditing = existingSupply != null
    val greenColor = Color(0xFF4F8A5B)

    LaunchedEffect(existingSupply, supplies) {
        if (existingSupply != null && supplies.isNotEmpty()) {
            selectedSupply = supplies.find { it.id == existingSupply.supply!!.id }
            minStock = existingSupply.minStock.toString()
            maxStock = existingSupply.maxStock.toString()
            price = existingSupply.price.toString()
            description = existingSupply.description ?: ""
            selectedCategory = existingSupply.supply?.category ?: ""
        }
    }

    val filteredSupplies = if (selectedCategory.isNotEmpty())
        supplies.filter { it.category == selectedCategory }
    else supplies

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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Category", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            DropdownMenuField(
                options = categoryOptions,
                selected = selectedCategory,
                onSelect = {
                    selectedCategory = it
                    selectedSupply = null
                }
            )

            Text("Select Base Supply", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            DropdownMenuField(
                options = filteredSupplies.map { it.name },
                selected = selectedSupply?.name,
                onSelect = { name -> selectedSupply = filteredSupplies.find { it.name == name } }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

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

            Text("Unit", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            DropdownMenuField(
                options = unitOptions.map { it.first },
                selected = selectedUnit.first,
                onSelect = { name -> selectedUnit = unitOptions.first { it.first == name } }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedSupply != null && minStock.isNotBlank() && maxStock.isNotBlank() && price.isNotBlank()) {
                        val newCustom = CustomSupply(
                            id = existingSupply?.id ?: 0,
                            userId = existingSupply?.userId ?: viewModel.getCurrentUserId(),
                            minStock = minStock.toInt(),
                            maxStock = maxStock.toInt(),
                            price = price.toDoubleOrNull() ?: 0.0,
                            supplyId = selectedSupply?.id ?: 0,
                            unit = UnitModel(selectedUnit.first, selectedUnit.second),
                            currencyCode = "PEN",
                            description = description
                        )

                        if (isEditing) viewModel.updateCustomSupply(newCustom)
                        else viewModel.addCustomSupply(newCustom)
                        Log.d("SupplyForm", "Unidad seleccionada: ${selectedUnit.first} (${selectedUnit.second})")
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
