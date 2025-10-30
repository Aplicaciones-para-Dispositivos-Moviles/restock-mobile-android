package com.uitopic.restockmobile.features.planning.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.core.cloudinary.repositories.ImageUploadRepository
import com.uitopic.restockmobile.features.planning.data.remote.datasources.RecipeRemoteDataSource
import com.uitopic.restockmobile.features.planning.data.remote.models.AddSupplyToRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.CreateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeSupplyDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeSupplyDto
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
    private val remoteDataSource: RecipeRemoteDataSource,
    private val imageUploadRepository: ImageUploadRepository,
    private val tokenManager: TokenManager,
    private val inventoryRepository: com.uitopic.restockmobile.features.resources.domain.repositories.InventoryRepository
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

    private var allRecipes: List<RecipeDto> = emptyList()
    private var currentRecipeId: Int? = null



    init {
        loadRecipes()
    }

    fun uploadRecipeImage(imageUri: Uri) {
        viewModelScope.launch {
            val currentState = _formState.value
            _formState.value = currentState.copy(
                isUploadingImage = true,
                imageUploadError = null
            )

            imageUploadRepository.uploadImage(imageUri)
                .onSuccess { imageUrl ->
                    _formState.value = _formState.value.copy(
                        isUploadingImage = false,
                        imageUrl = imageUrl,
                        imageUploadError = null
                    )
                }
                .onFailure { error ->
                    _formState.value = _formState.value.copy(
                        isUploadingImage = false,
                        imageUploadError = error.message ?: "Failed to upload image"
                    )
                }
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            remoteDataSource.getAllRecipes()
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

            // Fetch custom supplies with enriched data
            val customSupplies = inventoryRepository.getCustomSupplies()
            val customSuppliesMap = customSupplies.associateBy { it.id }

            remoteDataSource.getRecipeById(id)
                .onSuccess { recipe ->
                    val enrichedSupplies = recipe.supplies?.map { recipeSupply ->
                        val customSupply = customSuppliesMap[recipeSupply.supplyId]
                        val supply = customSupply?.supply

                        RecipeSupplyItem(
                            supplyId = recipeSupply.supplyId ?: 0,
                            supplyName = supply?.name ?: "Unknown Supply",
                            quantity = recipeSupply.quantity ?: 0.0,
                            unit = customSupply?.unit?.name ?: ""
                        )
                    } ?: emptyList()

                    _detailState.value = RecipeDetailUiState.Success(recipe, enrichedSupplies)
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
                recipe.name?.contains(_searchQuery.value, ignoreCase = true) == true
            }
        }

        // Apply sort
        if (_sortByPriceDesc.value) {
            filtered = filtered.sortedByDescending { it.price ?: 0.0 }
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
                // Check if supply is not already added
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
                        remoteDataSource.removeSupplyFromRecipe(recipeId, event.supplyId)
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

            val userId = tokenManager.getUserId()

            if (currentRecipeId == null) {
                // Create new recipe
                val request = CreateRecipeDto(
                    name = state.name,
                    description = state.description,
                    imageUrl = state.imageUrl ?: "",
                    price = state.price.toDouble(),
                    userId = userId
                )

                remoteDataSource.createRecipe(request)
                    .onSuccess { recipe ->
                        // Add supplies to the recipe
                        val supplies = state.supplies.map {
                            AddSupplyToRecipeDto(
                                supplyId = it.supplyId.toString(),
                                quantity = it.quantity
                            )
                        }
                        remoteDataSource.addSuppliesToRecipe(recipe.id ?: 0, supplies)
                            .onSuccess {
                                _formState.value = state.copy(isLoading = false, isSuccess = true)
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
                val request = UpdateRecipeDto(
                    name = state.name,
                    description = state.description,
                    imageUrl = state.imageUrl ?: "",
                    price = state.price.toDouble()
                )

                remoteDataSource.updateRecipe(currentRecipeId!!, request)
                    .onSuccess {
                        // Identify new supplies (not in originalSupplies)
                        val newSupplies = state.supplies.filter { supply ->
                            state.originalSupplies.none { it.supplyId == supply.supplyId }
                        }

                        // Identify supplies that need quantity update
                        val updatedSupplies = state.supplies.filter { supply ->
                            val original = state.originalSupplies.find { it.supplyId == supply.supplyId }
                            original != null && original.quantity != supply.quantity
                        }

                        // Add new supplies
                        if (newSupplies.isNotEmpty()) {
                            val newSuppliesDto = newSupplies.map {
                                AddSupplyToRecipeDto(
                                    supplyId = it.supplyId.toString(),
                                    quantity = it.quantity
                                )
                            }
                            remoteDataSource.addSuppliesToRecipe(currentRecipeId!!, newSuppliesDto)
                        }

                        // Update quantities for existing supplies
                        updatedSupplies.forEach { supply ->
                            val updateDto = UpdateRecipeSupplyDto(
                                supplyId = supply.supplyId.toString(),
                                quantity = supply.quantity
                            )
                            remoteDataSource.updateRecipeSupply(
                                currentRecipeId!!,
                                supply.supplyId,
                                updateDto
                            )
                        }

                        _formState.value = state.copy(isLoading = false, isSuccess = true)
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
            // Fetch custom supplies with enriched data
            val customSupplies = inventoryRepository.getCustomSupplies()
            val customSuppliesMap = customSupplies.associateBy { it.id }

            remoteDataSource.getRecipeById(recipeId)
                .onSuccess { recipe ->
                    val supplies = recipe.supplies?.map { recipeSupply ->
                        // recipeSupply.supplyId es el ID del CustomSupply
                        val customSupply = customSuppliesMap[recipeSupply.supplyId]
                        val supply = customSupply?.supply

                        RecipeSupplyItem(
                            supplyId = recipeSupply.supplyId ?: 0,
                            supplyName = supply?.name ?: "Unknown Supply",
                            quantity = recipeSupply.quantity ?: 0.0,
                            unit = customSupply?.unit?.name ?: ""
                        )
                    } ?: emptyList()

                    _formState.value = RecipeFormState(
                        name = recipe.name ?: "",
                        description = recipe.description ?: "",
                        price = recipe.price?.toString() ?: "",
                        imageUrl = recipe.imageUrl,
                        supplies = supplies,
                        originalSupplies = supplies
                    )
                }
        }
    }

    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            remoteDataSource.deleteRecipe(recipeId)
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