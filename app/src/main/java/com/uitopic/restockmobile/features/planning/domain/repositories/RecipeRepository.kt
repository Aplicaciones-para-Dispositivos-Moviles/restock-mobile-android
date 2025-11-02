package com.uitopic.restockmobile.features.planning.domain.repositories

import com.uitopic.restockmobile.features.planning.domain.models.AddSupplyToRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.CreateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.Recipe
import com.uitopic.restockmobile.features.planning.domain.models.RecipeSupply
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeSupplyRequest

interface RecipeRepository {
    suspend fun getAllRecipes(): Result<List<Recipe>>
    suspend fun getRecipeById(id: Int): Result<Recipe>
    suspend fun createRecipe(request: CreateRecipeRequest): Result<Recipe>
    suspend fun updateRecipe(id: Int, request: UpdateRecipeRequest): Result<Recipe>
    suspend fun deleteRecipe(id: Int): Result<Unit>

    //Recipe Supplies Management
    suspend fun getRecipeSupplies(recipeId:  Int): Result<List<RecipeSupply>>
    suspend fun addSuppliesToRecipe(recipeId: Int, supplies: List<AddSupplyToRecipeRequest>): Result<Unit>
    suspend fun updateRecipeSupply(recipeId: Int, supplyId: Int, request: UpdateRecipeSupplyRequest): Result<Unit>
    suspend fun removeSupplyFromRecipe(recipeId: Int, supplyId: Int): Result<Unit>
}