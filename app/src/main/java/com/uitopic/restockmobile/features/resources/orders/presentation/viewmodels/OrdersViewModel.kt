package com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState
import com.uitopic.restockmobile.features.resources.orders.domain.repositories.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository
) : ViewModel() {

    // ===== ESTADO PARA LA LISTA DE ÓRDENES =====
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()

    // ===== ESTADO PARA EL FLUJO DE CREACIÓN DE ÓRDENES =====

    private val _orderBatchItems = MutableStateFlow<List<OrderBatchItem>>(emptyList())
    val orderBatchItems: StateFlow<List<OrderBatchItem>> = _orderBatchItems.asStateFlow()

    val totalAmount: StateFlow<Double> = _orderBatchItems
        .map { items ->
            items.sumOf { item ->
                (item.batch?.customSupply?.price ?: 0.0) * item.quantity
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    init {
        loadAllOrders()
    }

    // ===== FUNCIONES PARA LA LISTA DE ÓRDENES =====

    private fun applyFilters() {
        var filtered = _orders.value

        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { order ->
                order.supplier.username.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        _filteredOrders.value = filtered
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            try {
                _orders.value = repository.getAllOrders()
                applyFilters()
            } catch (t: Throwable) {
                // TODO: manejar error
            }
        }
    }

    fun loadOrdersByAdminRestaurantId(adminRestaurantId: Int) {
        viewModelScope.launch {
            try {
                _orders.value = repository.getOrdersByAdminRestaurantId(adminRestaurantId)
                applyFilters()
            } catch (t: Throwable) {
                // TODO: manejar error
            }
        }
    }

    fun loadOrdersBySupplierId(supplierId: Int) {
        viewModelScope.launch {
            try {
                _orders.value = repository.getOrdersBySupplierId(supplierId)
                applyFilters()
            } catch (t: Throwable) {
                // TODO: manejar error
            }
        }
    }

    fun getOrderById(orderId: Int): Order? {
        return _orders.value.find { it.id == orderId }
    }

    fun refreshOrders() {
        loadAllOrders()
    }

    // ===== FUNCIONES PARA EL FLUJO DE CREACIÓN DE ÓRDENES =====

    fun addBatchToOrder(batch: Batch) {
        val newItem = OrderBatchItem(
            batchId = batch.id.toIntOrNull() ?: 0,
            quantity = 1.0,
            accepted = false,
            batch = batch
        )
        _orderBatchItems.value = _orderBatchItems.value + newItem
    }

    fun addMultipleBatchesToOrder(batches: List<Batch>) {
        val newItems = batches.map { batch ->
            OrderBatchItem(
                batchId = batch.id.toIntOrNull() ?: 0,
                quantity = 1.0,
                accepted = false,
                batch = batch
            )
        }
        _orderBatchItems.value = _orderBatchItems.value + newItems
    }

    fun updateItemQuantity(batchId: Int, newQuantity: Double) {
        _orderBatchItems.value = _orderBatchItems.value.map { item ->
            if (item.batchId == batchId) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }

    fun removeItem(batchId: Int) {
        _orderBatchItems.value = _orderBatchItems.value.filter { it.batchId != batchId }
    }

    fun calculateTotal(): Double {
        return _orderBatchItems.value.sumOf { item ->
            (item.batch?.customSupply?.price ?: 0.0) * item.quantity
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitOrder(
        adminRestaurantId: Int,
        supplierId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (_orderBatchItems.value.isEmpty()) {
                    onError("No items in order")
                    return@launch
                }

                val order = Order(
                    id = 0,
                    adminRestaurantId = adminRestaurantId,
                    supplierId = supplierId,
                    supplier = com.uitopic.restockmobile.features.auth.domain.models.User(
                        id = supplierId,
                        username = "temp",
                        roleId = 2,
                        profile = com.uitopic.restockmobile.features.profiles.domain.models.Profile(
                            id = 0,
                            firstName = "",
                            lastName = "",
                            email = "",
                            phone = "",
                            address = "",
                            country = "",
                            avatar = null,
                            businessName = "",
                            businessAddress = "",
                            description = null,
                            categories = emptyList()
                        )
                    ),
                    requestedDate = java.time.LocalDate.now().toString(),
                    partiallyAccepted = false,
                    requestedProductsCount = _orderBatchItems.value.size,
                    totalPrice = calculateTotal(),
                    state = OrderState.ON_HOLD,
                    situation = OrderSituation.PENDING,
                    batchItems = _orderBatchItems.value
                )

                val createdOrder = repository.createOrder(order)

                if (createdOrder != null) {
                    _orders.value = listOf(createdOrder) + _orders.value
                    applyFilters()
                    clearOrderState()
                    onSuccess()
                } else {
                    onError("Failed to create order")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error creating order")
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            try {
                val updatedOrder = repository.updateOrder(order)
                if (updatedOrder != null) {
                    _orders.value = _orders.value.map {
                        if (it.id == updatedOrder.id) updatedOrder else it
                    }
                    applyFilters()
                }
            } catch (t: Throwable) {
                _orders.value = _orders.value.map {
                    if (it.id == order.id) order else it
                }
                applyFilters()
            }
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteOrder(orderId)
                _orders.value = _orders.value.filterNot { it.id == orderId }
                applyFilters()
            } catch (t: Throwable) {
                _orders.value = _orders.value.filterNot { it.id == orderId }
                applyFilters()
            }
        }
    }

    fun clearOrderState() {
        _orderBatchItems.value = emptyList()
    }
}