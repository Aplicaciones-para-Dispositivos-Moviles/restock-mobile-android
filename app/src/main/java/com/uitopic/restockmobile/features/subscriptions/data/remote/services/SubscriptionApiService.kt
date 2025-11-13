package com.uitopic.restockmobile.features.subscriptions.data.remote.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

data class SubscriptionRequestBody(val subscription: Int)

interface SubscriptionApiService {
    @PUT("users/{id}/subscription")
    suspend fun updateSubscription(
        @Path("id") userId: Int,
        @Body subscription: SubscriptionRequestBody
    ): Response<Unit>
}
