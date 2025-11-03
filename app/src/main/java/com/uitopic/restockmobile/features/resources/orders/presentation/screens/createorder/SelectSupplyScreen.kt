package com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.CreateOrderViewModel
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSupplyScreen(
    modifier: Modifier = Modifier,
    category: String,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToSelectSuppliers: (Int) -> Unit,
    viewModel: CreateOrderViewModel = viewModel()
) {
    val customSupplies by viewModel.customSupplies.collectAsState()
    var selectedSupply by remember { mutableStateOf<com.uitopic.restockmobile.features.resources.domain.models.CustomSupply?>(null) }
    var expandedSupply by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        viewModel.loadUserSuppliesByCategory(category, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create order") },
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
            Text(
                text = "Complete the details of the new order and begin tracking it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Supplies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedSupply,
                onExpandedChange = { expandedSupply = it }
            ) {
                OutlinedTextField(
                    value = selectedSupply?.supply?.name ?: "Select a supply from your list",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupply) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                ExposedDropdownMenu(
                    expanded = expandedSupply,
                    onDismissRequest = { expandedSupply = false }
                ) {
                    customSupplies.forEach { customSupply ->
                        DropdownMenuItem(
                            text = {
                                Text(customSupply.supply?.name ?: customSupply.description)
                            },
                            onClick = {
                                selectedSupply = customSupply
                                expandedSupply = false
                                onNavigateToSelectSuppliers(customSupply.supplyId)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (selectedSupply == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "First select an input to see\nwhich suppliers have it\navailable.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectSupplyPreview() {
    RestockmobileTheme {
        SelectSupplyScreen(
            userId = 2,
            category = "Alimentos",
            onNavigateBack = {},
            onNavigateToSelectSuppliers = {}
        )
    }
}