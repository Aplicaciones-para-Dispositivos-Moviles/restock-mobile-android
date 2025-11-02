package com.uitopic.restockmobile.features.auth.presentation.states

data class SignUpUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false
)