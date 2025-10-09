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

    fun createBatch(batch: Batch) {
        viewModelScope.launch {
            try {
                repository.createBatch(batch)
                _batches.value = repository.getBatches()
            } catch (t: Throwable) {
                // Fallback local si no hay backend
                _batches.value = _batches.value + batch
            }
        }
    }

    fun updateBatch(updated: Batch) {
        viewModelScope.launch {
            try {
                repository.updateBatch(updated)
                _batches.value = repository.getBatches()
            } catch (t: Throwable) {
                // Fallback local en modo desarrollo
                _batches.value = _batches.value.map {
                    if (it.id == updated.id) updated else it
                }
            }
        }
    }

    fun deleteBatch(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteBatch(id)
                _batches.value = repository.getBatches()
            } catch (t: Throwable) {
                // Fallback local
                _batches.value = _batches.value.filterNot { it.id == id }
            }
        }
    }

    fun getBatchById(id: String?): Batch? {
        if (id == null) return null
        return _batches.value.find { it.id == id }
    }

    fun addCustomSupply(custom: CustomSupply) {
        viewModelScope.launch {
            try {
                repository.createCustomSupply(custom)
                _customSupplies.value = repository.getCustomSupplies()
            } catch (t: Throwable) {
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
                _customSupplies.value = _customSupplies.value.filterNot { it.id == custom.id }
            }
        }
    }

    fun getCustomSupplyById(id: String?): CustomSupply? {
        if (id == null) return null
        return _customSupplies.value.find { it.id == id }
    }


}
