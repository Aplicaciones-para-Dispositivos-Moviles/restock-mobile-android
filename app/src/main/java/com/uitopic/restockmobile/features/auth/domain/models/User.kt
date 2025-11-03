package com.uitopic.restockmobile.features.auth.domain.models

import com.uitopic.restockmobile.features.profiles.domain.models.Profile

data class User(
    val id: Int,
    val username: String,
    val roleId: Int,
    val token: String? = null,
    val profile: Profile? = null
)
