package com.uitopic.restockmobile.features.resources.orders.presentation.screens.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun OrderBatchItemCard(
    item: OrderBatchItem,
    onQuantityChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    val supplyName = item.batch?.customSupply?.supply?.name ?: "Unknown"
    val supplierName = "Supplier ${item.batch?.userId ?: "?"}"
    val price = item.batch?.customSupply?.price ?: 0.0
    val availableStock = item.batch?.stock ?: 0.0
    val unit = item.batch?.customSupply?.unit?.abbreviation ?: "u"

    //  CÁLCULO EXACTO usando BigDecimal
    val subtotal = remember(price, item.quantity) {
        val priceDecimal = BigDecimal(price)
        val quantityDecimal = BigDecimal(item.quantity)
        priceDecimal.multiply(quantityDecimal)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    var quantityText by remember(item.quantity) {
        mutableStateOf(item.quantity.toString())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Nombre del supply y botón de eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplyName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = supplierName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Available: $availableStock $unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            // Cálculo: Quantity × Price = Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cantidad editable
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        BasicTextField(
                            value = quantityText,
                            onValueChange = { newValue ->
                                quantityText = newValue
                                newValue.toDoubleOrNull()?.let { value ->
                                    if (value > 0 && value <= availableStock) {
                                        onQuantityChange(value)
                                    }
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .width(60.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        )
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Símbolo ×
                Text(
                    text = "×",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Precio
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    //  FORMATO EXACTO CON 2 DECIMALES
                    Text(
                        text = "S/ ${String.format("%.2f", price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Símbolo =
                Text(
                    text = "=",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Total (calculado)
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    //  FORMATO EXACTO CON 2 DECIMALES
                    Text(
                        text = "S/ ${String.format("%.2f", subtotal)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}