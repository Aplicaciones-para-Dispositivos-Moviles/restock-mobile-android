package com.uitopic.restockmobile.features.subscriptions.domain.models

data class SubscriptionPlan(
    val name: String,
    val price: String,
    val features: List<String>,
    val popular: Boolean = false
)
