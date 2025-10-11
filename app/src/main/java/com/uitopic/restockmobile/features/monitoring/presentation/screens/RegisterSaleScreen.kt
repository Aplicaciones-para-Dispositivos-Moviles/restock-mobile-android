package com.uitopic.restockmobile.features.monitoring.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.uitopic.restockmobile.features.monitoring.data.DishDataSource
import com.uitopic.restockmobile.features.monitoring.data.SupplyDataSource
import com.uitopic.restockmobile.features.monitoring.domain.model.DishOption
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplyOption
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSaleScreen(
    onBack: () -> Unit
) {
    // Cargar datos desde las fuentes de datos
    val dishOptions = remember { DishDataSource.getDishOptions() }
    val supplyOptions = remember { SupplyDataSource.getSupplyOptions() }

    var isRegistering by remember { mutableStateOf(false) }
    var dishSelections by remember { mutableStateOf<Map<Int, DishSelection>>(emptyMap()) }
    var selections by remember { mutableStateOf<Map<Int, SupplySelection>>(emptyMap()) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showCreationHint by remember { mutableStateOf(true) }
    var registeredSales by remember { mutableStateOf<List<RegisteredSale>>(emptyList()) }
    var nextSaleId by remember { mutableStateOf(1) }

    LaunchedEffect(isRegistering) {
        if (isRegistering) {
            showCreationHint = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            RegisterSaleHeader(
                onMenuClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isRegistering) {
                if (registeredSales.isEmpty()) {
                    item {
                        EmptySaleState(
                            onCreateSale = { isRegistering = true },
                            onClose = onBack
                        )
                    }
                } else {
                    // Mostrar lista de ventas registradas
                    items(registeredSales) { sale ->
                        RegisteredSaleCard(
                            sale = sale,
                            onDelete = {
                                registeredSales = registeredSales.filter { it.id != sale.id }
                            }
                        )
                    }

                    // Botón para crear nueva venta
                    item {
                        Button(
                            onClick = { isRegistering = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create new sale")
                        }
                    }
                }
            } else {
                item {
                    SaleFormCard(
                        dishOptions = dishOptions,
                        supplyOptions = supplyOptions,
                        dishSelections = dishSelections,
                        selections = selections,
                        showCreationHint = showCreationHint,
                        onSelectDish = { dishOption, quantity ->
                            dishSelections = dishSelections + (dishOption.id to DishSelection(dishOption, quantity))
                        },
                        onChangeDishQuantity = { dishOption, quantity ->
                            dishSelections = if (quantity <= 0) {
                                dishSelections - dishOption.id
                            } else {
                                dishSelections + (dishOption.id to DishSelection(dishOption, quantity))
                            }
                        },
                        onChangeSupplyQuantity = { option, quantity ->
                            selections = if (quantity <= 0) {
                                selections - option.id
                            } else {
                                selections + (option.id to SupplySelection(option, quantity))
                            }
                        }
                    )
                }

                item {
                    SaleActionButtons(
                        isAddEnabled = dishSelections.isNotEmpty(),
                        onCancel = {
                            isRegistering = false
                            dishSelections = emptyMap()
                            selections = emptyMap()
                        },
                        onReset = {
                            dishSelections = emptyMap()
                            selections = emptyMap()
                        },
                        onAdd = { showSuccessDialog = true }
                    )
                }
            }
        }
    }

    if (showSuccessDialog) {
        RegisterSaleSuccessDialog(
            dishSelections = dishSelections.values.toList(),
            selections = selections.values.toList(),
            onDismiss = {
                // Calcular el total
                val dishTotal = dishSelections.values.sumOf { it.option.price * it.quantity }
                val supplyTotal = selections.values.sumOf { it.option.unitPrice * it.quantity }
                val subtotal = dishTotal + supplyTotal
                val total = subtotal + (subtotal * 0.08) // Con impuestos

                // Crear nueva venta registrada
                val newSale = RegisteredSale(
                    id = nextSaleId,
                    saleNumber = "SALE-${String.format("%04d", nextSaleId)}",
                    dishSelections = dishSelections.values.toList(),
                    supplySelections = selections.values.toList(),
                    totalCost = total,
                    registeredDate = Date()
                )

                // Agregar al inicio de la lista (más recientes primero)
                registeredSales = listOf(newSale) + registeredSales
                nextSaleId++

                showSuccessDialog = false
                isRegistering = false
                dishSelections = emptyMap()
                selections = emptyMap()
            }
        )
    }
}

@Composable
private fun RegisterSaleHeader(
    onMenuClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlaceholderAvatar()
                Spacer(modifier = Modifier.width(12.dp))
                PlaceholderBrand(modifier = Modifier.weight(1f))
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Register sale",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Complete the details of a new sale to access the inventory update option",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaceholderAvatar() {
    Surface(
        modifier = Modifier.size(48.dp),
        color = Color.White,
        shape = CircleShape,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {}
}

@Composable
private fun PlaceholderBrand(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(16.dp),
            color = Color.White,
            shape = RoundedCornerShape(50)
        ) {}
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(12.dp),
            color = Color.White,
            shape = RoundedCornerShape(50)
        ) {}
    }
}

@Composable
private fun EmptySaleState(
    onCreateSale: () -> Unit,
    onClose: () -> Unit
) {
    CardContainer {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.large),
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "You have no registered sales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Once you register a sale, you will be able to review its information and the additional ingredients required for preparation.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                }
                Button(
                    onClick = onCreateSale,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F8A5B) // Verde
                    )
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create sale")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaleFormCard(
    dishOptions: List<DishOption>,
    supplyOptions: List<SupplyOption>,
    dishSelections: Map<Int, DishSelection>,
    selections: Map<Int, SupplySelection>,
    showCreationHint: Boolean,
    onSelectDish: (DishOption, Int) -> Unit,
    onChangeDishQuantity: (DishOption, Int) -> Unit,
    onChangeSupplyQuantity: (SupplyOption, Int) -> Unit
) {
    CardContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Nueva sección de plato
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Dishes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Select dishes for your sale. Use the + and - buttons to adjust quantities for each dish.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Mostrar platos con controles de cantidad (sin dropdown)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    dishOptions.forEach { option ->
                        DishRow(
                            option = option,
                            quantity = dishSelections[option.id]?.quantity ?: 0,
                            onChangeQuantity = { quantity -> onChangeDishQuantity(option, quantity) }
                        )
                    }
                }

                if (dishSelections.isNotEmpty()) {
                    SummaryCard(
                        title = "Selected dishes",
                        rows = dishSelections.values.map {
                            "${it.option.label} (${it.quantity}x)" to currencyFormatter.format(it.option.price * it.quantity)
                        }
                    )
                }
            }

            HorizontalDivider()

            // Sección existente de insumos
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Additional supplies",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Select the dishes and additional ingredients required for this sale.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    supplyOptions.forEach { option ->
                        SupplyRow(
                            option = option,
                            quantity = selections[option.id]?.quantity ?: 0,
                            onChangeQuantity = { quantity -> onChangeSupplyQuantity(option, quantity) }
                        )
                    }
                }

                if (selections.isNotEmpty()) {
                    SummaryCard(
                        title = "Selected additional supplies",
                        rows = selections.values.map {
                            it.option.name to "${it.quantity} x ${currencyFormatter.format(it.option.unitPrice)}"
                        }
                    )

                    FinancialSummary(
                        dishSelections = dishSelections.values.toList(),
                        selections = selections.values.toList()
                    )
                }

                if (showCreationHint && dishSelections.isEmpty()) {
                    InfoHint(
                        text = "Start by selecting dishes from the dropdown above. Use the + and - buttons to adjust quantities."
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    value: String?,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value ?: "",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true,
                placeholder = {
                    Text(
                        text = if (value.isNullOrEmpty()) placeholder else "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                            contentDescription = "Toggle dropdown",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            expanded = false
                            onOptionSelected(option)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplyRow(
    option: SupplyOption,
    quantity: Int,
    onChangeQuantity: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            QuantityStepper(
                quantity = quantity,
                onQuantityDecrease = { onChangeQuantity((quantity - 1).coerceAtLeast(0)) },
                onQuantityIncrease = { onChangeQuantity(quantity + 1) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Unit price: ${currencyFormatter.format(option.unitPrice)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DishRow(
    option: DishOption,
    quantity: Int,
    onChangeQuantity: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Peruvian dish",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            QuantityStepper(
                quantity = quantity,
                onQuantityDecrease = { onChangeQuantity((quantity - 1).coerceAtLeast(0)) },
                onQuantityIncrease = { onChangeQuantity(quantity + 1) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Unit price: ${currencyFormatter.format(option.price)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onQuantityDecrease,
            enabled = quantity > 0
        ) {
            Icon(imageVector = Icons.Rounded.Remove, contentDescription = "Decrease")
        }
        Surface(
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = quantity.toString(),
                modifier = Modifier
                    .widthIn(min = 36.dp)
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
        IconButton(onClick = onQuantityIncrease) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "Increase")
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    rows: List<Pair<String, String>>
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (MaterialTheme.colorScheme.onSurfaceVariant == Color.Unspecified) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialSummary(
    dishSelections: List<DishSelection>,
    selections: List<SupplySelection>
) {
    val subtotal = (selections.sumOf { it.option.unitPrice * it.quantity } + dishSelections.sumOf { it.option.price * it.quantity })
    val taxes = subtotal * 0.08
    val total = subtotal + taxes

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sale summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            SummaryRow(label = "Subtotal", value = currencyFormatter.format(subtotal))
            SummaryRow(label = "Taxes (8%)", value = currencyFormatter.format(taxes))
            HorizontalDivider()
            SummaryRow(
                label = "Total",
                value = currencyFormatter.format(total),
                emphasize = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (emphasize) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun InfoHint(text: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SaleActionButtons(
    isAddEnabled: Boolean,
    onCancel: () -> Unit,
    onReset: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6B) // Rojo coral
            )
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cancel")
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f)
        ) {
            Text("Reset")
        }
        Button(
            onClick = onAdd,
            enabled = isAddEnabled,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add sale")
        }
    }
}

@Composable
private fun RegisterSaleSuccessDialog(
    dishSelections: List<DishSelection>,
    selections: List<SupplySelection>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        RegisterSaleSuccessContent(
            dishSelections = dishSelections,
            selections = selections,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun RegisterSaleSuccessContent(
    dishSelections: List<DishSelection>,
    selections: List<SupplySelection>,
    onDismiss: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(88.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Sale successfully registered",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The sale and its additional supplies have been saved correctly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Dish",
                    rows = dishSelections.map {
                        it.option.label to currencyFormatter.format(it.option.price)
                    }
                )

                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Additional supplies",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (selections.isEmpty()) {
                            Text(
                                text = "No additional supplies were added.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            selections.forEach { selection ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = selection.option.name,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${selection.quantity} x ${currencyFormatter.format(selection.option.unitPrice)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                FinancialSummary(
                    dishSelections = dishSelections,
                    selections = selections
                )
            }

            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun CardContainer(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}

@Composable
private fun RegisteredSaleCard(
    sale: RegisteredSale,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con número de venta y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sale number",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sale.saleNumber,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete sale",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider()

            // Información del total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total cost",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = currencyFormatter.format(sale.totalCost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Fecha y hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateFormatter.format(sale.registeredDate),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timeFormatter.format(sale.registeredDate),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Resumen de platos
            if (sale.dishSelections.isNotEmpty()) {
                HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Dishes (${sale.dishSelections.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    sale.dishSelections.forEach { dish ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${dish.option.label} x${dish.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = currencyFormatter.format(dish.option.price * dish.quantity),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Resumen de suministros adicionales
            if (sale.supplySelections.isNotEmpty()) {
                HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Additional supplies (${sale.supplySelections.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    sale.supplySelections.forEach { supply ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${supply.option.name} x${supply.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = currencyFormatter.format(supply.option.unitPrice * supply.quantity),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterSaleEmptyPreview() {
    RestockmobileTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            RegisterSaleScreen(onBack = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterSaleFormPreview() {
    RestockmobileTheme {
        val dish = DishOption("Lomo Saltado", 1, 25.90)
        val supplies = listOf(
            SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50),
            SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75),
            SupplyOption(3, "Olive oil", "Extra virgin 500 ml", 7.80)
        )
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            SaleFormCard(
                dishOptions = listOf(dish),
                supplyOptions = supplies,
                dishSelections = mapOf(1 to DishSelection(dish, 1)),
                selections = supplies.take(2).associate { option ->
                    option.id to SupplySelection(option, if (option.id == 1) 2 else 1)
                },
                showCreationHint = false,
                onSelectDish = { _, _ -> },
                onChangeDishQuantity = { _, _ -> },
                onChangeSupplyQuantity = { _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterSaleSuccessPreview() {
    RestockmobileTheme {
        RegisterSaleSuccessContent(
            dishSelections = listOf(
                DishSelection(DishOption("Ceviche", 3, 28.90), 1)
            ),
            selections = listOf(
                SupplySelection(SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50), 2),
                SupplySelection(SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75), 1)
            ),
            onDismiss = {}
        )
    }
}
