package com.uitopic.restockmobile.features.auth.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.features.auth.domain.models.SignInRequest
import com.uitopic.restockmobile.features.auth.domain.models.SignUpRequest
import com.uitopic.restockmobile.features.auth.domain.repositories.AuthRepository
import com.uitopic.restockmobile.features.auth.presentation.states.SignInUiState
import com.uitopic.restockmobile.features.auth.presentation.states.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var signInState by mutableStateOf(SignInUiState())
        private set

    var signUpState by mutableStateOf(SignUpUiState())
        private set

    // SIGN IN
    fun onSignInUsernameChange(value: String) {
        signInState = signInState.copy(
            username = value,
            usernameError = null
        )
    }

    fun onSignInPasswordChange(value: String) {
        signInState = signInState.copy(
            password = value,
            passwordError = null
        )
    }

    fun toggleSignInPasswordVisibility() {
        signInState = signInState.copy(
            showPassword = !signInState.showPassword
        )
    }

    fun signIn() {
        if (!validateSignIn()) return

        viewModelScope.launch {
            signInState = signInState.copy(
                isLoading = true,
                error = null
            )

            val request = SignInRequest(
                username = signInState.username.trim(),
                password = signInState.password
            )

            repository.signIn(request)
                .onSuccess { user ->
                    signInState = signInState.copy(
                        isLoading = false,
                        success = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    signInState = signInState.copy(
                        isLoading = false,
                        error = error.message ?: "Sign in failed"
                    )
                }
        }
    }

    private fun validateSignIn(): Boolean {
        var isValid = true

        if (signInState.username.isBlank()) {
            signInState = signInState.copy(
                usernameError = "Username is required"
            )
            isValid = false
        }

        if (signInState.password.isBlank()) {
            signInState = signInState.copy(
                passwordError = "Password is required"
            )
            isValid = false
        }

        return isValid
    }

    // SIGN UP
    fun onSignUpUsernameChange(value: String) {
        signUpState = signUpState.copy(
            username = value,
            usernameError = null
        )
    }

    fun onSignUpPasswordChange(value: String) {
        signUpState = signUpState.copy(
            password = value,
            passwordError = null
        )
    }

    fun onSignUpConfirmPasswordChange(value: String) {
        signUpState = signUpState.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun toggleSignUpPasswordVisibility() {
        signUpState = signUpState.copy(
            showPassword = !signUpState.showPassword
        )
    }

    fun toggleSignUpConfirmPasswordVisibility() {
        signUpState = signUpState.copy(
            showConfirmPassword = !signUpState.showConfirmPassword
        )
    }

    fun signUp() {
        if (!validateSignUp()) return

        viewModelScope.launch {
            signUpState = signUpState.copy(
                isLoading = true,
                error = null
            )

            val request = SignUpRequest(
                username = signUpState.username.trim(),
                password = signUpState.password,
                roleId = 1  // Restaurant admin
            )

            repository.signUp(request)
                .onSuccess {
                    signUpState = signUpState.copy(
                        isLoading = false,
                        success = true
                    )
                }
                .onFailure { error ->
                    signUpState = signUpState.copy(
                        isLoading = false,
                        error = error.message ?: "Sign up failed"
                    )
                }
        }
    }

    private fun validateSignUp(): Boolean {
        var isValid = true

        if (signUpState.username.isBlank()) {
            signUpState = signUpState.copy(
                usernameError = "Username is required"
            )
            isValid = false
        } else if (signUpState.username.length < 3) {
            signUpState = signUpState.copy(
                usernameError = "Username must be at least 3 characters"
            )
            isValid = false
        }

        if (signUpState.password.isBlank()) {
            signUpState = signUpState.copy(
                passwordError = "Password is required"
            )
            isValid = false
        } else if (signUpState.password.length < 6) {
            signUpState = signUpState.copy(
                passwordError = "Password must be at least 6 characters"
            )
            isValid = false
        }

        if (signUpState.confirmPassword.isBlank()) {
            signUpState = signUpState.copy(
                confirmPasswordError = "Please confirm your password"
            )
            isValid = false
        } else if (signUpState.password != signUpState.confirmPassword) {
            signUpState = signUpState.copy(
                confirmPasswordError = "Passwords do not match"
            )
            isValid = false
        }

        return isValid
    }

    fun resetSignInState() {
        signInState = SignInUiState()
    }

    fun resetSignUpState() {
        signUpState = SignUpUiState()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}