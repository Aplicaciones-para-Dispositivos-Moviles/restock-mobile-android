package com.uitopic.restockmobile.features.planning.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeFormEvent
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeFormState
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeSupplyItem
import com.uitopic.restockmobile.features.planning.presentation.viewmodels.RecipesViewModel
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    recipeId: Int? = null,
    customSupplies: List<CustomSupply>,
    viewModel: RecipesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onUploadImage: suspend (Uri) -> String
) {
    val formState by viewModel.formState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showSupplyDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            viewModel.loadRecipeForEdit(recipeId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Create Recipe" else "Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(Icons.Default.Close, "Cancel")
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
            // Progress Indicator
            LinearProgressIndicator(
                progress = { formState.currentStep / 2f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (formState.currentStep) {
                1 -> RecipeInfoStep(
                    formState = formState,
                    customSupplies = customSupplies,
                    onEvent = viewModel::onFormEvent,
                    onShowSupplyDialog = { showSupplyDialog = true }
                )
                2 -> RecipeImageStep(
                    formState = formState,
                    onEvent = viewModel::onFormEvent,
                    onUploadImage = onUploadImage
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (formState.currentStep > 1) {
                    OutlinedButton(
                        onClick = { viewModel.onFormEvent(RecipeFormEvent.PreviousStep) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Back")
                    }
                    Spacer(Modifier.width(16.dp))
                }

                Button(
                    onClick = {
                        if (formState.currentStep < 2) {
                            viewModel.onFormEvent(RecipeFormEvent.NextStep)
                        } else {
                            viewModel.onFormEvent(RecipeFormEvent.Submit)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !formState.isLoading
                ) {
                    if (formState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (formState.currentStep < 2) "Next" else "Save Recipe")
                        if (formState.currentStep < 2) {
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }
                }
            }
        }
    }

    // Supply Selection Dialog
    if (showSupplyDialog) {
        SupplySelectionDialog(
            customSupplies = customSupplies,
            selectedSupplies = formState.supplies.map { it.supplyId },
            onDismiss = { showSupplyDialog = false },
            onSupplySelected = { supply, quantity ->
                viewModel.onFormEvent(
                    RecipeFormEvent.AddSupply(
                        RecipeSupplyItem(
                            supplyId = supply.id.toInt(),
                            supplyName = supply.supply.name,
                            quantity = quantity,
                            unit = supply.unit.name
                        )
                    )
                )
                showSupplyDialog = false
            }
        )
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            icon = { Icon(Icons.Default.Warning, null) },
            title = { Text("Cancel Recipe") },
            text = { Text("Are you sure you want to cancel? All unsaved changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onFormEvent(RecipeFormEvent.Cancel)
                        onNavigateBack()
                    }
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Continue Editing")
                }
            }
        )
    }

    // Error Display
    formState.error?.let { error ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { /* Dismiss */ }) {
                    Text("OK")
                }
            }
        ) {
            Text(error)
        }
    }
}

@Composable
fun RecipeInfoStep(
    formState: RecipeFormState,
    customSupplies: List<CustomSupply>,
    onEvent: (RecipeFormEvent) -> Unit,
    onShowSupplyDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Recipe Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = formState.name,
                onValueChange = { onEvent(RecipeFormEvent.NameChanged(it)) },
                label = { Text("Recipe Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = formState.description,
                onValueChange = { onEvent(RecipeFormEvent.DescriptionChanged(it)) },
                label = { Text("Description*") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }

        item {
            OutlinedTextField(
                value = formState.price,
                onValueChange = { onEvent(RecipeFormEvent.PriceChanged(it)) },
                label = { Text("Price (S/)*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Text("S/") }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Supplies*",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onShowSupplyDialog,
                    enabled = customSupplies.isNotEmpty()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Supply")
                }
            }
        }

        if (customSupplies.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "No supplies available. Please create supplies first in the Inventory section.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        items(formState.supplies) { supply ->
            SupplyItemCard(
                supply = supply,
                onQuantityChange = { newQuantity ->
                    onEvent(RecipeFormEvent.UpdateSupplyQuantity(supply.supplyId, newQuantity))
                },
                onRemove = {
                    onEvent(RecipeFormEvent.RemoveSupply(supply.supplyId))
                }
            )
        }
    }
}

@Composable
fun RecipeImageStep(
    formState: RecipeFormState,
    onEvent: (RecipeFormEvent) -> Unit,
    onUploadImage: suspend (Uri) -> String
) {
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploading = true
                try {
                    val imageUrl = onUploadImage(it)
                    onEvent(RecipeFormEvent.ImageUrlChanged(imageUrl))
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Recipe Image",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (formState.imageUrl?.isNotBlank() == true) {
            AsyncImage(
                model = formState.imageUrl,
                contentDescription = "Recipe image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No image selected",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            enabled = !isUploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Uploading...")
            } else {
                Icon(Icons.Default.Upload, null)
                Spacer(Modifier.width(8.dp))
                Text(if (formState.imageUrl?.isBlank() == true) "Upload Image" else "Change Image")
            }
        }
    }
}

@Composable
fun SupplyItemCard(
    supply: RecipeSupplyItem,
    onQuantityChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var quantityText by remember { mutableStateOf(supply.quantity.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = supply.supplyName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = {
                            quantityText = it
                            it.toDoubleOrNull()?.let { qty -> onQuantityChange(qty) }
                        },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = supply.unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null) },
            title = { Text("Remove Supply") },
            text = { Text("Are you sure you want to remove ${supply.supplyName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplySelectionDialog(
    customSupplies: List<CustomSupply>,
    selectedSupplies: List<Int>,
    onDismiss: () -> Unit,
    onSupplySelected: (CustomSupply, Double) -> Unit
) {
    var selectedSupply by remember { mutableStateOf<CustomSupply?>(null) }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Supply") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSupply?.supply?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Supply") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        customSupplies
                            .filter { it.id.toInt() !in selectedSupplies }
                            .forEach { supply ->
                                DropdownMenuItem(
                                    text = { Text("${supply.supply.name} (${supply.unit})") },
                                    onClick = {
                                        selectedSupply = supply
                                        expanded = false
                                    }
                                )
                            }

                        if (customSupplies.none { it.id.toInt() !in selectedSupplies }) {
                            DropdownMenuItem(
                                text = { Text("No supplies available") },
                                onClick = {},
                                enabled = false
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = { Text(selectedSupply?.unit?.name ?: "") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedSupply?.let { supply ->
                        quantity.toDoubleOrNull()?.let { qty ->
                            if (qty > 0) {
                                onSupplySelected(supply, qty)
                            }
                        }
                    }
                },
                enabled = selectedSupply != null && quantity.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}