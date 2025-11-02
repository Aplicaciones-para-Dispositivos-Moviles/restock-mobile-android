package com.uitopic.restockmobile.features.profiles.domain.models

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)