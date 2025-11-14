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

// DTO -> Domain
fun OrderDto.toDomain(): Order {
    return Order(
        id = this.id ?: 0,
        adminRestaurantId = this.adminRestaurantId ?: 0,
        supplierId = this.supplierId ?: 0,
        supplier = this.supplier?.toDomain() ?: User(
            id = this.supplierId ?: 0,
            username = "unknown",
            roleId = 2,
            profile = Profile(
                id = 0,
                firstName = "Unknown",
                lastName = "Supplier",
                email = "",
                phone = "",
                address = "",
                country = "",
                avatar = null,
                businessName = "",
                businessAddress = "",
                description = null,
                categories = emptyList()
            ),
            subscription = 0
        ),
        requestedDate = this.requestedDate ?: "",
        partiallyAccepted = this.partiallyAccepted ?: false,
        requestedProductsCount = this.requestedProductsCount ?: 0,
        totalPrice = this.totalPrice ?: 0.0,
        state = this.state?.toOrderState() ?: OrderState.ON_HOLD,
        situation = this.situation?.toOrderSituation() ?: OrderSituation.PENDING,
        batchItems = this.batchItems?.map { it.toDomain() } ?: emptyList()
    )
}

fun OrderBatchItemDto.toDomain(): OrderBatchItem {
    return OrderBatchItem(
        batchId = this.batchId ?: 0,
        quantity = this.quantity ?: 0.0,
        accepted = this.accepted ?: false,
        batch = this.batch?.toDomain()
    )
}

// Domain -> DTO
fun Order.toRequestDto(): OrderRequestDto {
    return OrderRequestDto(
        adminRestaurantId = this.adminRestaurantId,
        supplierId = this.supplierId,
        requestedDate = this.requestedDate,
        partiallyAccepted = this.partiallyAccepted,
        requestedProductsCount = this.requestedProductsCount,
        totalPrice = this.totalPrice,
        state = this.state.name,
        situation = this.situation.name,
        batches = this.batchItems.map { it.toRequestDto() }
    )
}

fun OrderBatchItem.toRequestDto(): OrderBatchItemRequestDto {
    return OrderBatchItemRequestDto(
        batchId = this.batchId,
        quantity = this.quantity,
        accept = this.accepted
    )
}

// String converters
private fun String.toOrderState(): OrderState {
    return try {
        OrderState.valueOf(this)
    } catch (e: IllegalArgumentException) {
        OrderState.ON_HOLD
    }
}

private fun String.toOrderSituation(): OrderSituation {
    return try {
        OrderSituation.valueOf(this)
    } catch (e: IllegalArgumentException) {
        OrderSituation.PENDING
    }
}