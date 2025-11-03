// com/uitopic/restockmobile/features/resources/presentation/screens/SupplyDetailScreen.kt
package com.uitopic.restockmobile.features.resources.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply

//Supply Detail information
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
                title = { Text("Detalle del Insumo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (customSupply == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró el insumo.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(customSupply.supply!!.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Precio: S/. ${customSupply.price}")
                Text("Stock mínimo: ${customSupply.minStock}")
                Text("Stock máximo: ${customSupply.maxStock}")
                Text("Unidad: ${customSupply.unit.name}")

                Spacer(Modifier.height(20.dp))

                // 🔥 Buttons for Edit/Delete HERE
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onEditClick(customSupply) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                        Spacer(Modifier.width(6.dp))
                        Text("Editar")
                    }

                    OutlinedButton(
                        onClick = { onDeleteClick(customSupply) },
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar")
                    }
                }
            }
        }
    }

}
