package com.uitopic.restockmobile.features.home.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.profiles.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    var userEmail by mutableStateOf("user@example.com")
        private set

    init {
        loadUserEmail()
    }

    fun getUsername(): String {
        return tokenManager.getUsername() ?: "User"
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId().toString()
            profileRepository.getProfileById(userId)
                .onSuccess { profile ->
                    userEmail = profile.email
                }
                .onFailure {
                    userEmail = "user@example.com"
                }
        }
    }
}
