package com.uitopic.restockmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.auth.presentation.navigation.authNavGraph
import com.uitopic.restockmobile.features.home.presentation.navigation.HomeRoute
import com.uitopic.restockmobile.features.home.presentation.navigation.homeNavGraph
import com.uitopic.restockmobile.features.planning.presentation.navigation.planningNavGraph
import com.uitopic.restockmobile.features.profiles.presentation.navigation.profileNavGraph
import com.uitopic.restockmobile.features.resources.inventory.presentation.navigation.inventoryNavGraph
import com.uitopic.restockmobile.features.monitoring.presentation.navigation.monitoringNavGraph
import com.uitopic.restockmobile.features.resources.orders.presentation.navigation.ordersNavGraph
import com.uitopic.restockmobile.features.subscriptions.presentation.navigation.SubscriptionRoute
import com.uitopic.restockmobile.features.subscriptions.presentation.navigation.subscriptionNavGraph
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestockmobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestockApp(tokenManager)
                }
            }
        }
    }
}

@Composable
fun RestockApp(tokenManager: TokenManager) {
    val navController = rememberNavController()
    // ==========================================
    // CONFIGURACIÓN DE INICIO DE LA APP
    // ==========================================
    val startDestination = if (tokenManager.isLoggedIn()) {
        // Si el usuario ya está logueado, verificar su suscripción
        val subscription = tokenManager.getSubscription()
        if (subscription == 0) {
            // Sin suscripción -> ir a selección de planes
            SubscriptionRoute.SubscriptionGraph
        } else {
            // Con suscripción (1 o 2) -> ir directamente a Home
            HomeRoute.Home.route
        }
    } else {
        "auth_graph"
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Graph (Sign In, Sign Up)
        authNavGraph(
            navController = navController,
            onAuthSuccess = { subscription ->
                // Redirigir según el valor de subscription
                if (subscription == 0) {
                    // Sin suscripción -> ir a selección de planes
                    navController.navigate(SubscriptionRoute.SubscriptionGraph) {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                } else {
                    // Con suscripción -> ir directamente a Home
                    navController.navigate(HomeRoute.Home.route) {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            }
        )

        // Subscription Graph
        subscriptionNavGraph(
            navController = navController,
            onSubscriptionComplete = {
                navController.navigate(HomeRoute.Home.route) {
                    popUpTo(SubscriptionRoute.SubscriptionGraph) { inclusive = true }
                }
            }
        )

        // Home Screen
        homeNavGraph(navController)
        // Monitoring Graph (Sales)
        monitoringNavGraph(navController)
        // Profile Graph (Profile Details, Edit, etc.)
        profileNavGraph(
            navController = navController,
            onAccountDeleted = {
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        // Inventory
        inventoryNavGraph(navController)
        // Planning (Recipes)
        planningNavGraph(navController)

        // Resources - Orders
        ordersNavGraph(navController, 1)
    }
}
