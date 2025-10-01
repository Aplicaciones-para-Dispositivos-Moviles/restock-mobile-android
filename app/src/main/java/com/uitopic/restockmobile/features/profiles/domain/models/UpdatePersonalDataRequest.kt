package com.uitopic.restockmobile.features.profiles.domain.models

data class UpdatePersonalDataRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val country: String
)

