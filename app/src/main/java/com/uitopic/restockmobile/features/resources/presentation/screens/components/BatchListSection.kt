package com.uitopic.restockmobile.features.resources.presentation.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.domain.models.Batch

@Composable
fun BatchListSection(
    batches: List<Batch>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBatchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val greenColor = Color(0xFF4F8A5B)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Inventory (Batches)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            placeholder = { Text("Search supply or batch...") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(batches) { batch ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(batch.customSupply?.supply?.name ?: "No name", fontWeight = FontWeight.SemiBold)
                            Text("Stock: ${batch.stock}")
                        }
                        IconButton(onClick = { onBatchClick(batch.id) }) {
                            Icon(Icons.Default.Search, contentDescription = "Details", tint = greenColor)
                        }
                    }
                }
            }
        }
    }
}
