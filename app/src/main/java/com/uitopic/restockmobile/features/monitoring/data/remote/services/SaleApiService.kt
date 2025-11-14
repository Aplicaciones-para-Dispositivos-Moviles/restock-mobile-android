package com.uitopic.restockmobile.features.monitoring.data.remote.services

import com.uitopic.restockmobile.features.monitoring.data.remote.models.CreateSaleDto
import com.uitopic.restockmobile.features.monitoring.data.remote.models.SaleDto
import retrofit2.Response
import retrofit2.http.*

interface SaleApiService {
    @POST("sales")
    suspend fun createSale(@Body createSaleDto: CreateSaleDto): Response<SaleDto>

    @GET("sales")
    suspend fun getAllSales(): Response<List<SaleDto>>

    @GET("sales/{id}")
    suspend fun getSaleById(@Path("id") id: Int): Response<SaleDto>

    @DELETE("sales/{id}")
    suspend fun cancelSale(@Path("id") id: Int): Response<Unit>
}

