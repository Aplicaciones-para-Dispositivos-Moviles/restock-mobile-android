package com.uitopic.restockmobile.features.planning.data.remote.datasources

import com.uitopic.restockmobile.features.planning.data.remote.models.AddSupplyToRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.CreateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeSupplyDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeSupplyDto
import com.uitopic.restockmobile.features.planning.data.remote.services.RecipeApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRemoteDataSource @Inject constructor(
    private val apiService: RecipeApiService
) {

    suspend fun getAllRecipesByUserId(userId: Int): Result<List<RecipeDto>> = try {
        val response = apiService.getAllRecipes()
        if (response.isSuccessful && response.body() != null) {
            val allRecipes = response.body()!!
            val userRecipes = allRecipes.filter { it.userId == userId }
            Result.success(userRecipes)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun getAllRecipes(): Result<List<RecipeDto>> = try {
        val response = apiService.getAllRecipes()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getRecipeById(id: Int): Result<RecipeDto> = try {
        val response = apiService.getRecipeById(id)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createRecipe(request: CreateRecipeDto): Result<RecipeDto> = try {
        val response = apiService.createRecipe(request)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRecipe(id: Int, request: UpdateRecipeDto): Result<RecipeDto> = try {
        val response = apiService.updateRecipe(id, request)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteRecipe(id: Int): Result<Unit> = try {
        val response = apiService.deleteRecipe(id)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getRecipeSupplies(recipeId: Int): Result<List<RecipeSupplyDto>> = try {
        val response = apiService.getRecipeSupplies(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addSuppliesToRecipe(
        recipeId: Int,
        supplies: List<AddSupplyToRecipeDto>
    ): Result<Unit> = try {
        val response = apiService.addSuppliesToRecipe(recipeId, supplies)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRecipeSupply(
        recipeId: Int,
        supplyId: Int,
        request: UpdateRecipeSupplyDto
    ): Result<Unit> = try {
        val response = apiService.updateRecipeSupply(recipeId, supplyId, request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun removeSupplyFromRecipe(recipeId: Int, supplyId: Int): Result<Unit> = try {
        val response = apiService.removeSupplyFromRecipe(recipeId, supplyId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
