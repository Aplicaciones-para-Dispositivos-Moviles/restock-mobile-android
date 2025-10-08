package com.uitopic.restockmobile.features.resources.domain.repositories

import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply

interface InventoryRepository {
    // --- Supplies ---
    suspend fun getSupplies(): List<Supply>

    // --- Custom Supplies ---
    suspend fun getCustomSupplies(): List<CustomSupply>
    suspend fun createCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun updateCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun deleteCustomSupply(customSupplyId: String)

    // --- Batches ---
    suspend fun getBatches(): List<Batch>
    suspend fun createBatch(batch: Batch): Batch?
    suspend fun updateBatch(batch: Batch): Batch?
    suspend fun deleteBatch(batchId: String)
}
