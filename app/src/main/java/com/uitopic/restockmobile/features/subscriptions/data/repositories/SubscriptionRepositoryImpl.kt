package com.uitopic.restockmobile.features.subscriptions.data.repositories

import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.subscriptions.data.remote.services.SubscriptionApiService
import com.uitopic.restockmobile.features.subscriptions.data.remote.services.SubscriptionRequestBody
import com.uitopic.restockmobile.features.subscriptions.domain.models.SubscriptionPlan
import com.uitopic.restockmobile.features.subscriptions.domain.repositories.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val apiService: SubscriptionApiService,
    private val tokenManager: TokenManager
) : SubscriptionRepository {
    override suspend fun getSubscriptionPlans(): List<SubscriptionPlan> {
        return listOf(
            SubscriptionPlan(
                name = "Anual Plan",
                price = "S/. 39.99 / monthly",
                features = listOf(
                    "Automated inventory management",
                    "Order and purchase control",
                    "Reporting and analytics",
                    "Critical stock notifications",
                    "Integration with suppliers"
                ),
                popular = true
            ),
            SubscriptionPlan(
                name = "Semester Plan",
                price = "S/. 49.99 / monthly",
                features = listOf(
                    "Automated inventory management",
                    "Order and purchase control",
                    "Reporting and analytics",
                    "Critical stock notifications",
                    "Integration with suppliers"
                )
            )
        )
    }

    override suspend fun updateSubscription(userId: Int, subscriptionType: Int): Result<Unit> {
        return try {
            val response = apiService.updateSubscription(
                userId = userId,
                subscription = SubscriptionRequestBody(subscriptionType)
            )

            if (response.isSuccessful) {
                // Actualizar el subscription en el TokenManager localmente
                val currentUserId = tokenManager.getUserId()
                val username = tokenManager.getUsername() ?: ""
                val roleId = tokenManager.getRoleId()
                tokenManager.saveUserData(currentUserId, username, roleId, subscriptionType)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update subscription: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
