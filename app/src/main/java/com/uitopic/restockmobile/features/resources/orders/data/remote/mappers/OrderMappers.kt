package com.uitopic.restockmobile.features.resources.orders.data.remote.mappers


import com.uitopic.restockmobile.features.auth.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.profiles.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.resources.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderBatchItemDto
import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderBatchItemRequestDto
import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderDto
import com.uitopic.restockmobile.features.resources.orders.data.remote.models.OrderRequestDto
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState

fun OrderDto.toDomain(): Order {
    //  Si el backend no devuelve el supplier completo, creamos uno temporal
    val supplierUser = supplier?.toDomain() ?: run {
        val suppId = supplierId ?: 0
        com.uitopic.restockmobile.features.auth.domain.models.User(
            id = suppId,
            username = "Supplier_$suppId",
            roleId = 1,
            profile = null,
            subscription = 0
        )
    }

    //  Parsear la fecha correctamente (el backend devuelve "date" en formato ISO)
    val parsedDate = requestedDate?.let { dateStr ->
        try {
            // Si viene en formato ISO: "2025-11-15T00:00:00.000+00:00"
            dateStr.split("T").firstOrNull() ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    } ?: ""

    return Order(
        id = id ?: 0,
        adminRestaurantId = adminRestaurantId ?: 0,
        supplierId = supplierId ?: 0,
        supplier = supplierUser,
        requestedDate = parsedDate,
        partiallyAccepted = partiallyAccepted ?: false,
        requestedProductsCount = requestedProductsCount ?: 0,
        totalPrice = totalPrice ?: 0.0,
        state = state?.let { OrderState.valueOf(it) } ?: OrderState.ON_HOLD,
        situation = situation?.let { OrderSituation.valueOf(it) } ?: OrderSituation.PENDING,
        batchItems = batchItems?.map { it.toDomain() } ?: emptyList()
    )
}

fun OrderBatchItemDto.toDomain(): OrderBatchItem {
    return OrderBatchItem(
        batchId = batchId ?: 0,
        quantity = quantity ?: 0.0,
        accepted = accepted ?: false,
        batch = batch?.toDomain()
    )
}

fun Order.toRequestDto(): OrderRequestDto {
    val dto = OrderRequestDto(
        adminRestaurantId = adminRestaurantId,
        supplierId = supplierId,
        requestedDate = requestedDate,
        partiallyAccepted = partiallyAccepted,
        requestedProductsCount = requestedProductsCount,
        totalPrice = totalPrice,
        state = state.name,
        situation = situation.name,
        batches = batchItems.map { it.toRequestDto() }
    ) 
    
    return dto
}

fun OrderBatchItem.toRequestDto(): OrderBatchItemRequestDto {
    return OrderBatchItemRequestDto(
        batchId = batchId,
        quantity = quantity,
        accept = accepted
    )
}