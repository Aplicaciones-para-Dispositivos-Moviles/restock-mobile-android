package com.uitopic.restockmobile.features.subscriptions.presentation.states

data class SubscriptionState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
