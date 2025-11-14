package com.uitopic.restockmobile.features.resources.orders.data.repositories


import android.util.Log
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.resources.orders.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.resources.orders.data.remote.mappers.toRequestDto
import com.uitopic.restockmobile.features.resources.orders.data.remote.services.OrdersService
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.repositories.OrdersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val service: OrdersService,
    private val tokenManager: TokenManager
) : OrdersRepository {

    override suspend fun getAllOrders(): List<Order> = withContext(Dispatchers.IO) {
        try {
            val response = service.getAllOrders()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e("OrdersRepository", "Error getting all orders: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exception getting all orders: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getOrdersByAdminRestaurantId(adminRestaurantId: Int): List<Order> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getOrdersByAdminRestaurantId(adminRestaurantId)
                if (response.isSuccessful) {
                    response.body()?.map { it.toDomain() } ?: emptyList()
                } else {
                    Log.e("OrdersRepository", "Error getting orders by admin: ${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("OrdersRepository", "Exception getting orders by admin: ${e.message}", e)
                emptyList()
            }
        }

    override suspend fun getOrdersBySupplierId(supplierId: Int): List<Order> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getOrdersBySupplierId(supplierId)
                if (response.isSuccessful) {
                    response.body()?.map { it.toDomain() } ?: emptyList()
                } else {
                    Log.e("OrdersRepository", "Error getting orders by supplier: ${response.errorBody()?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("OrdersRepository", "Exception getting orders by supplier: ${e.message}", e)
                emptyList()
            }
        }

    override suspend fun getOrderById(orderId: Int): Order? = withContext(Dispatchers.IO) {
        try {
            val response = service.getOrderById(orderId)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("OrdersRepository", "Error getting order by id: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exception getting order by id: ${e.message}", e)
            null
        }
    }

    override suspend fun createOrder(order: Order): Order? = withContext(Dispatchers.IO) {
        try {
            val dto = order.toRequestDto()
            Log.d("OrdersRepository", "Creating order with DTO: $dto")

            val response = service.createOrder(dto)
            Log.d("OrdersRepository", "Response code: ${response.code()} - success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("OrdersRepository", "Order created successfully: $body")
                body?.toDomain()
            } else {
                Log.e("OrdersRepository", "Error creating order: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exception creating order: ${e.message}", e)
            null
        }
    }

    override suspend fun updateOrder(order: Order): Order? = withContext(Dispatchers.IO) {
        try {
            val dto = order.toRequestDto()
            val response = service.updateOrder(order.id, dto)

            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("OrdersRepository", "Error updating order: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exception updating order: ${e.message}", e)
            null
        }
    }

    override suspend fun deleteOrder(orderId: Int): Unit = withContext(Dispatchers.IO) {
        try {
            val response = service.deleteOrder(orderId)
            if (!response.isSuccessful) {
                Log.e("OrdersRepository", "Error deleting order: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exception deleting order: ${e.message}", e)
        }
    }
}