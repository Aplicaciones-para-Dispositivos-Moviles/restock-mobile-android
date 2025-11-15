package com.uitopic.restockmobile.features.resources.inventory.presentation.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.inventory.domain.models.CustomSupply

@Composable
fun SupplyCatalogSection(
    supplies: List<CustomSupply>,
    onAddSupplyClick: () -> Unit,
    onViewSupplyDetails: (CustomSupply) -> Unit,
    modifier: Modifier = Modifier
) {
    val greenColor = Color(0xFF4F8A5B)
    val whiteColor = Color.White

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Supply Catalog",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onAddSupplyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenColor,
                    contentColor = whiteColor
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(4.dp))
                Text("Supply")
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(supplies) { custom ->
                SupplyCard(custom, onViewSupplyDetails)
            }
        }
    }
}

@Composable
private fun SupplyCard(custom: CustomSupply, onViewSupplyDetails: (CustomSupply) -> Unit) {
    val greenColor = Color(0xFF4F8A5B)

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(160.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                Text(
                    text = custom.supply?.name ?: "Unnamed",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(custom.unit.abbreviation) },
                        leadingIcon = null,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(
                                0xFFEFF9F1
                            )
                        )
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(custom.supply?.category ?: "-") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(
                                0xFFF2F2F2
                            )
                        )
                    )
                }

                Text(
                    text = "Stock: ${custom.minStock} ~ ${custom.maxStock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledIconButton(
                    onClick = { onViewSupplyDetails(custom) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = greenColor)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Details", tint = Color.White)
                }
            }
        }
    }
}

