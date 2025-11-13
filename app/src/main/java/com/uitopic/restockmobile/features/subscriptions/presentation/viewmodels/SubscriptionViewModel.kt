package com.uitopic.restockmobile.features.subscriptions.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.subscriptions.domain.repositories.SubscriptionRepository
import com.uitopic.restockmobile.features.subscriptions.presentation.states.SubscriptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var state by mutableStateOf(SubscriptionState())
        private set

    fun updateSubscription(subscriptionType: Int) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            val userId = tokenManager.getUserId()
            if (userId == -1) {
                state = state.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
                return@launch
            }

            repository.updateSubscription(userId, subscriptionType)
                .onSuccess {
                    state = state.copy(
                        isLoading = false,
                        success = true
                    )
                }
                .onFailure { error ->
                    state = state.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update subscription"
                    )
                }
        }
    }

    fun resetState() {
        state = SubscriptionState()
    }
}

