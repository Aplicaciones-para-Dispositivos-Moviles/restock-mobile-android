package com.uitopic.restockmobile.features.resources.data.remote.mappers

import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.models.CustomSupplyDto
import com.uitopic.restockmobile.features.resources.data.remote.models.SupplyDto
import com.uitopic.restockmobile.features.resources.data.remote.models.UnitDto
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel

fun SupplyDto.toDomain(): Supply =
    Supply(
        id = this.id ?: 0,
        name = this.name ?: "",
        description = this.description,
        perishable = this.perishable ?: false,
        category = this.category
    )

fun BatchDto.toDomain(): Batch =
    Batch(
        id = this.id ?: "",
        userId = this.userId ?: 0,
        customSupply = this.custom_supply?.toDomain(),
        stock = this.stock ?: 0,
        expirationDate = this.expiration_date
    )
// ---------------------------------------------------------
// CUSTOM SUPPLY
// ---------------------------------------------------------
fun CustomSupplyDto.toDomain(): CustomSupply =
    CustomSupply(
        id = this.id?.toString() ?: "",
        description = this.description,
        minStock = this.minStock,
        maxStock = this.maxStock,
        price = this.price,
        currencyCode = this.currencyCode,
        userId = this.userId,
        supplyId = this.supplyId,
        supply = Supply(
            id = this.supplyId,
            name = "",
            description = this.description,
            perishable = false,
            category = null
        ),
        unit = UnitModel(name = "", abbreviation = "")
    )

fun CustomSupply.toDto(userId: Int): CustomSupplyDto =
    CustomSupplyDto(
        id = this.id.toIntOrNull(),
        supplyId = this.supplyId,
        description = this.description,
        minStock = this.minStock,
        maxStock = this.maxStock,
        price = this.price,
        currencyCode = this.currencyCode,
        userId = userId
    )