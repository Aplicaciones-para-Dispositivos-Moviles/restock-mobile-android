package com.uitopic.restockmobile.features.resources.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    LaunchedEffect(customSupplies) {
        Log.d("BatchForm", "🔥 customSupplies loaded: ${customSupplies.size}")
        customSupplies.forEach { cs ->
            Log.d(
                "BatchForm", """
            ➡️ CustomSupply:
                id = ${cs.id}
                supplyId = ${cs.supplyId}
                supplyName = ${cs.supply?.name}
                unitName = ${cs.unit?.name}
                unitAbbr = ${cs.unit?.abbreviation}
                desc = ${cs.description}
        """.trimIndent()
            )
        }
    }
    var selectedCustom by remember { mutableStateOf<CustomSupply?>(null) }
    var stock by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val supplies by viewModel.supplies.collectAsState()
    val isEditing = existingBatch != null

    LaunchedEffect(existingBatch) {
        if (existingBatch != null) {
            selectedCustom = existingBatch.customSupply
            stock = existingBatch.stock.toString()
            expirationDate = existingBatch.expirationDate ?: ""
        }
    }

    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

    Scaffold(
        containerColor = whiteColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "Edit Batch" else "Add Batch", fontWeight = FontWeight.Bold)
                },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isEditing) {
                Text("Select Custom Supply", fontWeight = FontWeight.SemiBold)
                DropdownMenuField(
                    options = customSupplies.map { it.supply?.name ?: "No name" },
                    selected = selectedCustom?.supply?.name,
                    onSelect = { name ->
                        selectedCustom = customSupplies.find { it.supply?.name == name }
                    }
                )
            } else {
                Text("Custom Supply: ${existingBatch!!.customSupply?.supply?.name}", fontWeight = FontWeight.SemiBold)
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = expirationDate,
                onValueChange = { /* disabled manual */ },
                label = { Text("Expiration Date (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Pick date")
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("OK", color = greenColor, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    val datePickerState = rememberDatePickerState()
                    DatePicker(state = datePickerState)
                    LaunchedEffect(datePickerState.selectedDateMillis) {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(millis))
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
                            userId = existingBatch?.userId ?: 1,
                            customSupply = selectedCustom!!,
                            stock = stock.toDouble(),
                            expirationDate = expirationDate.ifBlank { null }
                        )

                        if (isEditing) viewModel.updateBatch(newBatch)
                        else viewModel.createBatch(newBatch)

                        onBack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor,
                    contentColor = whiteColor
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isEditing) "Update Batch" else "Save Batch", fontWeight = FontWeight.Bold)
            }
        }
    }
}
