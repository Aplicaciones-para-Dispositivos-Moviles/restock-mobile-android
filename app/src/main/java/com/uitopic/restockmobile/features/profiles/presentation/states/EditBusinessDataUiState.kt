package com.uitopic.restockmobile.features.profiles.presentation.states

import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory

data class EditBusinessDataUiState(
    val businessName: String = "",
    val businessAddress: String = "",
    val description: String = "",
    val selectedCategories: List<BusinessCategory> = emptyList(),
    val availableCategories: List<BusinessCategory> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCategories: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val businessNameError: String? = null,
    val businessAddressError: String? = null,
    val categoriesError: String? = null
)