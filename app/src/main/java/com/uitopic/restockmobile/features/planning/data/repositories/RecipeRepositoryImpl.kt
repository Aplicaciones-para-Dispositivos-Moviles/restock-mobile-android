package com.uitopic.restockmobile.features.planning.data.repositories

import com.uitopic.restockmobile.features.planning.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.planning.data.remote.mappers.toDto
import com.uitopic.restockmobile.features.planning.data.remote.services.RecipeApiService
import com.uitopic.restockmobile.features.planning.domain.models.AddSupplyToRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.CreateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.Recipe
import com.uitopic.restockmobile.features.planning.domain.models.RecipeSupply
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeSupplyRequest
import com.uitopic.restockmobile.features.planning.domain.repositories.RecipeRepository
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val apiService: RecipeApiService
) : RecipeRepository {
    override suspend fun getAllRecipes(): Result<List<Recipe>> {
        return try {
            val response = apiService.getAllRecipes()
            if (response.isSuccessful) {
                val recipes = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.success(recipes)
            } else {
                Result.failure(Exception("Error fetching recipes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeById(id: Int): Result<Recipe> {
        return try {
            val response = apiService.getRecipeById(id)
            if (response.isSuccessful) {
                val recipe = response.body()?.toDomain()
                if (recipe != null) {
                    Result.success(recipe)
                } else {
                    Result.failure(Exception("Recipe not found"))
                }
            } else {
                Result.failure(Exception("Error loading recipe: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRecipe(request: CreateRecipeRequest): Result<Recipe> {
        return try {
            val response = apiService.createRecipe(request.toDto())
            if (response.isSuccessful) {
                val recipe = response.body()?.toDomain()
                if (recipe != null) {
                    Result.success(recipe)
                } else {
                    Result.failure(Exception("Error creating recipe"))
                }
            } else {
                Result.failure(Exception("Error creating recipe: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecipe(id: Int, request: UpdateRecipeRequest): Result<Recipe> {
        return try {
            val response = apiService.updateRecipe(id, request.toDto())
            if (response.isSuccessful) {
                val recipe = response.body()?.toDomain()
                if (recipe != null) {
                    Result.success(recipe)
                } else {
                    Result.failure(Exception("Error updating recipe"))
                }
            } else {
                Result.failure(Exception("Error updating recipe: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRecipe(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteRecipe(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting recipe: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeSupplies(recipeId: Int): Result<List<RecipeSupply>> {
        return try {
            val response = apiService.getRecipeSupplies(recipeId)
            if (response.isSuccessful) {
                val supplies = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.success(supplies)
            } else {
                Result.failure(Exception("Error loading supplies: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSuppliesToRecipe(recipeId: Int, supplies: List<AddSupplyToRecipeRequest>): Result<Unit> {
        return try {
            val response = apiService.addSuppliesToRecipe(
                recipeId,
                supplies.map { it.toDto() }
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error adding supplies: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecipeSupply(
        recipeId: Int,
        supplyId: Int,
        request: UpdateRecipeSupplyRequest
    ): Result<Unit> {
        return try {
            val response = apiService.updateRecipeSupply(recipeId, supplyId, request.toDto())
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error updating supply: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeSupplyFromRecipe(recipeId: Int, supplyId: Int): Result<Unit> {
        return try {
            val response = apiService.removeSupplyFromRecipe(recipeId, supplyId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error removing supply: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}