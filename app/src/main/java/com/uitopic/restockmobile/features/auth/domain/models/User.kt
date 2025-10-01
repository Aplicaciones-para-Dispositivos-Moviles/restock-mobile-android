package com.uitopic.restockmobile.features.auth.domain.models

data class User(
    val id: Int,
    val username: String,
    val roleId: Int,
    val token: String? = null
)
