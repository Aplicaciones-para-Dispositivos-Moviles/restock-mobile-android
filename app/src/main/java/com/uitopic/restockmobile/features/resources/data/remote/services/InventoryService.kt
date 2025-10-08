package com.uitopic.restockmobile.features.resources.data.remote.services

import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.models.CustomSupplyDto
import com.uitopic.restockmobile.features.resources.data.remote.models.SupplyDto
import retrofit2.Response
import retrofit2.http.*

interface InventoryService {
    @GET("supplies")
    suspend fun getSupplies(): Response<List<SupplyDto>>

    @GET("custom_supplies")
    suspend fun getCustomSupplies(): Response<List<CustomSupplyDto>>

    @GET("batches")
    suspend fun getBatches(): Response<List<BatchDto>>

    @POST("batches")
    suspend fun createBatch(@Body batch: BatchDto): Response<BatchDto>

    @DELETE("batches/{id}")
    suspend fun deleteBatch(@Path("id") id: String): Response<Unit>

    suspend fun deleteCustomSupply(id: String): Response<Unit>
}