package com.uitopic.restockmobile.features.planning.presentation.states

import com.uitopic.restockmobile.features.planning.domain.models.Recipe

sealed class RecipeUiState {
    data object Loading : RecipeUiState()
    data class Success(val recipes: List<Recipe>) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

data class RecipeFormState(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String? = "",
    val supplies: List<RecipeSupplyItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStep: Int = 1
)

data class RecipeSupplyItem(
    val supplyId: Int,
    val supplyName: String,
    val quantity: Double,
    val unit: String
)

sealed class RecipeFormEvent {
    data class NameChanged(val name: String) : RecipeFormEvent()
    data class DescriptionChanged(val description: String) : RecipeFormEvent()
    data class PriceChanged(val price: String) : RecipeFormEvent()
    data class ImageUrlChanged(val imageUrl: String) : RecipeFormEvent()
    data class AddSupply(val supply: RecipeSupplyItem) : RecipeFormEvent()
    data class RemoveSupply(val supplyId: Int) : RecipeFormEvent()
    data class UpdateSupplyQuantity(val supplyId: Int, val quantity: Double) : RecipeFormEvent()
    data object NextStep : RecipeFormEvent()
    data object PreviousStep : RecipeFormEvent()
    data object Submit : RecipeFormEvent()
    data object Cancel : RecipeFormEvent()
}