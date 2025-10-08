package com.uitopic.restockmobile.features.resources.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.repositories.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _supplies = MutableStateFlow<List<Supply>>(emptyList())
    val supplies: StateFlow<List<Supply>> = _supplies.asStateFlow()

    private val _customSupplies = MutableStateFlow<List<CustomSupply>>(emptyList())
    val customSupplies: StateFlow<List<CustomSupply>> = _customSupplies.asStateFlow()

    private val _batches = MutableStateFlow<List<Batch>>(emptyList())
    val batches: StateFlow<List<Batch>> = _batches.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            try {
                _supplies.value = repository.getSupplies()
                _customSupplies.value = repository.getCustomSupplies()
                _batches.value = repository.getBatches()
            } catch (t: Throwable) {
                // TODO: manejar error (mostrar snackbar o log)
            }
        }
    }

    fun addBatchFromCustom(custom: CustomSupply) {
        viewModelScope.launch {
            try {
                val batch = Batch(
                    id = "",
                    userId = custom.userId,
                    customSupply = custom,
                    stock = custom.minStock,
                    expirationDate = null
                )

                val created = repository.createBatch(batch)
                if (created != null) {
                    _batches.value = repository.getBatches()
                }
            } catch (t: Throwable) {
                // TODO: manejar error
            }
        }
    }

    fun deleteBatch(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteBatch(id)
            } catch (t: Throwable) {
                // TODO: manejar error de borrado
            } finally {
                try {
                    _batches.value = repository.getBatches()
                } catch (_: Throwable) {}
            }
        }
    }

    fun addCustomSupply(custom: CustomSupply) {
        viewModelScope.launch {
            try {
                repository.createCustomSupply(custom)
                _customSupplies.value = repository.getCustomSupplies()
            } catch (t: Throwable) {
                // Fallback local por si no hay backend aún
                _customSupplies.value = _customSupplies.value + custom
            }
        }
    }

    fun updateCustomSupply(updated: CustomSupply) {
        viewModelScope.launch {
            try {
                repository.updateCustomSupply(updated)
                _customSupplies.value = repository.getCustomSupplies()
            } catch (t: Throwable) {
                // Fallback local para desarrollo sin backend
                _customSupplies.value = _customSupplies.value.map {
                    if (it.id == updated.id) updated else it
                }
            }
        }
    }

    fun deleteCustomSupply(custom: CustomSupply) {
        viewModelScope.launch {
            try {
                repository.deleteCustomSupply(custom.id)
                _customSupplies.value = repository.getCustomSupplies()
            } catch (t: Throwable) {
                // Fallback local por si no hay backend aún
                _customSupplies.value = _customSupplies.value.filterNot { it.id == custom.id }
            }
        }
    }

    fun getCustomSupplyById(id: String?): CustomSupply? {
        if (id == null) return null
        return _customSupplies.value.find { it.id == id }
    }
}
