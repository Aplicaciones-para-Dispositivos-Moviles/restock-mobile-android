package com.uitopic.restockmobile.features.planning.domain.repositories

import com.uitopic.restockmobile.features.planning.domain.models.AddSupplyToRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.CreateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.Recipe
import com.uitopic.restockmobile.features.planning.domain.models.RecipeSupply
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeSupplyRequest
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    // Reactive Flows - automatically updates UI when data changes
    fun observeAllRecipes(): Flow<List<Recipe>>
    fun observeRecipeById(id: Int): Flow<Recipe?>

    // One-time operations (for backwards compatibility and single operations)
    suspend fun getAllRecipes(): Result<List<Recipe>>
    suspend fun getRecipeById(id: Int): Result<Recipe>
    suspend fun createRecipe(request: CreateRecipeRequest): Result<Recipe>
    suspend fun updateRecipe(id: Int, request: UpdateRecipeRequest): Result<Recipe>
    suspend fun deleteRecipe(id: Int): Result<Unit>

    // Refresh data from backend
    suspend fun refreshRecipes()

    //Recipe Supplies Management
    suspend fun getRecipeSupplies(recipeId:  Int): Result<List<RecipeSupply>>
    suspend fun addSuppliesToRecipe(recipeId: Int, supplies: List<AddSupplyToRecipeRequest>): Result<Unit>
    suspend fun updateRecipeSupply(recipeId: Int, supplyId: Int, request: UpdateRecipeSupplyRequest): Result<Unit>
    suspend fun removeSupplyFromRecipe(recipeId: Int, supplyId: Int): Result<Unit>
}