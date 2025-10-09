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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val detailState: StateFlow<RecipeDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(RecipeFormState())
    val formState: StateFlow<RecipeFormState> = _formState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortByPriceDesc = MutableStateFlow(false)
    val sortByPriceDesc: StateFlow<Boolean> = _sortByPriceDesc.asStateFlow()

    private var allRecipes: List<Recipe> = emptyList()
    private var currentRecipeId: Int? = null

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            repository.getAllRecipes()
                .onSuccess { recipes ->
                    allRecipes = recipes
                    applyFilters()
                }
                .onFailure { error ->
                    _uiState.value = RecipeUiState.Error(
                        error.message ?: "Error loading recipes"
                    )
                }
        }
    }

    fun loadRecipeById(id: Int) {
        viewModelScope.launch {
            _detailState.value = RecipeDetailUiState.Loading
            repository.getRecipeById(id)
                .onSuccess { recipe ->
                    _detailState.value = RecipeDetailUiState.Success(recipe)
                }
                .onFailure { error ->
                    _detailState.value = RecipeDetailUiState.Error(
                        error.message ?: "Error loading recipe"
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun toggleSortByPrice() {
        _sortByPriceDesc.value = !_sortByPriceDesc.value
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allRecipes

        // Apply search filter
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { recipe ->
                recipe.name.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        // Apply sort
        if (_sortByPriceDesc.value) {
            filtered = filtered.sortedByDescending { it.price }
        }

        _uiState.value = RecipeUiState.Success(filtered)
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
                                loadRecipes()
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
                        loadRecipes()
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
                    loadRecipes()
                }
                .onFailure { error ->
                    _uiState.value = RecipeUiState.Error(
                        error.message ?: "Error deleting recipe"
                    )
                }
        }
    }

    private fun resetForm() {
        _formState.value = RecipeFormState()
        currentRecipeId = null
    }
}