package com.uitopic.restockmobile.features.profiles.data.remote.mappers

import com.uitopic.restockmobile.core.auth.remote.models.UserDto
import com.uitopic.restockmobile.features.auth.domain.models.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id ?: 0,
        username = this.username ?: "",
        roleId = this.roleId ?: 0,
        profile = this.profile?.toDomain(),  // si tienes ProfileDto mapper
        subscription = this.subscription ?: 0,
    )
}