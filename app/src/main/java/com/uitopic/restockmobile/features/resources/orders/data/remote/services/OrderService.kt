package com.uitopic.restockmobile.features.resources.orders.data.remote.services


import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderDto
import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderRequestDto
import retrofit2.Response
import retrofit2.http.*

interface OrdersService {

    @GET("orders")
    suspend fun getAllOrders(): Response<List<OrderDto>>

    @GET("orders/admin-restaurant/{adminRestaurantId}")
    suspend fun getOrdersByAdminRestaurantId(
        @Path("adminRestaurantId") adminRestaurantId: Int
    ): Response<List<OrderDto>>

    @GET("orders/supplier/{supplierId}")
    suspend fun getOrdersBySupplierId(
        @Path("supplierId") supplierId: Int
    ): Response<List<OrderDto>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") orderId: Int
    ): Response<OrderDto>

    @POST("orders")
    suspend fun createOrder(
        @Body order: OrderRequestDto
    ): Response<OrderDto>

    @PUT("orders/{id}")
    suspend fun updateOrder(
        @Path("id") orderId: Int,
        @Body order: OrderRequestDto
    ): Response<OrderDto>

    @DELETE("orders/{id}")
    suspend fun deleteOrder(
        @Path("id") orderId: Int
    ): Response<Unit>
}