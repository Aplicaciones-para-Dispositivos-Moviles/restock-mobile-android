// features/home/presentation/HomeScreen.kt
package com.uitopic.restockmobile.features.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.home.presentation.components.QuickActionCard
import com.uitopic.restockmobile.features.home.presentation.components.RestockScaffold
import com.uitopic.restockmobile.features.home.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    userAvatar: String = "",
    onNavigateToProfile: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToSales: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val userName = viewModel.getUsername()
    val userEmail = viewModel.userEmail

    RestockScaffold(
        title = "Restock",
        userName = userName,
        userEmail = userEmail,
        userAvatar = userAvatar,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToRecipes = onNavigateToRecipes,
        onNavigateToSales = onNavigateToSales,
        onNavigateToHome = {},
        onLogout = onLogout,
        onNavigateToOrders = onNavigateToOrders
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            userName = userName,
            onNavigateToRecipes = onNavigateToRecipes,
            onNavigateToInventory = onNavigateToInventory,
            onNavigateToSales = onNavigateToSales,
            onNavigateToOrders = onNavigateToOrders
        )
    }
}


@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    userName: String,
    onNavigateToRecipes: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Quick Actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Inventory,
                title = "Inventory",
                description = "Track supplies",
                onClick = onNavigateToInventory,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                icon = Icons.Default.ShoppingCart,
                title = "Orders",
                description = "Make orders",
                onClick = onNavigateToOrders,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Restaurant,
                title = "Recipes",
                description = "Manage your recipes",
                onClick = onNavigateToRecipes,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                icon = Icons.Outlined.PointOfSale,
                title = "Sales",
                description = "Register sales",
                onClick = onNavigateToSales,
                modifier = Modifier.weight(1f)
            )

            //Spacer(modifier = Modifier.weight(1f))
        }

        // Statistics Card (Placeholder)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Business",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Start managing your recipes and inventory to see insights here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
