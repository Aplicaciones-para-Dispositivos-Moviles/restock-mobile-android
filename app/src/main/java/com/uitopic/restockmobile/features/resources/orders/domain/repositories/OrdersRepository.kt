package com.uitopic.restockmobile.features.resources.orders.domain.repositories


import com.uitopic.restockmobile.features.resources.orders.domain.models.Order

interface OrdersRepository {
    suspend fun getAllOrders(): List<Order>
    suspend fun getOrdersByAdminRestaurantId(adminRestaurantId: Int): List<Order>
    suspend fun getOrdersBySupplierId(supplierId: Int): List<Order>
    suspend fun getOrderById(orderId: Int): Order?
    suspend fun createOrder(order: Order): Order?
    suspend fun updateOrder(order: Order): Order?
    suspend fun deleteOrder(orderId: Int)
}