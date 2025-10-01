package com.uitopic.restockmobile.features.profiles.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditBusinessDataScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.editBusinessDataState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadAllCategories()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar("Business data updated successfully")
            viewModel.resetBusinessDataSuccess()
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Data") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.businessName,
                onValueChange = viewModel::onBusinessNameChange,
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.businessNameError != null,
                supportingText = state.businessNameError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.businessAddress,
                onValueChange = viewModel::onBusinessAddressChange,
                label = { Text("Company Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.businessAddressError != null,
                supportingText = state.businessAddressError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Company category",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (state.isLoadingCategories) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Selected Categories
                    if (state.selectedCategories.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.selectedCategories.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = true,
                                    onClick = { viewModel.toggleCategory(category) }
                                )
                            }
                        }
                    }

                    // Available Categories
                    val availableCategories = state.availableCategories.filter {
                        it !in state.selectedCategories
                    }

                    if (availableCategories.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCategories.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = false,
                                    onClick = { viewModel.toggleCategory(category) }
                                )
                            }
                        }
                    }
                }

                if (state.categoriesError != null) {
                    Text(
                        text = state.categoriesError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.updateBusinessData() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("SAVE CHANGES")
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: BusinessCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        InputChip(
            selected = true,
            onClick = onClick,
            label = { Text(category.name) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    } else {
        SuggestionChip(
            onClick = onClick,
            label = { Text(category.name) }
        )
    }
}