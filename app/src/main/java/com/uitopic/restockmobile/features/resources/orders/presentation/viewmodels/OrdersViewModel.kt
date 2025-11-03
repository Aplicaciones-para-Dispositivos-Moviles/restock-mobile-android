package com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.planning.presentation.states.RecipeUiState
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class OrdersViewModel: ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    // State for SelectCategoryScreen
    private val _suppliers = MutableStateFlow<List<User>>(emptyList())
    val suppliers: StateFlow<List<User>> = _suppliers.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all categories")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()


    // State for SelectSupplyScreen
    private val _customSupplies = MutableStateFlow<List<CustomSupply>>(emptyList())
    val customSupplies: StateFlow<List<CustomSupply>> = _customSupplies.asStateFlow()

    private val _selectedSupply = MutableStateFlow<CustomSupply?>(null)
    val selectedSupply: StateFlow<CustomSupply?> = _selectedSupply.asStateFlow()

    // State for SelectSupplierForSupplyScreen
    private val _supplierBatches = MutableStateFlow<List<Batch>>(emptyList())
    val supplierBatches: StateFlow<List<Batch>> = _supplierBatches.asStateFlow()

    private val _selectedBatches = MutableStateFlow<List<Batch>>(emptyList())
    val selectedBatches: StateFlow<List<Batch>> = _selectedBatches.asStateFlow()

    // State for OrderDetailScreen
    private val _orderBatchItems  = MutableStateFlow<List<OrderBatchItem>>(emptyList())
    val orderBatchItems: StateFlow<List<OrderBatchItem>> = _orderBatchItems.asStateFlow()


    private fun applyFilters() {
        var filtered = _orders.value

        // Apply search filter
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { order ->
                order.supplier.username.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        _orders.value = filtered

        // Apply search filter
        /*if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { order ->
                order.supplier.username?.contains(_searchQuery.value, ignoreCase = true) == true
            }
        } */

        /* // Apply sort
        if (_sortByPriceDesc.value) {
            filtered = filtered.sortedByDescending { it.price ?: 0.0 }
        }*/

        //_uiState.value = RecipeUiState.Success(filtered)
    }



    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }


    fun getAllOrders() {
        viewModelScope.launch {
            // Aquí iría la llamada al repositorio
            // _orders.value = orderRepository.getAllOrders()
        }
    }

    // ===================================================


    init {
        getAllOrders()
    }

}

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