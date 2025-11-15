package com.uitopic.restockmobile.features.profiles.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.core.cloudinary.repositories.ImageUploadRepository
import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.domain.models.ChangePasswordRequest
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.models.UpdateBusinessDataRequest
import com.uitopic.restockmobile.features.profiles.domain.models.UpdatePersonalDataRequest
import com.uitopic.restockmobile.features.profiles.domain.repositories.ProfileRepository
import com.uitopic.restockmobile.features.profiles.presentation.states.ChangePasswordUiState
import com.uitopic.restockmobile.features.profiles.presentation.states.DeleteAccountUiState
import com.uitopic.restockmobile.features.profiles.presentation.states.EditBusinessDataUiState
import com.uitopic.restockmobile.features.profiles.presentation.states.EditPersonalDataUiState
import com.uitopic.restockmobile.features.profiles.presentation.states.ProfileUiState
import com.uitopic.restockmobile.features.profiles.presentation.states.UploadAvatarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val imageUploadRepository: ImageUploadRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var profileState by mutableStateOf(ProfileUiState())
        private set

    var editPersonalDataState by mutableStateOf(EditPersonalDataUiState())
        private set

    var editBusinessDataState by mutableStateOf(EditBusinessDataUiState())
        private set

    var changePasswordState by mutableStateOf(ChangePasswordUiState())
        private set

    var deleteAccountState by mutableStateOf(DeleteAccountUiState())
        private set

    var uploadAvatarState by mutableStateOf(UploadAvatarUiState())
        private set

    private fun saveAvatarToBackend(avatarUrl: String) {
        profileState.profile?.let { profile ->
            viewModelScope.launch {
                val request = UpdatePersonalDataRequest(
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = profile.email,
                    phone = profile.phone,
                    address = profile.address,
                    country = profile.country,
                    avatar = avatarUrl  // ← Nueva URL de Cloudinary
                )

                val userId = tokenManager.getUserId().toString()
                repository.updatePersonalData(userId, request)
                    .onFailure { error ->
                        // Si falla guardar en backend, mostrar error pero mantener la imagen
                        uploadAvatarState = uploadAvatarState.copy(
                            error = "Avatar uploaded but failed to save: ${error.message}"
                        )
                    }
            }
        }
    }

    fun uploadAvatar(imageUri: Uri) {
        viewModelScope.launch {
            uploadAvatarState = uploadAvatarState.copy(
                isUploading = true,
                error = null,
                success = false
            )

            imageUploadRepository.uploadImage(imageUri)
                .onSuccess { imageUrl ->
                    uploadAvatarState = uploadAvatarState.copy(
                        isUploading = false,
                        uploadedUrl = imageUrl,
                        success = true
                    )

                    profileState.profile?.let { currentProfile ->
                        profileState = profileState.copy(
                            profile = currentProfile.copy(avatar = imageUrl)
                        )
                    }

                    saveAvatarToBackend(imageUrl)
                }
                .onFailure { error ->
                    uploadAvatarState = uploadAvatarState.copy(
                        isUploading = false,
                        error = error.message ?: "Failed to upload image"
                    )
                }
        }
    }

    fun onPhoneChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            phone = value,
            phoneError = null
        )
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun resetUploadState() {
        uploadAvatarState = UploadAvatarUiState()
    }

    // PROFILE OPERATIONS
    fun loadProfile() {
        viewModelScope.launch {
            profileState = profileState.copy(isLoading = true, error = null)

            val userId = tokenManager.getUserId().toString()
            repository.getProfileById(userId)
                .onSuccess { profile ->
                    profileState = profileState.copy(
                        profile = profile,
                        isLoading = false
                    )
                    loadPersonalDataForEdit(profile)
                    loadBusinessDataForEdit(profile)
                }
                .onFailure { error ->
                    profileState = profileState.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }

    private fun loadPersonalDataForEdit(profile: Profile) {
        editPersonalDataState = editPersonalDataState.copy(
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            phone = profile.phone,
            address = profile.address,
            country = profile.country,
            avatar = profile.avatar
        )
    }

    private fun loadBusinessDataForEdit(profile: Profile) {
        editBusinessDataState = editBusinessDataState.copy(
            businessName = profile.businessName,
            businessAddress = profile.businessAddress,
            description = profile.description ?: "",
            selectedCategories = profile.categories
        )
    }

    fun onDescriptionChange(value: String) {
        editBusinessDataState = editBusinessDataState.copy(
            description = value
        )
    }

    // EDIT PERSONAL DATA
    fun onFirstNameChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            firstName = value,
            firstNameError = null
        )
    }

    fun onLastNameChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            lastName = value,
            lastNameError = null
        )
    }

    fun onEmailChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            email = value,
            emailError = null
        )
    }

    fun onAddressChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            address = value,
            addressError = null
        )
    }

    fun onCountryChange(value: String) {
        editPersonalDataState = editPersonalDataState.copy(
            country = value,
            countryError = null
        )
    }

    fun updatePersonalData() {
        if (!validatePersonalData()) return

        viewModelScope.launch {
            editPersonalDataState = editPersonalDataState.copy(
                isLoading = true,
                error = null,
                success = false
            )

            val request = UpdatePersonalDataRequest(
                firstName = editPersonalDataState.firstName.trim(),
                lastName = editPersonalDataState.lastName.trim(),
                email = editPersonalDataState.email.trim(),
                phone = editPersonalDataState.phone.trim(),
                address = editPersonalDataState.address.trim(),
                country = editPersonalDataState.country.trim(),
                avatar = profileState.profile?.avatar
            )

            val userId = tokenManager.getUserId().toString()
            repository.updatePersonalData(userId, request)
                .onSuccess { updatedProfile ->
                    profileState = profileState.copy(profile = updatedProfile)
                    editPersonalDataState = editPersonalDataState.copy(
                        isLoading = false,
                        success = true
                    )
                }
                .onFailure { error ->
                    editPersonalDataState = editPersonalDataState.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update personal data"
                    )
                }
        }
    }

    private fun validatePersonalData(): Boolean {
        var isValid = true

        if (editPersonalDataState.firstName.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                firstNameError = "First name is required"
            )
            isValid = false
        }

        if (editPersonalDataState.lastName.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                lastNameError = "Last name is required"
            )
            isValid = false
        }

        if (editPersonalDataState.email.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                emailError = "Email is required"
            )
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editPersonalDataState.email).matches()) {
            editPersonalDataState = editPersonalDataState.copy(
                emailError = "Invalid email format"
            )
            isValid = false
        }

        if (editPersonalDataState.phone.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                phoneError = "Phone is required"
            )
            isValid = false
        }

        if (editPersonalDataState.address.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                addressError = "Address is required"
            )
            isValid = false
        }

        if (editPersonalDataState.country.isBlank()) {
            editPersonalDataState = editPersonalDataState.copy(
                countryError = "Country is required"
            )
            isValid = false
        }

        return isValid
    }

    // EDIT BUSINESS DATA
    fun loadAllCategories() {
        viewModelScope.launch {
            editBusinessDataState = editBusinessDataState.copy(isLoadingCategories = true)

            repository.getAllBusinessCategories()
                .onSuccess { categories ->
                    editBusinessDataState = editBusinessDataState.copy(
                        availableCategories = categories,
                        isLoadingCategories = false
                    )
                }
                .onFailure { error ->
                    editBusinessDataState = editBusinessDataState.copy(
                        isLoadingCategories = false,
                        error = error.message
                    )
                }
        }
    }

    fun onBusinessNameChange(value: String) {
        editBusinessDataState = editBusinessDataState.copy(
            businessName = value,
            businessNameError = null
        )
    }

    fun onBusinessAddressChange(value: String) {
        editBusinessDataState = editBusinessDataState.copy(
            businessAddress = value,
            businessAddressError = null
        )
    }

    fun toggleCategory(category: BusinessCategory) {
        val currentCategories = editBusinessDataState.selectedCategories
        val newCategories = if (currentCategories.contains(category)) {
            currentCategories - category
        } else {
            currentCategories + category
        }
        editBusinessDataState = editBusinessDataState.copy(
            selectedCategories = newCategories,
            categoriesError = null
        )
    }

    fun updateBusinessData() {
        if (!validateBusinessData()) return

        viewModelScope.launch {
            editBusinessDataState = editBusinessDataState.copy(
                isLoading = true,
                error = null,
                success = false
            )

            val request = UpdateBusinessDataRequest(
                businessName = editBusinessDataState.businessName.trim(),
                businessAddress = editBusinessDataState.businessAddress.trim(),
                description = editBusinessDataState.description.trim(),  // ← AGREGADO
                categoryIds = editBusinessDataState.selectedCategories.map { it.id }
            )

            val userId = tokenManager.getUserId().toString()
            repository.updateBusinessData(userId, request)
                .onSuccess { updatedProfile ->
                    profileState = profileState.copy(profile = updatedProfile)
                    editBusinessDataState = editBusinessDataState.copy(
                        isLoading = false,
                        success = true
                    )
                }
                .onFailure { error ->
                    editBusinessDataState = editBusinessDataState.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update business data"
                    )
                }
        }
    }


    private fun validateBusinessData(): Boolean {
        var isValid = true

        if (editBusinessDataState.businessName.isBlank()) {
            editBusinessDataState = editBusinessDataState.copy(
                businessNameError = "Company name is required"
            )
            isValid = false
        }

        if (editBusinessDataState.businessAddress.isBlank()) {
            editBusinessDataState = editBusinessDataState.copy(
                businessAddressError = "Company address is required"
            )
            isValid = false
        }

        if (editBusinessDataState.selectedCategories.isEmpty()) {
            editBusinessDataState = editBusinessDataState.copy(
                categoriesError = "At least one category is required"
            )
            isValid = false
        }

        return isValid
    }

    // CHANGE PASSWORD
    fun onCurrentPasswordChange(value: String) {
        changePasswordState = changePasswordState.copy(
            currentPassword = value,
            currentPasswordError = null
        )
    }

    fun onNewPasswordChange(value: String) {
        changePasswordState = changePasswordState.copy(
            newPassword = value,
            newPasswordError = null
        )
    }

    fun onConfirmPasswordChange(value: String) {
        changePasswordState = changePasswordState.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun toggleCurrentPasswordVisibility() {
        changePasswordState = changePasswordState.copy(
            showCurrentPassword = !changePasswordState.showCurrentPassword
        )
    }

    fun toggleNewPasswordVisibility() {
        changePasswordState = changePasswordState.copy(
            showNewPassword = !changePasswordState.showNewPassword
        )
    }

    fun toggleConfirmPasswordVisibility() {
        changePasswordState = changePasswordState.copy(
            showConfirmPassword = !changePasswordState.showConfirmPassword
        )
    }

    fun changePassword() {
        if (!validatePassword()) return

        viewModelScope.launch {
            changePasswordState = changePasswordState.copy(
                isLoading = true,
                error = null,
                success = false
            )

            val request = ChangePasswordRequest(
                currentPassword = changePasswordState.currentPassword,
                newPassword = changePasswordState.newPassword,
                confirmPassword = changePasswordState.confirmPassword
            )

            repository.changePassword(tokenManager.getUserId().toString(), request)
                .onSuccess {
                    changePasswordState = ChangePasswordUiState(success = true)
                }
                .onFailure { error ->
                    changePasswordState = changePasswordState.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to change password"
                    )
                }
        }
    }

    private fun validatePassword(): Boolean {
        var isValid = true

        if (changePasswordState.currentPassword.isBlank()) {
            changePasswordState = changePasswordState.copy(
                currentPasswordError = "Current password is required"
            )
            isValid = false
        }

        if (changePasswordState.newPassword.isBlank()) {
            changePasswordState = changePasswordState.copy(
                newPasswordError = "New password is required"
            )
            isValid = false
        } else if (changePasswordState.newPassword.length < 6) {
            changePasswordState = changePasswordState.copy(
                newPasswordError = "Password must be at least 6 characters"
            )
            isValid = false
        }

        if (changePasswordState.confirmPassword.isBlank()) {
            changePasswordState = changePasswordState.copy(
                confirmPasswordError = "Please confirm your password"
            )
            isValid = false
        } else if (changePasswordState.newPassword != changePasswordState.confirmPassword) {
            changePasswordState = changePasswordState.copy(
                confirmPasswordError = "Passwords do not match"
            )
            isValid = false
        }

        return isValid
    }

    // DELETE ACCOUNT
    fun deleteAccount() {
        viewModelScope.launch {
            deleteAccountState = deleteAccountState.copy(
                isLoading = true,
                error = null,
                success = false
            )

            repository.deleteProfile(tokenManager.getUserId().toString())
                .onSuccess {
                    deleteAccountState = deleteAccountState.copy(
                        isLoading = false,
                        success = true
                    )
                }
                .onFailure { error ->
                    deleteAccountState = deleteAccountState.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete account"
                    )
                }
        }
    }

    // RESET STATES
    fun resetPersonalDataSuccess() {
        editPersonalDataState = editPersonalDataState.copy(success = false)
    }

    fun resetBusinessDataSuccess() {
        editBusinessDataState = editBusinessDataState.copy(success = false)
    }

    fun resetPasswordSuccess() {
        changePasswordState = changePasswordState.copy(success = false)
    }

    fun resetDeleteAccountSuccess() {
        deleteAccountState = deleteAccountState.copy(success = false)
    }

    // GET USERNAME FROM TOKEN MANAGER
    fun getUsername(): String {
        return tokenManager.getUsername() ?: ""
    }
}