package com.uitopic.restockmobile.features.subscriptions.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uitopic.restockmobile.features.subscriptions.presentation.screens.PaymentScreen
import com.uitopic.restockmobile.features.subscriptions.presentation.screens.SubscriptionPlansScreen

object SubscriptionRoute {
    const val SubscriptionGraph = "subscription_graph"
    const val Plans = "subscription_plans"
    const val Payment = "subscription_payment"
}

fun NavGraphBuilder.subscriptionNavGraph(
    navController: NavHostController,
    onSubscriptionComplete: () -> Unit
) {
    navigation(
        startDestination = SubscriptionRoute.Plans,
        route = SubscriptionRoute.SubscriptionGraph
    ) {
        composable(SubscriptionRoute.Plans) {
            SubscriptionPlansScreen(
                onSubscribeClick = { planType ->
                    navController.navigate("${SubscriptionRoute.Payment}/$planType")
                }
            )
        }
        composable("${SubscriptionRoute.Payment}/{planType}") { backStackEntry ->
            val planType = backStackEntry.arguments?.getString("planType")?.toIntOrNull() ?: 1
            PaymentScreen(
                planType = planType,
                onPaymentSuccess = onSubscriptionComplete
            )
        }
    }
}
