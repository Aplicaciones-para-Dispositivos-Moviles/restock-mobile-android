package com.uitopic.restockmobile.features.profiles.presentation.states

import com.uitopic.restockmobile.features.profiles.domain.models.Profile

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
