package com.uitopic.restockmobile.features.profiles.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.profiles.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonalDataScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.editPersonalDataState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.success) {
        if (state.success) {
            snackbarHostState.showSnackbar("Personal data updated successfully")
            viewModel.resetPersonalDataSuccess()
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
                title = { Text("Edit your information") },
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
            Text(
                text = "Personal data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.firstName,
                onValueChange = viewModel::onFirstNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.firstNameError != null,
                supportingText = state.firstNameError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.lastName,
                onValueChange = viewModel::onLastNameChange,
                label = { Text("Last names") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.lastNameError != null,
                supportingText = state.lastNameError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.phoneError != null,
                supportingText = state.phoneError?.let { { Text(it) } },
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.addressError != null,
                supportingText = state.addressError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = state.country,
                onValueChange = viewModel::onCountryChange,
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.countryError != null,
                supportingText = state.countryError?.let { { Text(it) } },
                enabled = !state.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.updatePersonalData() },
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