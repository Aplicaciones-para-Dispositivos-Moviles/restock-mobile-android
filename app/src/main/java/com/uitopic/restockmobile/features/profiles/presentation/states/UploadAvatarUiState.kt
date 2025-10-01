package com.uitopic.restockmobile.features.profiles.presentation.states
data class UploadAvatarUiState(
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val error: String? = null,
    val success: Boolean = false
)