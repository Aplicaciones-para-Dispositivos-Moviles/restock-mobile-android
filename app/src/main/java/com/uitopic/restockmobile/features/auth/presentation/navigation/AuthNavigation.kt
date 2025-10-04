package com.uitopic.restockmobile.features.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uitopic.restockmobile.features.auth.presentation.screens.SignInScreen
import com.uitopic.restockmobile.features.auth.presentation.screens.SignUpScreen

sealed class AuthRoute(val route: String) {
    data object SignIn : AuthRoute("sign_in")
    data object SignUp : AuthRoute("sign_up")
}

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onAuthSuccess: () -> Unit
) {
    navigation(
        startDestination = AuthRoute.SignIn.route,
        route = "auth_graph"
    ) {
        composable(AuthRoute.SignIn.route) {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(AuthRoute.SignUp.route)
                },
                onSignInSuccess = onAuthSuccess
            )
        }

        composable(AuthRoute.SignUp.route) {
            SignUpScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSignUpSuccess = onAuthSuccess
            )
        }
    }
}