package com.uitopic.restockmobile.features.auth.data.remote.mappers

import com.uitopic.restockmobile.core.auth.remote.models.AuthResponseDto
import com.uitopic.restockmobile.core.auth.remote.models.SignInRequestDto
import com.uitopic.restockmobile.core.auth.remote.models.SignUpRequestDto
import com.uitopic.restockmobile.features.auth.domain.models.SignInRequest
import com.uitopic.restockmobile.features.auth.domain.models.SignUpRequest
import com.uitopic.restockmobile.features.auth.domain.models.User

fun AuthResponseDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        roleId = roleId,
        token = token
    )
}

fun SignUpRequest.toDto(): SignUpRequestDto {
    return SignUpRequestDto(
        username = username,
        password = password,
        roleId = roleId
    )
}

fun SignInRequest.toDto(): SignInRequestDto {
    return SignInRequestDto(
        username = username,
        password = password
    )
}