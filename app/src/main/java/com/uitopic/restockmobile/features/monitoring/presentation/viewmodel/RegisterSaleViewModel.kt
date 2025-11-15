package com.uitopic.restockmobile.features.monitoring.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import com.uitopic.restockmobile.features.monitoring.domain.usecases.CancelSaleUseCase
import com.uitopic.restockmobile.features.monitoring.domain.usecases.CreateSaleUseCase
import com.uitopic.restockmobile.features.monitoring.domain.usecases.GetAllSalesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaleUiState(
    val isLoading: Boolean = false,
    val registeredSales: List<RegisteredSale> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class RegisterSaleViewModel @Inject constructor(
    private val createSaleUseCase: CreateSaleUseCase,
    private val getAllSalesUseCase: GetAllSalesUseCase,
    private val cancelSaleUseCase: CancelSaleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleUiState())
    val uiState: StateFlow<SaleUiState> = _uiState.asStateFlow()

    init {
        loadSales()
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
        supplySelections: List<SupplySelection>,
        userId: Int = 1 // Por defecto userId 1, puedes obtenerlo del usuario logueado
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

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
