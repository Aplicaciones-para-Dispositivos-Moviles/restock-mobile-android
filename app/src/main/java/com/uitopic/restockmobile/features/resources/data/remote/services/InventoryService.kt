package com.uitopic.restockmobile.features.resources.data.remote.services

import CustomSupplyDto
import CustomSupplyRequestDto
import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.models.SupplyDto
import retrofit2.Response
import retrofit2.http.*

interface InventoryService {
    @GET("supplies")
    suspend fun getSupplies(): Response<List<SupplyDto>>

    @GET("custom-supplies")
    suspend fun getCustomSupplies(): Response<List<CustomSupplyDto>>

    @GET("batches")
    suspend fun getBatches(): Response<List<BatchDto>>

    @POST("batches")
    suspend fun createBatch(@Body batch: BatchDto): Response<BatchDto>

    @PUT("batches/{id}")
    suspend fun updateBatch(@Path("id") id: String, @Body batch: BatchDto): Response<BatchDto>

    @DELETE("batches/{id}")
    suspend fun deleteBatch(@Path("id") id: String): Response<Unit>

    @DELETE("custom-supplies/{id}")
    suspend fun deleteCustomSupply(@Path("id") id: Int): Response<Unit>

    @POST("custom-supplies")
    suspend fun createCustomSupply(@Body customSupply: CustomSupplyRequestDto): Response<CustomSupplyDto>

    @PUT("custom-supplies/{id}")
    suspend fun updateCustomSupply(
        @Path("id") id: Int,
        @Body customSupply: CustomSupplyRequestDto
    ): Response<CustomSupplyDto>
}
