package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.presentation.viewmodels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchFormScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    existingBatch: Batch? = null,
    onBack: () -> Unit
) {
    val customSupplies by viewModel.customSupplies.collectAsState()

    var selectedCustom by remember { mutableStateOf<CustomSupply?>(null) }
    var stock by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }

    // 👇 Control del calendario
    var showDatePicker by remember { mutableStateOf(false) }

    val isEditing = existingBatch != null

    LaunchedEffect(existingBatch) {
        if (existingBatch != null) {
            selectedCustom = existingBatch.customSupply
            stock = existingBatch.stock.toString()
            expirationDate = existingBatch.expirationDate ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Batch" else "Add Batch") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isEditing) {
                Text("Select Custom Supply")
                DropdownMenuField(
                    options = customSupplies.map { it.supply.name },
                    selected = selectedCustom?.supply?.name,
                    onSelect = { name ->
                        selectedCustom = customSupplies.find { it.supply.name == name }
                    }
                )
            } else {
                Text("Custom Supply: ${existingBatch!!.customSupply?.supply?.name}")
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🗓 Campo con calendario
            OutlinedTextField(
                value = expirationDate,
                onValueChange = { /* deshabilitado manual */ },
                label = { Text("Expiration Date (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false, // desactiva escritura directa
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Pick date")
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    val datePickerState = rememberDatePickerState()
                    DatePicker(state = datePickerState)

                    LaunchedEffect(datePickerState.selectedDateMillis) {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.text.SimpleDateFormat("yyyy-MM-dd")
                                .format(java.util.Date(millis))
                            expirationDate = date
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedCustom != null && stock.isNotBlank()) {
                        val newBatch = Batch(
                            id = existingBatch?.id ?: "",
                            userId = existingBatch?.userId ?: "demoUser",
                            customSupply = selectedCustom!!,
                            stock = stock.toInt(),
                            expirationDate = expirationDate.ifBlank { null }
                        )

                        if (isEditing) {
                            viewModel.updateBatch(newBatch)
                        } else {
                            viewModel.createBatch(newBatch)
                        }

                        onBack()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isEditing) "Update" else "Save")
            }
        }
    }
}


