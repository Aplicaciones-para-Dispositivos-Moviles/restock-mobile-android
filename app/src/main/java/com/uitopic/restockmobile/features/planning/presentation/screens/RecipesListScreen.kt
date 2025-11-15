package com.uitopic.restockmobile.features.planning.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.planning.presentation.components.common.EmptyRecipesState
import com.uitopic.restockmobile.features.planning.presentation.components.common.ErrorState
import com.uitopic.restockmobile.features.planning.presentation.components.common.LoadingState
import com.uitopic.restockmobile.features.planning.presentation.components.list.RecipeCard
import com.uitopic.restockmobile.features.planning.presentation.components.list.RecipeSearchBar
import com.uitopic.restockmobile.features.planning.presentation.components.list.RecipeSortButton
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeUiState
import com.uitopic.restockmobile.features.planning.presentation.viewmodels.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesListScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    onRecipeClick: (Int) -> Unit,
    onCreateRecipe: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortByPrice by viewModel.sortByPriceDesc.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    // Reload recipes when the screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadRecipes()
    }

    // Handle refresh state
    LaunchedEffect(uiState) {
        if (uiState !is RecipeUiState.Loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    RecipeSortButton(
                        isSortedByPriceDesc = sortByPrice,
                        onToggleSort = { viewModel.toggleSortByPrice() }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateRecipe,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Create Recipe")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RecipeSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.loadRecipes()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is RecipeUiState.Loading -> {
                        if (!isRefreshing) {
                            LoadingState()
                        }
                    }
                    is RecipeUiState.Success -> {
                        val recipes = (uiState as RecipeUiState.Success).recipes

                        if (recipes.isEmpty()) {
                            EmptyRecipesState()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(recipes) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = { onRecipeClick(recipe.id ?: 0) }
                                    )
                                }
                            }
                        }
                    }
                    is RecipeUiState.Error -> {
                        ErrorState(
                            message = (uiState as RecipeUiState.Error).message,
                            onRetry = { viewModel.loadRecipes() }
                        )
                    }
                }
            }
        }
    }
}