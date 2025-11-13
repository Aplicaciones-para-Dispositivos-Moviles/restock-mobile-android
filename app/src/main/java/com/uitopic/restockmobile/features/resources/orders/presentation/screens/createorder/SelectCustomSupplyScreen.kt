package com.uitopic.restockmobile.features.resources.orders.presentation.screens.createorder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.ui.SupplySearchCard
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCustomSupplyScreen(
    modifier: Modifier = Modifier,
    userId: Int,
    onNavigateBack: () -> Unit,
    onSupplySelected: (Int) -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val customSupplies by viewModel.customSupplies.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Filtrar supplies por búsqueda
    val filteredCustomSupplies = remember(customSupplies, searchQuery) {
        if (searchQuery.isBlank()) {
            customSupplies
        } else {
            customSupplies.filter { customSupply ->
                customSupply.supply?.name?.contains(searchQuery, ignoreCase = true) == true ||
                        customSupply.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserCustomSupplies(userId)
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
        ) {
            // Header
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Search for the supplies you need to order",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                /* OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search supplies (e.g., rice, oil...)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    },
                    singleLine = true
                ) */

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search supplies (e.g., rice, oil...)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    },
                    singleLine = true
                )
            }

            // Lista de resultados
            when {
                customSupplies.isEmpty() -> {
                    // Estado vacío
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No supplies registered yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add supplies to your inventory first",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                filteredCustomSupplies.isEmpty() && searchQuery.isNotEmpty() -> {
                    // Sin resultados
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No results for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    // Lista de supplies
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(filteredCustomSupplies) { customSupply ->
                            SupplySearchCard(
                                supply = customSupply,
                                onClick = { onSupplySelected(customSupply.supplyId) }

                            )
                        }
                    }
                }
            }
        }
    }
}


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCustomSupplyScreen(
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
        viewModel.loadUserCustomSupplies(userId)
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
} */

@Preview
@Composable
fun SelectCustomSupplyPreview() {
    RestockmobileTheme {
        SelectCustomSupplyScreen(
            userId = 2,
            onNavigateBack = {},
            onSupplySelected = {}
        )
    }
}