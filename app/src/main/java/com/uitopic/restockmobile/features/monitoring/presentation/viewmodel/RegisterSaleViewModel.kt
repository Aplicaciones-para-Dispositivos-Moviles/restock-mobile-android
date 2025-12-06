package com.uitopic.restockmobile.features.monitoring.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.domain.model.DishOption
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplyOption
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import com.uitopic.restockmobile.features.monitoring.domain.usecases.CancelSaleUseCase
import com.uitopic.restockmobile.features.monitoring.domain.usecases.CreateSaleUseCase
import com.uitopic.restockmobile.features.monitoring.domain.usecases.GetAllSalesUseCase
import com.uitopic.restockmobile.features.planning.data.remote.datasources.RecipeRemoteDataSource
import com.uitopic.restockmobile.features.resources.inventory.domain.repositories.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaleUiState(
    val isLoading: Boolean = false,
    val registeredSales: List<RegisteredSale> = emptyList(),
    val dishOptions: List<DishOption> = emptyList(),
    val supplyOptions: List<SupplyOption> = emptyList(),
    val isLoadingOptions: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class RegisterSaleViewModel @Inject constructor(
    private val createSaleUseCase: CreateSaleUseCase,
    private val getAllSalesUseCase: GetAllSalesUseCase,
    private val cancelSaleUseCase: CancelSaleUseCase,
    private val recipeRemoteDataSource: RecipeRemoteDataSource,
    private val inventoryRepository: InventoryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleUiState())
    val uiState: StateFlow<SaleUiState> = _uiState.asStateFlow()

    init {
        loadSales()
        loadDishesAndSupplies()
    }

    private fun loadDishesAndSupplies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingOptions = true)

            // Cargar recetas (dishes) desde el backend
            recipeRemoteDataSource.getAllRecipes()
                .onSuccess { recipes ->
                    val dishOptions = recipes.map { recipe ->
                        DishOption(
                            label = recipe.name ?: "Unknown Dish",
                            id = recipe.id ?: 0,
                            price = recipe.price ?: 0.0
                        )
                    }
                    _uiState.value = _uiState.value.copy(dishOptions = dishOptions)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to load dishes: ${error.message}"
                    )
                }

            // Cargar custom supplies del usuario desde el backend
            try {
                val customSupplies = inventoryRepository.getCustomSuppliesByUserId()
                val supplyOptions = customSupplies.map { customSupply ->
                    SupplyOption(
                        id = customSupply.id,
                        name = customSupply.supply?.name ?: "Unknown Supply",
                        description = customSupply.description,
                        unitPrice = customSupply.price
                    )
                }
                _uiState.value = _uiState.value.copy(
                    supplyOptions = supplyOptions,
                    isLoadingOptions = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingOptions = false,
                    error = "Failed to load supplies: ${e.message}"
                )
            }
        }
    }

    fun loadSales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getAllSalesUseCase()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        registeredSales = result.data ?: emptyList(),
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun createSale(
        dishSelections: List<DishSelection>,
        supplySelections: List<SupplySelection>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userId = tokenManager.getUserId()

            when (val result = createSaleUseCase(dishSelections, supplySelections, userId)) {
                is Resource.Success -> {
                    // Recargar todas las ventas desde el backend para obtener datos actualizados
                    loadSales()
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Sale created successfully",
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun cancelSale(saleId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = cancelSaleUseCase(saleId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        registeredSales = _uiState.value.registeredSales.filter { it.id != saleId },
                        successMessage = "Sale cancelled successfully",
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
