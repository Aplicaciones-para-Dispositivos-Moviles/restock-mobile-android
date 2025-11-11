package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyDetailScreen(
    customSupply: CustomSupply?,
    onBack: () -> Unit,
    onEditClick: (CustomSupply) -> Unit,
    onDeleteClick: (CustomSupply) -> Unit
) {
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

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onEditClick(customSupply) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(Modifier.width(8.dp))
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = { onDeleteClick(customSupply) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
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
