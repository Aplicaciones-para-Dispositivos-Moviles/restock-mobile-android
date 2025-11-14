package com.uitopic.restockmobile.features.auth.domain.models

data class SignUpRequest(
    val username: String,
    val password: String,
    val roleId: Int = 2  // Default: 2 (restaurant admin)
)