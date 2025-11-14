package com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState
import com.uitopic.restockmobile.features.resources.orders.domain.repositories.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
class OrdersViewModel(/* private val repository: OrdersRepository */) : ViewModel() {

    // ===== ESTADO PARA LA LISTA DE ÓRDENES =====
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()

    // ===== ESTADO PARA EL FLUJO DE CREACIÓN DE ÓRDENES =====

    // Data loaded from repository
    private val _suppliers = MutableStateFlow<List<User>>(emptyList())
    val suppliers: StateFlow<List<User>> = _suppliers.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _customSupplies = MutableStateFlow<List<CustomSupply>>(emptyList())
    val customSupplies: StateFlow<List<CustomSupply>> = _customSupplies.asStateFlow()

    private val _supplierBatches = MutableStateFlow<List<Batch>>(emptyList())
    val supplierBatches: StateFlow<List<Batch>> = _supplierBatches.asStateFlow()

    // Order being built
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
        loadInitialData()
        getAllOrders()
        _filteredOrders.value = _orders.value
    }

    // ===== FUNCIONES PARA LA LISTA DE ÓRDENES =====

    private fun applyFilters() {
        var filtered = _orders.value

        // Apply search filter
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

    fun getAllOrders() {
        viewModelScope.launch {

            //_orders.value = repository.getAllOrders()
            //_orders.value = repository.getAllOrders()
            applyFilters()
        }
    }

    fun addOrders(newOrders: List<Order>) {

        // Agregar al inicio de la lista (más recientes primero)
        _orders.value = newOrders + _orders.value
        applyFilters()
    }



    fun refreshOrders() {
        getAllOrders()
    }

    // ===== FUNCIONES PARA EL FLUJO DE CREACIÓN DE ÓRDENES =====

    private fun loadInitialData() {
        viewModelScope.launch {
            // TODO: Reemplazar con llamadas al repositorio
            _suppliers.value = listOf(
                User(
                    id = 100,
                    username = "alimentos_sa",
                    roleId = 2,
                    profile = Profile(
                        id = 1,
                        firstName = "Carlos",
                        lastName = "Mendoza",
                        email = "contacto@alimentossa.com",
                        phone = "+51 987 654 321",
                        address = "Av. Industrial 123, Lima",
                        country = "Peru",
                        avatar = null,
                        businessName = "Alimentos S.A.",
                        businessAddress = "Av. Industrial 123, Cercado de Lima",
                        description = "Distribuidor mayorista de alimentos de primera calidad",
                        categories = emptyList()
                    )
                ),
                User(
                    id = 101,
                    username = "bebidas_srl",
                    roleId = 2,
                    profile = Profile(
                        id = 2,
                        firstName = "María",
                        lastName = "Torres",
                        email = "ventas@bebidassrl.com",
                        phone = "+51 956 789 123",
                        address = "Jr. Comercio 456, Lima",
                        country = "Peru",
                        avatar = null,
                        businessName = "Bebidas SRL",
                        businessAddress = "Jr. Comercio 456, San Isidro",
                        description = "Proveedor especializado en bebidas y refrescos",
                        categories = emptyList()
                    )
                ),
                User(
                    id = 102,
                    username = "abarrotes_premium",
                    roleId = 2,
                    profile = Profile(
                        id = 3,
                        firstName = "Jorge",
                        lastName = "Ramírez",
                        email = "info@abarrotespremium.com",
                        phone = "+51 912 345 678",
                        address = "Av. Central 789, Lima",
                        country = "Peru",
                        avatar = null,
                        businessName = "Abarrotes Premium",
                        businessAddress = "Av. Central 789, Miraflores",
                        description = "Abarrotes de calidad premium para restaurantes",
                        categories = emptyList()
                    )
                ),
                User(
                    id = 103,
                    username = "distribuidora_central",
                    roleId = 2,
                    profile = Profile(
                        id = 4,
                        firstName = "Ana",
                        lastName = "Flores",
                        email = "pedidos@distribuidoracentral.com",
                        phone = "+51 923 456 789",
                        address = "Av. Distribución 321, Lima",
                        country = "Peru",
                        avatar = null,
                        businessName = "Distribuidora Central",
                        businessAddress = "Av. Distribución 321, La Victoria",
                        description = "Distribución de insumos para el sector gastronómico",
                        categories = emptyList()
                    )
                )
            )

            _categories.value = listOf(
                "all categories",
                "Alimentos",
                "Bebidas",
                "Lácteos",
                "Carnes",
                "Verduras",
                "Panadería"
            )
        }
    }

    fun loadSuppliers() {
        viewModelScope.launch {
            _suppliers.value = _suppliers.value
        }
    }

    fun loadUserCustomSupplies(userId: Int) {
        viewModelScope.launch {
            //_customSupplies.value = repository.getUserCustomSupplies(userId)
            _customSupplies.value = listOf(
                CustomSupply(
                    id = 1,
                    minStock = 5,
                    maxStock = 50,
                    price = 15.50,
                    userId = userId,
                    supplyId = 11,
                    currencyCode = "PEN",
                    description = "Aceite vegetal de 1L",
                    unit = UnitModel(name = "Litro", abbreviation = "L"),
                    supply = Supply(
                        id = 11,
                        name = "Aceite vegetal",
                        description = "Aceite refinado para cocina",
                        perishable = false,
                        category = "Alimentos"
                    )
                ),
                CustomSupply(
                    id = 2,
                    minStock = 2,
                    maxStock = 20,
                    price = 30.0,
                    userId = userId,
                    supplyId = 12,
                    currencyCode = "PEN",
                    description = "Harina de trigo premium 5kg",
                    unit = UnitModel(name = "Kilogramo", abbreviation = "kg"),
                    supply = Supply(
                        id = 12,
                        name = "Harina de trigo",
                        description = "Harina blanca para panadería",
                        perishable = false,
                        category = "Alimentos"
                    )
                ),
                CustomSupply(
                    id = 3,
                    minStock = 3,
                    maxStock = 30,
                    price = 5.0,
                    userId = userId,
                    supplyId = 13,
                    currencyCode = "PEN",
                    description = "Azúcar blanca 1kg",
                    unit = UnitModel(name = "Kilogramo", abbreviation = "kg"),
                    supply = Supply(
                        id = 13,
                        name = "Azúcar blanca",
                        description = "Azúcar refinada",
                        perishable = false,
                        category = "Alimentos"
                    )
                ),
                CustomSupply(
                    id = 4,
                    minStock = 10,
                    maxStock = 100,
                    price = 8.50,
                    userId = userId,
                    supplyId = 14,
                    currencyCode = "PEN",
                    description = "Sal de mesa 1kg",
                    unit = UnitModel(name = "Kilogramo", abbreviation = "kg"),
                    supply = Supply(
                        id = 14,
                        name = "Sal",
                        description = "Sal refinada de mesa",
                        perishable = false,
                        category = "Alimentos"
                    )
                ),
                CustomSupply(
                    id = 5,
                    minStock = 8,
                    maxStock = 80,
                    price = 25.00,
                    userId = userId,
                    supplyId = 15,
                    currencyCode = "PEN",
                    description = "Arroz extra 5kg",
                    unit = UnitModel(name = "Kilogramo", abbreviation = "kg"),
                    supply = Supply(
                        id = 15,
                        name = "Arroz",
                        description = "Arroz blanco extra",
                        perishable = false,
                        category = "Alimentos"
                    )
                )
            )
        }
    }

    fun loadSupplierBatchesForSupply(supplyId: Int) {
        viewModelScope.launch {
            //_supplierBatches.value = repository.getSupplierBatchesForSupply(supplyId)
            _supplierBatches.value = listOf(
                Batch(
                    id = "1",
                    userId = 100,
                    stock = 320.0,
                    expirationDate = "2025-12-30",
                    customSupply = CustomSupply(
                        id = 1,
                        minStock = 5,
                        maxStock = 500,
                        price = 10.50,
                        userId = 100,
                        supplyId = supplyId,
                        currencyCode = "PEN",
                        description = "Lote premium",
                        unit = UnitModel(name = "Unidad", abbreviation = "u"),
                        supply = Supply(
                            id = supplyId,
                            name = "Supply $supplyId",
                            description = "Producto de calidad",
                            perishable = true,
                            category = "Alimentos"
                        )
                    )
                ),
                Batch(
                    id = "2",
                    userId = 101,
                    stock = 400.0,
                    expirationDate = "2025-12-25",
                    customSupply = CustomSupply(
                        id = 2,
                        minStock = 5,
                        maxStock = 500,
                        price = 12.50,
                        userId = 101,
                        supplyId = supplyId,
                        currencyCode = "PEN",
                        description = "Lote estándar",
                        unit = UnitModel(name = "Unidad", abbreviation = "u"),
                        supply = Supply(
                            id = supplyId,
                            name = "Supply $supplyId",
                            description = "Producto de calidad",
                            perishable = true,
                            category = "Alimentos"
                        )
                    )
                ),
                Batch(
                    id = "3",
                    userId = 102,
                    stock = 200.0,
                    expirationDate = "2025-12-20",
                    customSupply = CustomSupply(
                        id = 3,
                        minStock = 5,
                        maxStock = 500,
                        price = 13.50,
                        userId = 102,
                        supplyId = supplyId,
                        currencyCode = "PEN",
                        description = "Lote económico",
                        unit = UnitModel(name = "Unidad", abbreviation = "u"),
                        supply = Supply(
                            id = supplyId,
                            name = "Supply $supplyId",
                            description = "Producto de calidad",
                            perishable = true,
                            category = "Alimentos"
                        )
                    )
                ),
                Batch(
                    id = "4",
                    userId = 103,
                    stock = 150.0,
                    expirationDate = "2025-12-15",
                    customSupply = CustomSupply(
                        id = 4,
                        minStock = 5,
                        maxStock = 500,
                        price = 13.00,
                        userId = 103,
                        supplyId = supplyId,
                        currencyCode = "PEN",
                        description = "Lote especial",
                        unit = UnitModel(name = "Unidad", abbreviation = "u"),
                        supply = Supply(
                            id = supplyId,
                            name = "Supply $supplyId",
                            description = "Producto de calidad",
                            perishable = true,
                            category = "Alimentos"
                        )
                    )
                )
            )
        }
    }

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
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (_orderBatchItems.value.isEmpty()) {
                    onError("No items in order")
                    return@launch
                }

                // Agrupar items por proveedor (userId del batch)
                val itemsBySupplier = _orderBatchItems.value.groupBy { it.batch?.userId }

                val newOrders = mutableListOf<Order>()

                itemsBySupplier.forEach { (supplierId, items) ->
                    if (supplierId != null) {
                        val supplierUser = _suppliers.value.find { it.id == supplierId }

                        val order = Order(
                            id = 1,
                            adminRestaurantId = adminRestaurantId,
                            supplierId = supplierId,
                            supplier = supplierUser ?: User(
                                id = supplierId,
                                username = "unknown",
                                roleId = 2,
                                profile = Profile(
                                    id = 0,
                                    firstName = "Unknown",
                                    lastName = "Supplier",
                                    email = "",
                                    phone = "",
                                    address = "",
                                    country = "Peru",
                                    avatar = null,
                                    businessName = "Unknown Business",
                                    businessAddress = "",
                                    description = null,
                                    categories = emptyList()
                                )
                            ),
                            requestedDate = java.time.LocalDate.now().toString(),
                            partiallyAccepted = false,
                            requestedProductsCount = items.size,
                            totalPrice = items.sumOf {
                                (it.batch?.customSupply?.price ?: 0.0) * it.quantity
                            },
                            state = OrderState.ON_HOLD,
                            situation = OrderSituation.PENDING,
                            batchItems = items
                        )

                        // TODO: Reemplazar con llamada al repositorio
                        newOrders.add(order)
                    }
                }

                // Agregar las órdenes creadas a la lista principal
                addOrders(newOrders)

                clearOrderState()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error creating order")
            }
        }
    }

    fun clearOrderState() {
        _orderBatchItems.value = emptyList()
        _supplierBatches.value = emptyList()
        _customSupplies.value = emptyList()
    }
}
*/

//===============================================================


/* class OrdersViewModel: ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Reemplazar con llamada al repositorio
                // _orders.value = orderRepository.getAllOrders()
                _orders.value = emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading orders"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val allOrders = _orders.value

            val filtered = if (_searchQuery.value.isNotBlank()) {
                allOrders.filter { order ->
                    order.supplier.username.contains(_searchQuery.value, ignoreCase = true)
                }
            } else {
                allOrders
            }

            _orders.value = filtered
        }
    }

    fun refreshOrders() {
        loadOrders()
    }
} */


