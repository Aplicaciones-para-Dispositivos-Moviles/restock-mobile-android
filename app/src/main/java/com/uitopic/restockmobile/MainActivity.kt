package com.uitopic.restockmobile

<<<<<<< HEAD
import android.net.Uri
=======
>>>>>>> feature/monitoring
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
<<<<<<< HEAD
import androidx.compose.ui.Modifier
=======
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
>>>>>>> feature/monitoring
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.auth.presentation.navigation.authNavGraph
import com.uitopic.restockmobile.features.home.presentation.navigation.HomeRoute
import com.uitopic.restockmobile.features.home.presentation.navigation.homeNavGraph
<<<<<<< HEAD
import com.uitopic.restockmobile.features.planning.presentation.navigation.planningNavGraph
import com.uitopic.restockmobile.features.profiles.presentation.navigation.profileNavGraph
import com.uitopic.restockmobile.features.resources.presentation.navigation.inventoryNavGraph
=======
import com.uitopic.restockmobile.features.monitoring.presentation.navigation.MonitoringRoute
import com.uitopic.restockmobile.features.monitoring.presentation.navigation.monitoringNavGraph
import com.uitopic.restockmobile.features.profiles.presentation.navigation.profileNavGraph
>>>>>>> feature/monitoring
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
<<<<<<< HEAD
                    val navController = rememberNavController()

                    val startDestination = HomeRoute.Home.route // Ajusta según auth si aplica

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // Auth
                        authNavGraph(
                            navController = navController,
                            onAuthSuccess = {
                                navController.navigate(HomeRoute.Home.route) {
                                    popUpTo("auth_graph") { inclusive = true }
                                }
                            }
                        )

                        // Home
                        homeNavGraph(navController)

                        // Profile
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
                        planningNavGraph(
                            navController = navController,
                            onUploadImage = { uri: Uri ->
                                // TODO: implementar subida real y devolver URL
                                uri.toString()
                            }
                        )
                    }
=======
                    RestockApp(tokenManager)
>>>>>>> feature/monitoring
                }
            }
        }
    }
<<<<<<< HEAD
}
=======
}

@Composable
fun RestockApp(tokenManager: TokenManager) {
    val navController = rememberNavController()

    // ==========================================
    // CONFIGURACIÓN DE INICIO DE LA APP
    // ==========================================
    // Lógica de autenticación activada
    // Si el usuario tiene token válido, va a Home
    // Si no tiene token, va al Login
    // ==========================================

    // App original sin bypass
    val startDestination = if (tokenManager.isLoggedIn()) {
        HomeRoute.Home.route
    } else {
        "auth_graph"
    }

    // ==========================================
    // BYPASS DESACTIVADOS (para desarrollo)
    // ==========================================

    // Bypass directo a Home (sin login) - DESACTIVADO
    // val FORCE_HOME_START = true
    // val startDestination = when {
    //     FORCE_HOME_START -> HomeRoute.Home.route
    //     tokenManager.isLoggedIn() -> HomeRoute.Home.route
    //     else -> "auth_graph"
    // }

    // Bypass directo a Monitoring/Sales - DESACTIVADO
    //val FORCE_MONITORING_START = true
    //val startDestination = when {
    //     FORCE_MONITORING_START -> MonitoringRoute.Sales.route
    //     tokenManager.isLoggedIn() -> HomeRoute.Home.route
    //     else -> "auth_graph"
    //}


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Graph (Sign In, Sign Up)
        authNavGraph(
            navController = navController,
            onAuthSuccess = {
                navController.navigate(HomeRoute.Home.route) {
                    popUpTo("auth_graph") { inclusive = true }
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
                // Logout y volver a login
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
>>>>>>> feature/monitoring
