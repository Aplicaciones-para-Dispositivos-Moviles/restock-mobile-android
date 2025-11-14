package com.uitopic.restockmobile.features.auth.presentation.states

import com.uitopic.restockmobile.features.auth.domain.models.User

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val showPassword: Boolean = false,
    val user: User? = null
)