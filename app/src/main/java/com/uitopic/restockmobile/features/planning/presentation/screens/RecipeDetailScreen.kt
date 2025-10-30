package com.uitopic.restockmobile.features.planning.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto
import com.uitopic.restockmobile.features.planning.presentation.components.common.DeleteConfirmationDialog
import com.uitopic.restockmobile.features.planning.presentation.components.common.ErrorState
import com.uitopic.restockmobile.features.planning.presentation.components.common.LoadingState
import com.uitopic.restockmobile.features.planning.presentation.components.detail.RecipeHeader
import com.uitopic.restockmobile.features.planning.presentation.components.detail.RecipePriceCard
import com.uitopic.restockmobile.features.planning.presentation.components.detail.SuppliesSection
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeDetailUiState
import com.uitopic.restockmobile.features.planning.presentation.viewmodels.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    viewModel: RecipesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditRecipe: (Int) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeById(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditRecipe(recipeId) }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            )
        }
    ) { padding ->
        when (detailState) {
            is RecipeDetailUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            is RecipeDetailUiState.Success -> {
                val successState = detailState as RecipeDetailUiState.Success
                RecipeDetailContent(
                    recipe = successState.recipe,
                    enrichedSupplies = successState.enrichedSupplies,
                    modifier = Modifier.padding(padding)
                )
            }
            is RecipeDetailUiState.Error -> {
                ErrorState(
                    message = (detailState as RecipeDetailUiState.Error).message,
                    onRetry = { viewModel.loadRecipeById(recipeId) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Delete Recipe",
            message = "Are you sure you want to delete this recipe? This action cannot be undone.",
            onConfirm = {
                viewModel.deleteRecipe(recipeId)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: RecipeDto,
    enrichedSupplies: List<com.uitopic.restockmobile.features.planning.presentation.states.RecipeSupplyItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RecipeHeader(
                imageUrl = recipe.imageUrl,
                name = recipe.name ?: "",
                description = recipe.description ?: ""
            )
        }

        item {
            RecipePriceCard(price = recipe.price ?: 0.0)
        }

        item {
            SuppliesSection(supplies = enrichedSupplies)
        }
    }
}
