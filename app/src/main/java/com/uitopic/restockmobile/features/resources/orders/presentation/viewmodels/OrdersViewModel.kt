package com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.repositories.ProfileRepository
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch
import com.uitopic.restockmobile.features.resources.inventory.domain.repositories.InventoryRepository
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState
import com.uitopic.restockmobile.features.resources.orders.domain.repositories.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val ROLE_SUPPLIER = 1
private const val ROLE_ADMIN_RESTAURANT = 2

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    private val inventoryRepository: InventoryRepository,
    private val profileRepository: ProfileRepository,
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

    // Cache de PROFILES
    private val _suppliersProfileCache = MutableStateFlow<Map<Int, Profile>>(emptyMap())
    val suppliersProfileCache: StateFlow<Map<Int, Profile>> = _suppliersProfileCache.asStateFlow()

    private val _isLoadingSuppliers = MutableStateFlow(false)
    val isLoadingSuppliers: StateFlow<Boolean> = _isLoadingSuppliers.asStateFlow()


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
        loadOrdersForCurrentUser()
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



    fun loadOrdersForCurrentUser() {
        viewModelScope.launch {
            try {
                val currentUserId = getCurrentUserId()
                val currentRoleId = getCurrentUserRoleId()

                Log.d("OrdersViewModel", "Loading orders for userId=$currentUserId, roleId=$currentRoleId")

                val loadedOrders = when (currentRoleId) {
                    ROLE_ADMIN_RESTAURANT -> repository.getOrdersByAdminRestaurantId(currentUserId)
                    ROLE_SUPPLIER -> repository.getOrdersBySupplierId(currentUserId)
                    else -> repository.getAllOrders() // o emptyList()
                }

                Log.d("OrdersViewModel", "Loaded ${loadedOrders.size} orders from backend")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val sortedOrders = loadedOrders.sortedByDescending { order ->
                    val time = try {
                        dateFormat.parse(order.requestedDate)?.time
                    } catch (e: Exception) {
                        Log.w("OrdersViewModel", "Error parsing date: ${order.requestedDate}", e)
                        null
                    }
                    time ?: Long.MIN_VALUE
                }

                _orders.value = enrichOrdersWithSupplierInfo(sortedOrders)
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
     * Enriquece las órdenes con información del supplier obtenida de los batches disponibles
     */
    private suspend fun enrichOrdersWithSupplierInfo(orders: List<Order>): List<Order> {
        // Obtener IDs únicos de suppliers
        val supplierIds = orders.map { it.supplierId }.distinct()

        Log.d("OrdersViewModel", "Enriching orders with ${supplierIds.size} unique suppliers")

        // CARGAR TODOS LOS PROFILES EN PARALELO
        supplierIds.forEach { supplierId ->
            if (!_suppliersProfileCache.value.containsKey(supplierId)) {
                loadSupplierProfile(supplierId)
            }
        }

        // ESPERAR UN POCO para que se carguen los profiles
        kotlinx.coroutines.delay(500)

        return orders.map { order ->
            // Buscar profile en caché
            val cachedProfile = _suppliersProfileCache.value[order.supplierId]

            if (cachedProfile != null) {
                Log.d("OrdersViewModel", "Found profile for supplier ${order.supplierId}: ${cachedProfile.businessName}")

                // Crear un User con el profile cargado
                order.copy(
                    supplier = order.supplier.copy(
                        profile = cachedProfile,
                        username = order.supplier.username.takeIf { it.isNotBlank() }
                            ?: "supplier_${order.supplierId}"
                    )
                )
            } else {
                Log.w("OrdersViewModel", "No profile found for supplier ${order.supplierId}")

                // Si el order ya tiene profile del backend, mantenerlo
                if (order.supplier.profile != null &&
                    order.supplier.profile.businessName.isNotBlank()) {
                    order
                } else {
                    // Crear un supplier temporal
                    order.copy(
                        supplier = order.supplier.copy(
                            username = order.supplier.username.takeIf { it.isNotBlank() }
                                ?: "supplier_${order.supplierId}"
                        )
                    )
                }
            }
        }
    }

    fun getOrderById(orderId: Int): Order? {
        return _orders.value.find { it.id == orderId }
    }

    fun refreshOrders() {
        loadOrdersForCurrentUser()
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
                // 1. Validaciones
                if (_orderBatchItems.value.isEmpty()) {
                    onError("No items in order")
                    return@launch
                }

                val currentUserId = getCurrentUserId()
                if (currentUserId == -1) {
                    onError("User not logged in")
                    return@launch
                }

                // 2. AGRUPAR ITEMS POR SUPPLIER
                val itemsBySupplier = _orderBatchItems.value.groupBy { item ->
                    item.batch?.userId ?: 0
                }

                val supplierIds = itemsBySupplier.keys.filter { it != 0 }
                Log.d("OrdersViewModel", "Loading profiles for ${supplierIds.size} suppliers before creating orders")

                supplierIds.forEach { supplierId ->
                    if (!_suppliersProfileCache.value.containsKey(supplierId)) {
                        loadSupplierProfile(supplierId)
                    }
                }

                delay(500)

                // 3. CREAR UNA ORDEN POR CADA SUPPLIER
                val createdOrders = mutableListOf<Order>()

                itemsBySupplier.forEach { (supplierId, items) ->
                    if (supplierId == 0) {
                        Log.w("OrdersViewModel", "Skipping items with invalid supplierId")
                        return@forEach
                    }

                    // Calcular el total para ESTE supplier específico
                    val supplierTotal = items.fold(BigDecimal.ZERO) { acc, item ->
                        val price = BigDecimal(item.batch?.customSupply?.price ?: 0.0)
                        val quantity = BigDecimal(item.quantity)
                        acc + (price * quantity)
                    }.setScale(2, RoundingMode.HALF_UP).toDouble()

                    val supplierProfile = _suppliersProfileCache.value[supplierId]

                    // Crear orden para este supplier
                    val order = Order(
                        id = 0,
                        adminRestaurantId = currentUserId,
                        supplierId = supplierId,
                        supplier = User(
                            id = supplierId,
                            username = "temp",
                            roleId = 2,
                            profile = supplierProfile,
                            subscription = 0
                        ),
                        requestedDate = java.time.LocalDate.now().toString(),
                        partiallyAccepted = false,
                        requestedProductsCount = items.size,  // Solo los items de este supplier
                        totalPrice = supplierTotal,  // Total calculado para este supplier
                        state = OrderState.ON_HOLD,
                        situation = OrderSituation.PENDING,
                        batchItems = items  // Solo los items de este supplier
                    )

                    // Crear la orden en el backend
                    val createdOrder = repository.createOrder(order)

                    if (createdOrder != null) {
                        createdOrders.add(createdOrder)
                    } else {
                        Log.e("OrdersViewModel", "Failed to create order for supplier $supplierId")
                    }
                }

                // 4. ACTUALIZAR LA LISTA CON TODAS LAS ÓRDENES CREADAS
                if (createdOrders.isNotEmpty()) {
                    // Recargar todas las órdenes desde el servidor para tener datos completos
                    val completeOrders = createdOrders.mapNotNull { order ->
                        val fetchedOrder = repository.getOrderById(order.id)

                        // SI LA ORDEN NO TIENE PROFILE, AGREGARLO DEL CACHÉ
                        if (fetchedOrder != null &&
                            (fetchedOrder.supplier.profile == null ||
                                    fetchedOrder.supplier.profile.businessName.isBlank())) {

                            val profile = _suppliersProfileCache.value[fetchedOrder.supplierId]
                            if (profile != null) {
                                fetchedOrder.copy(
                                    supplier = fetchedOrder.supplier.copy(profile = profile)
                                )
                            } else {
                                fetchedOrder
                            }
                        } else {
                            fetchedOrder
                        }
                    }
                    _orders.value = completeOrders + _orders.value
                    applyFilters()
                    clearOrderState()
                    onSuccess()

                    Log.d("OrdersViewModel", "Successfully created ${createdOrders.size} orders")
                } else {
                    onError("Failed to create any orders")
                }

            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error creating orders: ${e.message}", e)
                onError(e.message ?: "Error creating orders")
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

                // Cargar profiles de suppliers automáticamente
                loadSuppliersProfiles(filtered)

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

    private suspend fun loadSupplierProfile(userId: Int): Profile? {
        // Si ya está en caché, retornar
        _suppliersProfileCache.value[userId]?.let {
            Log.d("OrdersViewModel", "Profile for supplier $userId already in cache")
            return it
        }

        return try {
            Log.d("OrdersViewModel", "Loading profile for supplier $userId...")

            var loadedProfile: Profile? = null

            profileRepository.getProfileById(userId.toString())
                .onSuccess { profile ->
                    // Agregar al caché
                    _suppliersProfileCache.value = _suppliersProfileCache.value + (userId to profile)
                    loadedProfile = profile
                    Log.d("OrdersViewModel", "✓ Loaded profile for supplier $userId: ${profile.businessName}")
                }
                .onFailure { error ->
                    Log.e("OrdersViewModel", "✗ Error loading profile for supplier $userId: ${error.message}")
                }

            loadedProfile
        } catch (e: Exception) {
            Log.e("OrdersViewModel", "✗ Exception loading supplier profile $userId: ${e.message}", e)
            null
        }
    }

    fun loadSuppliersProfiles(batches: List<Batch>) {
        viewModelScope.launch {
            _isLoadingSuppliers.value = true
            try {
                // Obtener IDs únicos de suppliers
                val supplierIds = batches.mapNotNull { it.userId }.distinct()

                // Filtrar los que NO están en caché
                val idsToLoad = supplierIds.filter { id ->
                    !_suppliersProfileCache.value.containsKey(id)
                }

                Log.d("OrdersViewModel", "Loading ${idsToLoad.size} supplier profiles (${supplierIds.size - idsToLoad.size} already cached)")

                // Cargar en paralelo
                idsToLoad.map { userId ->
                    async { loadSupplierProfile(userId) }
                }.awaitAll()

            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error loading suppliers profiles: ${e.message}")
            } finally {
                _isLoadingSuppliers.value = false
            }
        }
    }

    fun getSupplierProfile(userId: Int): Profile? {
        return _suppliersProfileCache.value[userId]
    }

    fun getSupplierBusinessName(userId: Int): String {
        val profile = _suppliersProfileCache.value[userId]
        return profile?.businessName?.takeIf { it.isNotBlank() }
            ?: "Supplier #$userId"
    }

    fun getBatchesGroupedBySupplier(): Map<Int, List<Batch>> {
        return _availableBatches.value.groupBy { it.userId ?: 0 }
    }

    fun getSupplierPhone(userId: Int): String {
        return _suppliersProfileCache.value[userId]?.phone?.takeIf { it.isNotBlank() }
            ?: "N/A"
    }

    fun getSupplierEmail(userId: Int): String {
        return _suppliersProfileCache.value[userId]?.email?.takeIf { it.isNotBlank() }
            ?: "N/A"
    }

    //NO VA
    /* fun getSupplierBusinessName(batch: Batch): String {
        return getSupplierBusinessName(batch.userId ?: 0)
    } */

    fun loadSupplierForOrder(supplierId: Int) {
        viewModelScope.launch {
            if (!_suppliersProfileCache.value.containsKey(supplierId)) {
                loadSupplierProfile(supplierId)
            }
        }
    }
    fun getCurrentUsername(): String? {
        return tokenManager.getUsername()
    }
}