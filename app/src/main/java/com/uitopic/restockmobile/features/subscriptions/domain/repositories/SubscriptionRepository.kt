package com.uitopic.restockmobile.features.subscriptions.domain.repositories

import com.uitopic.restockmobile.features.subscriptions.domain.models.SubscriptionPlan

interface SubscriptionRepository {
    suspend fun getSubscriptionPlans(): List<SubscriptionPlan>
    suspend fun updateSubscription(userId: Int, subscriptionType: Int): Result<Unit>
}
