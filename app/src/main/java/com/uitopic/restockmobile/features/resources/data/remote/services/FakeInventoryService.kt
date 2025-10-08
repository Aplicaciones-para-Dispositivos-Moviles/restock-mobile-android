package com.uitopic.restockmobile.features.resources.data.remote.services

import com.uitopic.restockmobile.features.resources.data.remote.models.*
import okhttp3.ResponseBody
import retrofit2.Response

class FakeInventoryService : InventoryService {

    private val supplies = mutableListOf(
        SupplyDto(_id = "1", name = "Arroz", description = "Grano básico", perishable = false, category = "Granos"),
        SupplyDto(_id = "2", name = "Leche", description = "Entera 1L", perishable = true, category = "Lácteos"),
        SupplyDto(_id = "3", name = "Aceite", description = "Vegetal 1L", perishable = false, category = "Aceites")
    )

    private val customSupplies = mutableListOf(
        CustomSupplyDto(
            _id = "cs1",
            min_stock = 10,
            max_stock = 50,
            price = 5.5,
            user_id = "user123",
            supply = supplies[0],
            unit = UnitDto(name = "kg", abbreviation = "kg")
        ),
        CustomSupplyDto(
            _id = "cs2",
            min_stock = 5,
            max_stock = 30,
            price = 4.0,
            user_id = "user123",
            supply = supplies[1],
            unit = UnitDto(name = "litro", abbreviation = "L")
        )
    )

    private val batches = mutableListOf(
        BatchDto(
            _id = "b1",
            user_id = "user123",
            custom_supply = customSupplies[0],
            stock = 20,
            expiration_date = "2025-12-30"
        )
    )

    override suspend fun getSupplies(): Response<List<SupplyDto>> = Response.success(supplies)

    override suspend fun getCustomSupplies(): Response<List<CustomSupplyDto>> = Response.success(customSupplies)

    override suspend fun getBatches(): Response<List<BatchDto>> = Response.success(batches)

    override suspend fun createBatch(batch: BatchDto): Response<BatchDto> {
        val newBatch = batch.copy(_id = "b${batches.size + 1}")
        batches.add(newBatch)
        return Response.success(newBatch)
    }

    override suspend fun deleteBatch(id: String): Response<Unit> {
        batches.removeIf { it._id == id }
        return Response.success(Unit)
    }
    suspend fun createCustomSupply(custom: CustomSupplyDto): Response<CustomSupplyDto> {
        val newCustom = custom.copy(_id = "cs${customSupplies.size + 1}")
        customSupplies.add(newCustom)
        return Response.success(newCustom)
    }
    suspend fun updateCustomSupply(updated: CustomSupplyDto): Response<CustomSupplyDto> {
        val index = customSupplies.indexOfFirst { it._id == updated._id }
        return if (index != -1) {
            customSupplies[index] = updated
            Response.success(updated)
        } else {
            Response.error(404, ResponseBody.create(null, "Custom supply not found"))
        }
    }
    override suspend fun deleteCustomSupply(id: String): Response<Unit> {
        val removed = customSupplies.removeIf { it._id == id }
        if (removed) {
            batches.removeIf { it.custom_supply?._id == id }
            return Response.success(Unit)
        } else {
            return Response.error(404, ResponseBody.create(null, "Custom supply not found"))
        }
    }
    override suspend fun updateBatch(id: String, batch: BatchDto): Response<BatchDto> {
        val index = batches.indexOfFirst { it._id == id }
        return if (index != -1) {
            val updated = batch.copy(_id = id)
            batches[index] = updated
            Response.success(updated)
        } else {
            Response.error(404, okhttp3.ResponseBody.create(null, "Batch not found"))
        }
    }


}
