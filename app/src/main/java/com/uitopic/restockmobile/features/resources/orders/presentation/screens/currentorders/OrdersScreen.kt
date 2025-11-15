package com.uitopic.restockmobile.features.resources.orders.presentation.screens.currentorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.home.presentation.components.RestockScaffold
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel
import com.uitopic.restockmobile.features.resources.orders.domain.models.Order
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderBatchItem
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderSituation
import com.uitopic.restockmobile.features.resources.orders.domain.models.OrderState
import com.uitopic.restockmobile.features.resources.orders.presentation.viewmodels.OrdersViewModel
import com.uitopic.restockmobile.shared.presentation.components.ItemSearchBar
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.uitopic.restockmobile.features.resources.orders.presentation.screens.ui.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),

    userName: String = "User",
    userEmail: String = "user@example.com",
    userAvatar: String = "",
    onNavigateToProfile: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSales: () -> Unit,
    onLogout: () -> Unit,

    onCreateOrder: () -> Unit,
    onOrderClick: (Int) -> Unit = {}
    )
{
    RestockScaffold(
        title = "Orders",
        userName = userName,
        userEmail = userEmail,

        userAvatar = userAvatar,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToRecipes = onNavigateToRecipes,
        onNavigateToSales = onNavigateToSales,
        onNavigateToHome = onNavigateToHome,
        onLogout = onLogout,
        onNavigateToOrders = {},
    ) { innerPadding ->
        OrdersContent(
            modifier = Modifier.padding(innerPadding),
            userName = userName,
            onCreateOrder = onCreateOrder,
            onOrderClick = onOrderClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContent(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel(),
    userName: String = "User",
    onCreateOrder: () -> Unit,
    onOrderClick: (Int) -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val allOrders by viewModel.orders.collectAsState()

    val displayOrders = if (searchQuery.isNotBlank()) {
        filteredOrders
    } else {
        allOrders
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateOrder,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Make an Order")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ItemSearchBar(
                subtitle = "orders",
                searchQuery = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )

            if (displayOrders.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No orders yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Create your first order",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(displayOrders.size) { index ->
                        val order = displayOrders[index]
                        OrderCard(
                            order = order,
                            onOrderClick = { onOrderClick(index) }
                        )
                        if (index < displayOrders.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OrdersPreview()
{
    RestockmobileTheme {
        OrdersScreen(
            onNavigateToProfile = {},
            onNavigateToInventory = {},
            onNavigateToRecipes = {},
            onNavigateToSales = {},
            onNavigateToHome = {},
            onLogout = {},
            onCreateOrder = {}
        )
    }
}