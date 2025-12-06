package com.uitopic.restockmobile.features.resources.inventory.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.inventory.domain.models.CustomSupply

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyDetailScreen(
    customSupply: CustomSupply?,
    onBack: () -> Unit,
    onEditClick: (CustomSupply) -> Unit,
    onDeleteClick: (CustomSupply) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Estado del menú desplegable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supply Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                expanded = false
                                customSupply?.let { onEditClick(it) }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false
                                customSupply?.let { onDeleteClick(it) }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (customSupply == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Supply not found.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Supply Title
            Text(
                text = customSupply.supply?.name ?: "Unnamed Supply",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoItem("Description", customSupply.description)
                    InfoItem("Min Stock", "${customSupply.minStock}")
                    InfoItem("Max Stock", "${customSupply.maxStock}")
                    InfoItem("Unit", customSupply.unit.name)
                    InfoItem("Unit Abbreviation", customSupply.unit.abbreviation)
                }
            }

            // Eliminamos los botones inferiores
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
