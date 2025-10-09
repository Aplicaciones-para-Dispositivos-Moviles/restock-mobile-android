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
        id = this._id ?: "",
        name = this.name ?: "",
        description = this.description,
        perishable = this.perishable ?: false,
        category = this.category
    )
fun CustomSupplyDto.toDomain(): CustomSupply =
    CustomSupply(
        id = this._id ?: "",
        minStock = this.min_stock ?: 0,
        maxStock = this.max_stock ?: 0,
        price = this.price ?: 0.0,
        userId = this.user_id,
        supply = this.supply?.toDomain() ?: Supply("", "Unknown", null, false, null),
        unit = UnitModel(name = this.unit?.name ?: "", abbreviation = this.unit?.abbreviation ?: "")
    )
fun BatchDto.toDomain(): Batch =
    Batch(
        id = this._id ?: "",
        userId = this.user_id,
        customSupply = this.custom_supply?.toDomain(),
        stock = this.stock ?: 0,
        expirationDate = this.expiration_date
    )
fun CustomSupply.toDto(): CustomSupplyDto =
    CustomSupplyDto(
        _id = this.id,
        min_stock = this.minStock,
        max_stock = this.maxStock,
        price = this.price,
        user_id = this.userId,
        supply = SupplyDto(
            _id = this.supply.id,
            name = this.supply.name,
            description = this.supply.description,
            perishable = this.supply.perishable,
            category = this.supply.category
        ),
        unit = UnitDto(name = this.unit.name, abbreviation = this.unit.abbreviation)
    )