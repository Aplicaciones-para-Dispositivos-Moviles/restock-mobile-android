package com.uitopic.restockmobile.features.planning.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.planning.domain.models.*
import com.uitopic.restockmobile.features.planning.domain.repositories.RecipeRepository
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeDetailUiState
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeFormEvent
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeFormState
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeSupplyItem
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortByPriceDesc = MutableStateFlow(false)
    val sortByPriceDesc: StateFlow<Boolean> = _sortByPriceDesc.asStateFlow()

    private val _formState = MutableStateFlow(RecipeFormState())
    val formState: StateFlow<RecipeFormState> = _formState.asStateFlow()

    private var currentRecipeId: Int? = null

    // Reactive UI state that automatically updates when recipes change
    val uiState: StateFlow<RecipeUiState> = repository.observeAllRecipes()
        .combine(_searchQuery) { recipes, query -> recipes to query }
        .combine(_sortByPriceDesc) { (recipes, query), sortDesc ->
            Triple(recipes, query, sortDesc)
        }
        .map { (recipes, query, sortDesc) ->
            var filtered = recipes

            // Apply search filter
            if (query.isNotBlank()) {
                filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
            }

            // Apply sort
            if (sortDesc) {
                filtered = filtered.sortedByDescending { it.price }
            }

            RecipeUiState.Success(filtered) as RecipeUiState
        }
        .catch { e ->
            emit(RecipeUiState.Error(e.message ?: "Unknown error") as RecipeUiState)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeUiState.Loading
        )

    private val _currentRecipeIdFlow = MutableStateFlow<Int?>(null)

    // Reactive detail state that automatically updates when the recipe changes
    val detailState: StateFlow<RecipeDetailUiState> = _currentRecipeIdFlow
        .flatMapLatest { recipeId ->
            if (recipeId == null) {
                flowOf(RecipeDetailUiState.Loading as RecipeDetailUiState)
            } else {
                repository.observeRecipeById(recipeId)
                    .map { recipe ->
                        if (recipe != null) {
                            RecipeDetailUiState.Success(recipe) as RecipeDetailUiState
                        } else {
                            RecipeDetailUiState.Loading as RecipeDetailUiState
                        }
                    }
                    .catch { e ->
                        emit(RecipeDetailUiState.Error(e.message ?: "Unknown error") as RecipeDetailUiState)
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeDetailUiState.Loading
        )

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            repository.refreshRecipes()
        }
    }

    fun loadRecipeById(id: Int) {
        viewModelScope.launch {
            // First, trigger loading from backend to ensure we have latest data
            repository.getRecipeById(id)
            // Then set the ID to trigger reactive flow
            _currentRecipeIdFlow.value = id
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortByPrice() {
        _sortByPriceDesc.value = !_sortByPriceDesc.value
    }

    fun onFormEvent(event: RecipeFormEvent) {
        when (event) {
            is RecipeFormEvent.NameChanged -> {
                _formState.value = _formState.value.copy(name = event.name)
            }
            is RecipeFormEvent.DescriptionChanged -> {
                _formState.value = _formState.value.copy(description = event.description)
            }
            is RecipeFormEvent.PriceChanged -> {
                _formState.value = _formState.value.copy(price = event.price)
            }
            is RecipeFormEvent.ImageUrlChanged -> {
                _formState.value = _formState.value.copy(imageUrl = event.imageUrl)
            }
            is RecipeFormEvent.AddSupply -> {
                val currentSupplies = _formState.value.supplies
                if (!currentSupplies.any { it.supplyId == event.supply.supplyId }) {
                    _formState.value = _formState.value.copy(
                        supplies = currentSupplies + event.supply
                    )
                }
            }
            is RecipeFormEvent.RemoveSupply -> {
                val currentSupplies = _formState.value.supplies
                _formState.value = _formState.value.copy(
                    supplies = currentSupplies.filter { it.supplyId != event.supplyId }
                )

                // If editing, remove from backend
                currentRecipeId?.let { recipeId ->
                    viewModelScope.launch {
                        repository.removeSupplyFromRecipe(recipeId, event.supplyId)
                    }
                }
            }
            is RecipeFormEvent.UpdateSupplyQuantity -> {
                val currentSupplies = _formState.value.supplies.map { supply ->
                    if (supply.supplyId == event.supplyId) {
                        supply.copy(quantity = event.quantity)
                    } else supply
                }
                _formState.value = _formState.value.copy(supplies = currentSupplies)
            }
            RecipeFormEvent.NextStep -> {
                if (validateCurrentStep()) {
                    _formState.value = _formState.value.copy(
                        currentStep = _formState.value.currentStep + 1
                    )
                }
            }
            RecipeFormEvent.PreviousStep -> {
                _formState.value = _formState.value.copy(
                    currentStep = _formState.value.currentStep - 1
                )
            }
            RecipeFormEvent.Submit -> {
                submitRecipe()
            }
            RecipeFormEvent.Cancel -> {
                resetForm()
            }
        }
    }

    private fun validateCurrentStep(): Boolean {
        val state = _formState.value
        return when (state.currentStep) {
            1 -> {
                val isValid = state.name.isNotBlank() &&
                        state.description.isNotBlank() &&
                        state.price.toDoubleOrNull() != null &&
                        state.supplies.isNotEmpty()

                if (!isValid) {
                    _formState.value = state.copy(
                        error = "Please fill all required fields and add at least one supply"
                    )
                }
                isValid
            }
            else -> true
        }
    }

    private fun submitRecipe() {
        viewModelScope.launch {
            val state = _formState.value
            _formState.value = state.copy(isLoading = true, error = null)

            val userId = 1 // TODO: Get from auth

            if (currentRecipeId == null) {
                // Create new recipe
                val request = CreateRecipeRequest(
                    name = state.name,
                    description = state.description,
                    imageUrl = state.imageUrl,
                    price = state.price.toDouble(),
                    userId = userId
                )

                repository.createRecipe(request)
                    .onSuccess { recipe ->
                        // Add supplies to the recipe
                        val supplies = state.supplies.map {
                            AddSupplyToRecipeRequest(it.supplyId, it.quantity)
                        }
                        repository.addSuppliesToRecipe(recipe.id, supplies)
                            .onSuccess {
                                _formState.value = state.copy(isLoading = false)
                                resetForm()
                                // No need to call loadRecipes() - cache updates automatically
                            }
                            .onFailure { error ->
                                _formState.value = state.copy(
                                    isLoading = false,
                                    error = error.message
                                )
                            }
                    }
                    .onFailure { error ->
                        _formState.value = state.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
            } else {
                // Update existing recipe
                val request = UpdateRecipeRequest(
                    name = state.name,
                    description = state.description,
                    imageUrl = state.imageUrl,
                    price = state.price.toDouble()
                )

                repository.updateRecipe(currentRecipeId!!, request)
                    .onSuccess {
                        _formState.value = state.copy(isLoading = false)
                        resetForm()
                        // No need to call loadRecipes() - cache updates automatically
                    }
                    .onFailure { error ->
                        _formState.value = state.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
            }
        }
    }

    fun loadRecipeForEdit(recipeId: Int) {
        currentRecipeId = recipeId
        viewModelScope.launch {
            repository.getRecipeById(recipeId)
                .onSuccess { recipe ->
                    _formState.value = RecipeFormState(
                        name = recipe.name,
                        description = recipe.description,
                        price = recipe.price.toString(),
                        imageUrl = recipe.imageUrl,
                        supplies = recipe.supplies.map {
                            RecipeSupplyItem(
                                supplyId = it.supplyId,
                                supplyName = it.supplyName ?: "",
                                quantity = it.quantity,
                                unit = it.unitName ?: ""
                            )
                        }
                    )
                }
        }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.deleteRecipe(recipeId)
                .onSuccess {
                    // No need to call loadRecipes() - cache updates automatically
                }
                .onFailure { error ->
                    // Could emit error to a separate error flow if needed
                }
        }
    }

    private fun resetForm() {
        _formState.value = RecipeFormState()
        currentRecipeId = null
    }
}