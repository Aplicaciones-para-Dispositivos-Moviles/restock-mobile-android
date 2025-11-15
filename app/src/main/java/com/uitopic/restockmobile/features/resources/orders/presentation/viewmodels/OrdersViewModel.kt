package com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch
import com.uitopic.restockmobile.features.resources.inventory.domain.repositories.InventoryRepository
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
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    private val inventoryRepository: InventoryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // ===== ESTADO PARA LA LISTA DE ÓRDENES =====
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()

    // Cache de suppliers para evitar múltiples llamadas
    private val _suppliersCache = MutableStateFlow<Map<Int, User>>(emptyMap())

    // ===== ESTADO PARA EL FLUJO DE CREACIÓN DE ÓRDENES =====

    private val _orderBatchItems = MutableStateFlow<List<OrderBatchItem>>(emptyList())
    val orderBatchItems: StateFlow<List<OrderBatchItem>> = _orderBatchItems.asStateFlow()

    //Estado para batches filtrados por supply
    private val _availableBatches = MutableStateFlow<List<Batch>>(emptyList())
    val availableBatches: StateFlow<List<Batch>> = _availableBatches.asStateFlow()

    private val _isLoadingBatches = MutableStateFlow(false)
    val isLoadingBatches: StateFlow<Boolean> = _isLoadingBatches.asStateFlow()

    // CÁLCULO EXACTO DE TOTALES CON BigDecimal
    val totalAmount: StateFlow<Double> = _orderBatchItems
        .map { items ->
            items.fold(BigDecimal.ZERO) { acc, item ->
                val price = BigDecimal(item.batch?.customSupply?.price ?: 0.0)
                val quantity = BigDecimal(item.quantity)
                acc + (price * quantity)
            }.setScale(2, RoundingMode.HALF_UP).toDouble()
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
                order.supplier.username.contains(_searchQuery.value, ignoreCase = true) ||
                        order.supplier.profile?.businessName?.contains(_searchQuery.value, ignoreCase = true) == true
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
                val loadedOrders = repository.getAllOrders()
                _orders.value = enrichOrdersWithSupplierInfo(loadedOrders)
                applyFilters()
            } catch (t: Throwable) {
                Log.e("OrdersViewModel", "Error loading orders: ${t.message}", t)
            }
        }
    }

    fun loadOrdersByAdminRestaurantId(adminRestaurantId: Int) {
        viewModelScope.launch {
            try {
                val loadedOrders = repository.getOrdersByAdminRestaurantId(adminRestaurantId)
                _orders.value = enrichOrdersWithSupplierInfo(loadedOrders)
                applyFilters()
            } catch (t: Throwable) {
                Log.e("OrdersViewModel", "Error loading orders by admin: ${t.message}", t)
            }
        }
    }

    fun loadOrdersBySupplierId(supplierId: Int) {
        viewModelScope.launch {
            try {
                val loadedOrders = repository.getOrdersBySupplierId(supplierId)
                _orders.value = enrichOrdersWithSupplierInfo(loadedOrders)
                applyFilters()
            } catch (t: Throwable) {
                Log.e("OrdersViewModel", "Error loading orders by supplier: ${t.message}", t)
            }
        }
    }

    /**
     * ✅ Enriquece las órdenes con información del supplier obtenida de los batches disponibles
     */
    private suspend fun enrichOrdersWithSupplierInfo(orders: List<Order>): List<Order> {
        // Cargar todos los batches una sola vez
        val allBatches = try {
            inventoryRepository.getBatches()
        } catch (e: Exception) {
            Log.e("OrdersViewModel", "Error loading batches: ${e.message}")
            emptyList()
        }

        // Crear un mapa de userId a batch para búsqueda rápida
        val batchesByUserId = allBatches.groupBy { it.userId }

        return orders.map { order ->
            // Si la orden ya tiene supplier con profile completo, no hacer nada
            if (order.supplier.profile != null &&
                order.supplier.profile.businessName.isNotBlank()) {
                return@map order
            }

            // Buscar un batch del supplier en nuestros batches cargados
            val supplierBatches = batchesByUserId[order.supplierId]
            val sampleBatch = supplierBatches?.firstOrNull()

            if (sampleBatch != null) {
                // Crear un User mejorado con la info disponible
                order.copy(
                    supplier = order.supplier.copy(
                        username = order.supplier.username.takeIf { it.isNotBlank() }
                            ?: "Supplier ${order.supplierId}"
                    )
                )
            } else {
                // Si no encontramos batches, dejar como está
                order
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

    // CÁLCULO EXACTO con BigDecimal
    fun calculateTotal(): Double {
        return _orderBatchItems.value.fold(BigDecimal.ZERO) { acc, item ->
            val price = BigDecimal(item.batch?.customSupply?.price ?: 0.0)
            val quantity = BigDecimal(item.quantity)
            acc + (price * quantity)
        }.setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    // Función auxiliar para obtener el ID del usuario actual
    private fun getCurrentUserId(): Int {
        return tokenManager.getUserId()
    }

    private fun getCurrentUserRoleId(): Int {
        return tokenManager.getRoleId()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitOrder(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (_orderBatchItems.value.isEmpty()) {
                    onError("No items in order")
                    return@launch
                }

                val currentUserId = getCurrentUserId()
                if (currentUserId == -1) {
                    onError("User not logged in")
                    return@launch
                }

                val supplierId = _orderBatchItems.value.firstOrNull()?.batch?.userId

                if (supplierId == null) {
                    onError("Supplier not found")
                    return@launch
                }

                val order = Order(
                    id = 0,
                    adminRestaurantId = currentUserId,
                    supplierId = supplierId,
                    supplier = User(
                        id = supplierId,
                        username = "temp",
                        roleId = 2,
                        profile = Profile(
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
                        ),
                        subscription = 0
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
                    // Recargar la orden completa desde el servidor para obtener datos actualizados
                    val completeOrder = repository.getOrderById(createdOrder.id)

                    if (completeOrder != null) {
                        _orders.value = listOf(completeOrder) + _orders.value
                    } else {
                        _orders.value = listOf(createdOrder) + _orders.value
                    }

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

    // ===== FUNCIONES PARA FILTRAR BATCHES =====
 
    fun loadBatchesForSupply(supplyId: Int) {
        viewModelScope.launch {
            _isLoadingBatches.value = true
            try {
                // Obtener todos los batches
                val allBatches = inventoryRepository.getBatches()

                // Filtrar batches que cumplan las condiciones
                val filtered = allBatches.filter { batch ->
                    // Condición 1: El dueño debe ser supplier (roleId = 1)
                    val isSupplier = batch.userRoleId == 1

                    // Condición 2: El customSupply.supplyId debe coincidir EXACTAMENTE
                    val matchesSupply = batch.customSupply?.supplyId == supplyId

                    // Condición 3: Debe tener stock disponible
                    val hasStock = batch.stock > 0

                    // Condición 4: No debe pertenecer al usuario actual (no puede ordenar a sí mismo)
                    val isNotCurrentUser = batch.userId != getCurrentUserId()

                    // Todas las condiciones deben cumplirse
                    val meetsAllConditions = isSupplier && matchesSupply && hasStock && isNotCurrentUser

                    // Log para debugging
                    if (matchesSupply) {
                        Log.d("OrdersViewModel", "Batch ${batch.id}: supplier=$isSupplier, supplyMatch=$matchesSupply (${batch.customSupply?.supplyId} == $supplyId), hasStock=$hasStock, notCurrentUser=$isNotCurrentUser")
                    }

                    meetsAllConditions
                }

                Log.d("OrdersViewModel", "Filtered ${filtered.size} batches for supplyId $supplyId from ${allBatches.size} total batches")
                _availableBatches.value = filtered
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error loading batches: ${e.message}")
                _availableBatches.value = emptyList()
            } finally {
                _isLoadingBatches.value = false
            }
        }
    }

    /**
     * Agrupa los batches disponibles por supplier
     */
    fun getBatchesGroupedBySupplier(): Map<Int, List<Batch>> {
        return _availableBatches.value.groupBy { it.userId ?: 0 }
    }
 
    fun getSupplierBusinessName(batch: Batch): String {
        // Aquí deberías tener una forma de obtener el User completo
        // Por ahora, retornamos un placeholder basado en el userId
        // TODO: Implementar carga de User/Profile completo
        return batch.customSupply?.supply?.name?.let { supplyName ->
            "Supplier #${batch.userId}"
        } ?: "Unknown Supplier"
    }

    fun getCurrentUsername(): String? {
        return tokenManager.getUsername()
    }
}