package com.uitopic.restockmobile

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.auth.presentation.navigation.authNavGraph
import com.uitopic.restockmobile.features.home.presentation.navigation.HomeRoute
import com.uitopic.restockmobile.features.home.presentation.navigation.homeNavGraph
import com.uitopic.restockmobile.features.planning.presentation.navigation.planningNavGraph
import com.uitopic.restockmobile.features.profiles.presentation.navigation.profileNavGraph
import com.uitopic.restockmobile.features.resources.presentation.navigation.inventoryNavGraph
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
                }
            }
        }
    }
}