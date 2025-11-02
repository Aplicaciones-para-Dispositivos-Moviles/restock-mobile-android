package com.uitopic.restockmobile.features.planning.data.remote.services

import com.uitopic.restockmobile.features.planning.data.remote.models.AddSupplyToRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.CreateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeSupplyDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeSupplyDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecipeApiService {

    @GET("recipes")
    suspend fun getAllRecipes(): Response<List<RecipeDto>>

    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Int): Response<RecipeDto>

    @POST("recipes")
    suspend fun createRecipe(@Body request: CreateRecipeDto): Response<RecipeDto>

    @PUT("recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") id: Int,
        @Body request: UpdateRecipeDto
    ): Response<RecipeDto>

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Response<Unit>

    // Recipe Supplies Management
    @GET("recipes/{recipeId}/supplies")
    suspend fun getRecipeSupplies(@Path("recipeId") recipeId: Int): Response<List<RecipeSupplyDto>>

    @POST("recipes/{recipeId}/supplies")
    suspend fun addSuppliesToRecipe(
        @Path("recipeId") recipeId: Int,
        @Body supplies: List<AddSupplyToRecipeDto>
    ): Response<Unit>

    @PUT("recipes/{recipeId}/supplies/{supplyId}")
    suspend fun updateRecipeSupply(
        @Path("recipeId") recipeId: Int,
        @Path("supplyId") supplyId: Int,
        @Body request: UpdateRecipeSupplyDto
    ): Response<Unit>

    @DELETE("recipes/{recipeId}/supplies/{supplyId}")
    suspend fun removeSupplyFromRecipe(
        @Path("recipeId") recipeId: Int,
        @Path("supplyId") supplyId: Int
    ): Response<Unit>

}