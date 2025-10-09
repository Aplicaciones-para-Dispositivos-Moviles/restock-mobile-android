package com.uitopic.restockmobile.features.profiles.domain.models

data class Profile(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String,
    val country: String,
    val avatar: String?,
    val businessName: String,
    val businessAddress: String,
    val description: String?,
    val categories: List<BusinessCategory>
)
