package com.uitopic.restockmobile.features.resources.inventory.presentation.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch

@Composable
fun BatchListSection(
    batches: List<Batch>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBatchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val greenColor = Color(0xFF4F8A5B)
    val redColor = Color(0xFFD9534F)
    val grayColor = Color(0xFF9E9E9E)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Inventory (Batches)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            placeholder = { Text("Search supply or batch...") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(batches) { batch ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = batch.customSupply?.supply?.name ?: "No name",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (batch.expirationDate == "9999-12-31") {
                                    Text(
                                        text = "Non-perishable",
                                        color = greenColor,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else if (batch.customSupply?.supply?.perishable == true) {
                                    Text(
                                        text = "Perishable",
                                        color = redColor,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            IconButton(onClick = { onBatchClick(batch.id) }) {
                                Icon(Icons.Default.Visibility, contentDescription = "View details", tint = greenColor)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Stock: ${batch.stock}", fontWeight = FontWeight.SemiBold)
                            batch.customSupply?.let { cs ->
                                Text("Min: ${cs.minStock}", color = grayColor, style = MaterialTheme.typography.bodySmall)
                                Text("Max: ${cs.maxStock}", color = grayColor, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        batch.customSupply?.let { cs ->
                            Text(
                                "Unit: ${cs.unit.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = grayColor
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = batch.customSupply?.supply?.category ?: "-",
                                style = MaterialTheme.typography.bodySmall,
                                color = grayColor
                            )
                            val expText = if (batch.expirationDate == "9999-12-31") {
                                "Non-perishable"
                            } else {
                                "Expires: ${batch.expirationDate ?: "-"}"
                            }
                            Text(
                                text = expText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (batch.expirationDate == "9999-12-31") greenColor else redColor
                            )
                        }
                    }
                }
            }
        }
    }
}
