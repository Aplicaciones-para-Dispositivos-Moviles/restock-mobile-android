package com.uitopic.restockmobile.features.resources.inventory.domain.repositories

import com.uitopic.restockmobile.features.resources.inventory.domain.models.Batch
import com.uitopic.restockmobile.features.resources.inventory.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.inventory.domain.models.Supply

//Inventory repository interface

interface InventoryRepository {
    // --- Supplies ---
    suspend fun getSupplies(): List<Supply>

    //Custom Supplies
    suspend fun getCustomSupplies(): List<CustomSupply>

    suspend fun getCustomSuppliesByUserId(): List<CustomSupply>
    suspend fun createCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun updateCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun deleteCustomSupply(customSupplyId: Int)

    //Batches
    suspend fun getBatches(): List<Batch>

    suspend fun getBatchesByUserId(): List<Batch>
    suspend fun createBatch(batch: Batch): Batch?
    suspend fun updateBatch(batch: Batch): Batch?
    suspend fun deleteBatch(batchId: String)
}
