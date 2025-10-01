package com.uitopic.restockmobile.features.profiles.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uitopic.restockmobile.features.profiles.presentation.screens.*

sealed class ProfileRoute(val route: String) {
    data object ProfileDetail : ProfileRoute("profile_detail")
    data object EditPersonalData : ProfileRoute("edit_personal_data")
    data object EditBusinessData : ProfileRoute("edit_business_data")
    data object ChangePassword : ProfileRoute("change_password")
    data object DeleteAccount : ProfileRoute("delete_account")
}

fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    onAccountDeleted: () -> Unit
) {
    navigation(
        startDestination = ProfileRoute.ProfileDetail.route,
        route = "profile_graph"
    ) {
        composable(ProfileRoute.ProfileDetail.route) {
            ProfileDetailScreen(
                onNavigateToEditPersonal = {
                    navController.navigate(ProfileRoute.EditPersonalData.route)
                },
                onNavigateToEditBusiness = {
                    navController.navigate(ProfileRoute.EditBusinessData.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(ProfileRoute.ChangePassword.route)
                },
                onNavigateToDeleteAccount = {
                    navController.navigate(ProfileRoute.DeleteAccount.route)
                }
            )
        }

        composable(ProfileRoute.EditPersonalData.route) {
            EditPersonalDataScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(ProfileRoute.EditBusinessData.route) {
            EditBusinessDataScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(ProfileRoute.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(ProfileRoute.DeleteAccount.route) {
            DeleteAccountScreen(
                onNavigateBack = { navController.navigateUp() },
                onAccountDeleted = onAccountDeleted
            )
        }
    }
}