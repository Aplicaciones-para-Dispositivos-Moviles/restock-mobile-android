package com.uitopic.restockmobile.features.profiles.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.profiles.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.changePasswordState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar("Password changed successfully")
            viewModel.resetPasswordSuccess()
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.currentPassword,
                onValueChange = viewModel::onCurrentPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.showCurrentPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleCurrentPasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.showCurrentPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (state.showCurrentPassword) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                },
                isError = state.currentPasswordError != null,
                supportingText = state.currentPasswordError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.showNewPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleNewPasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.showNewPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (state.showNewPassword) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                },
                isError = state.newPasswordError != null,
                supportingText = state.newPasswordError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Confirm new Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.showConfirmPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.showConfirmPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (state.showConfirmPassword) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                },
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.changePassword() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("SAVE CHANGES")
                }
            }
        }
    }
}