package com.uitopic.restockmobile.features.profiles.presentation.states

data class DeleteAccountUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)