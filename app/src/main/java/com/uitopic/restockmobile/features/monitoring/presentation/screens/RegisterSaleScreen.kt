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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

private data class SaleDateOption(
    val label: String,
    val date: String // antes LocalDate, ahora String para evitar requerir API 26
)

private data class SaleTimeOption(
    val label: String
)

private data class SupplyOption(
    val id: Int,
    val name: String,
    val description: String,
    val unitPrice: Double
)

private data class SupplySelection(
    val option: SupplyOption,
    val quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSaleScreen(
    onBack: () -> Unit
) {
    val dateOptions = remember {
        listOf(
            SaleDateOption("Mar 03, 2023", "2023-03-03"),
            SaleDateOption("Mar 04, 2023", "2023-03-04"),
            SaleDateOption("Mar 05, 2023", "2023-03-05")
        )
    }
    val timeOptions = remember {
        listOf(
            SaleTimeOption("12:30 pm"),
            SaleTimeOption("01:15 pm"),
            SaleTimeOption("07:45 pm")
        )
    }
    val supplyOptions = remember {
        listOf(
            SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50),
            SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75),
            SupplyOption(3, "Olive oil", "Extra virgin 500 ml", 7.80),
            SupplyOption(4, "Pasta", "Rigatoni, 1 lb box", 3.60),
            SupplyOption(5, "Flour", "00 flour, 1 kg", 2.90)
        )
    }

    var isRegistering by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<SaleDateOption?>(null) }
    var selectedTime by remember { mutableStateOf<SaleTimeOption?>(null) }
    var selections by remember { mutableStateOf<Map<Int, SupplySelection>>(emptyMap()) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showCreationHint by remember { mutableStateOf(true) }

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
                item {
                    EmptySaleState(
                        onCreateSale = { isRegistering = true },
                        onClose = onBack
                    )
                }
            } else {
                item {
                    SaleFormCard(
                        dateOptions = dateOptions,
                        timeOptions = timeOptions,
                        supplyOptions = supplyOptions,
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        selections = selections,
                        showCreationHint = showCreationHint,
                        onSelectDate = { selectedDate = it },
                        onSelectTime = { selectedTime = it },
                        onChangeQuantity = { option, quantity ->
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
                        isAddEnabled = selectedDate != null && selectedTime != null && selections.isNotEmpty(),
                        onCancel = {
                            isRegistering = false
                            selectedDate = null
                            selectedTime = null
                            selections = emptyMap()
                        },
                        onReset = {
                            selectedDate = null
                            selectedTime = null
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
            selectedDate = selectedDate,
            selectedTime = selectedTime,
            selections = selections.values.toList(),
            onDismiss = {
                showSuccessDialog = false
                isRegistering = false
                selectedDate = null
                selectedTime = null
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
                text = "Complete the fields to register a sale. Choose the dates and additional ingredients needed for the service.",
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
                    modifier = Modifier.weight(1f)
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
    dateOptions: List<SaleDateOption>,
    timeOptions: List<SaleTimeOption>,
    supplyOptions: List<SupplyOption>,
    selectedDate: SaleDateOption?,
    selectedTime: SaleTimeOption?,
    selections: Map<Int, SupplySelection>,
    showCreationHint: Boolean,
    onSelectDate: (SaleDateOption) -> Unit,
    onSelectTime: (SaleTimeOption) -> Unit,
    onChangeQuantity: (SupplyOption, Int) -> Unit
) {
    CardContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Dates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Choose the date and time for this sale.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                DropdownSelector(
                    label = "Sale date",
                    value = selectedDate?.label,
                    placeholder = "Select date",
                    icon = Icons.Outlined.CalendarMonth,
                    options = dateOptions.map { it.label },
                    onOptionSelected = { label ->
                        dateOptions.firstOrNull { it.label == label }?.let(onSelectDate)
                    }
                )

                DropdownSelector(
                    label = "Sale time",
                    value = selectedTime?.label,
                    placeholder = "Select time",
                    icon = Icons.Outlined.Schedule,
                    options = timeOptions.map { it.label },
                    onOptionSelected = { label ->
                        timeOptions.firstOrNull { it.label == label }?.let(onSelectTime)
                    }
                )

                if (selectedDate != null || selectedTime != null) {
                    SummaryCard(
                        title = "Selected dates",
                        rows = listOfNotNull(
                            selectedDate?.let { "Sale date" to it.label },
                            selectedTime?.let { "Sale time" to it.label }
                        )
                    )
                }
            }

            Divider()

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
                            onChangeQuantity = { quantity -> onChangeQuantity(option, quantity) }
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
                        selections = selections.values.toList()
                    )
                }

                if (showCreationHint) {
                    InfoHint(
                        text = "Start by selecting a date and the ingredients you need."
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
        Box {
            OutlinedTextField(
                value = value ?: "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable { expanded = !expanded },
                readOnly = true,
                label = { Text(placeholder) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                },
                leadingIcon = {
                    Icon(imageVector = icon, contentDescription = null)
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            expanded = false
                            onOptionSelected(option)
                        }
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    selections: List<SupplySelection>
) {
    val subtotal = selections.sumOf { it.option.unitPrice * it.quantity }
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
            HorizontalDivider() // reemplaza Divider deprecado
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
            modifier = Modifier.weight(1f)
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
    selectedDate: SaleDateOption?,
    selectedTime: SaleTimeOption?,
    selections: List<SupplySelection>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        RegisterSaleSuccessContent(
            selectedDate = selectedDate,
            selectedTime = selectedTime,
            selections = selections,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun RegisterSaleSuccessContent(
    selectedDate: SaleDateOption?,
    selectedTime: SaleTimeOption?,
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
                    title = "Dates",
                    rows = listOfNotNull(
                        selectedDate?.let { "Sale date" to it.label },
                        selectedTime?.let { "Sale time" to it.label }
                    )
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

                FinancialSummary(selections)
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
        val dateOption = SaleDateOption("Mar 03, 2023", "2023-03-03")
        val timeOption = SaleTimeOption("12:30 pm")
        val supplies = listOf(
            SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50),
            SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75),
            SupplyOption(3, "Olive oil", "Extra virgin 500 ml", 7.80)
        )

        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            SaleFormCard(
                dateOptions = listOf(dateOption),
                timeOptions = listOf(timeOption),
                supplyOptions = supplies,
                selectedDate = dateOption,
                selectedTime = timeOption,
                selections = supplies.take(2).associate { option ->
                    option.id to SupplySelection(option, if (option.id == 1) 2 else 1)
                },
                showCreationHint = false,
                onSelectDate = {},
                onSelectTime = {},
                onChangeQuantity = { _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterSaleSuccessPreview() {
    RestockmobileTheme {
        RegisterSaleSuccessContent(
            selectedDate = SaleDateOption("Mar 03, 2023", "2023-03-03"),
            selectedTime = SaleTimeOption("12:30 pm"),
            selections = listOf(
                SupplySelection(SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50), 2),
                SupplySelection(SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75), 1)
            ),
            onDismiss = {}
        )
    }
}
