package com.uitopic.restockmobile.features.profiles.presentation.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.presentation.components.AvatarPicker
import com.uitopic.restockmobile.features.profiles.presentation.viewmodels.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    onNavigateToEditPersonal: () -> Unit,
    onNavigateToEditBusiness: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToDeleteAccount: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.profileState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(viewModel.uploadAvatarState.success) {
        if (viewModel.uploadAvatarState.success) {
            snackbarHostState.showSnackbar("Avatar updated successfully")
            viewModel.resetUploadState()
        }
    }

    LaunchedEffect(viewModel.uploadAvatarState.error) {
        viewModel.uploadAvatarState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    ErrorMessage(
                        message = state.error,
                        onRetry = { viewModel.loadProfile() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.profile != null -> {
                    ProfileContent(
                        profile = state.profile,
                        isUploadingAvatar = viewModel.uploadAvatarState.isUploading,  // ← AGREGAR
                        onAvatarSelected = { uri -> viewModel.uploadAvatar(uri) },    // ← AGREGAR
                        onEditPersonal = onNavigateToEditPersonal,
                        onEditBusiness = onNavigateToEditBusiness,
                        onChangePassword = onNavigateToChangePassword,
                        onDeleteAccount = onNavigateToDeleteAccount
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ProfileContent(
    profile: Profile,
    isUploadingAvatar: Boolean,
    onAvatarSelected: (Uri) -> Unit,
    onEditPersonal: () -> Unit,
    onEditBusiness: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        AvatarPicker(
            avatarUrl = profile.avatar,
            initials = "${profile.firstName.firstOrNull() ?: ""}${profile.lastName.firstOrNull() ?: ""}",
            isUploading = isUploadingAvatar,
            onImageSelected = onAvatarSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "${profile.firstName} ${profile.lastName}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Info Card
        ProfileInfoCard(
            title = "Personal Information",
            icon = Icons.Default.Person,
            onClick = onEditPersonal
        ) {
            ProfileInfoItem(Icons.Default.Email, "Email", profile.email)
            ProfileInfoItem(Icons.Default.Phone, "Phone", profile.phone)
            ProfileInfoItem(Icons.Default.LocationOn, "Address", profile.address)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Business Info Card
        ProfileInfoCard(
            title = "Business Information",
            icon = Icons.Default.Business,
            onClick = onEditBusiness
        ) {
            ProfileInfoItem(Icons.Default.Store, "Company", profile.businessName)
            ProfileInfoItem(Icons.Default.LocationCity, "Address", profile.businessAddress)

            if (profile.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profile.categories.forEach { category ->
                        AssistChip(
                            onClick = { },
                            label = { Text(category.name) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Section
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onChangePassword
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Change Password",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = onDeleteAccount,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Account")
        }
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}