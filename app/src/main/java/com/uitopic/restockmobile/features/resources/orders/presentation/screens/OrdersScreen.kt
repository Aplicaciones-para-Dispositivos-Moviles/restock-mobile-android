package com.uitopic.restockmobile.features.resources.orders.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel(),

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
            onCreateOrder = onCreateOrder
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContent(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel(),

    userName: String = "User",
    userEmail: String = "user@example.com",
    userAvatar: String = "",

    onCreateOrder: () -> Unit,
)
{
    val orders by viewModel.orders.collectAsState()

    val sampleOrders = listOf(
        Order(
            adminRestaurantId = 1,
            supplierId = 100,
            supplier = User(
                id = 100,
                username = "Proveedor Lima",
                roleId = 2,
                profile = Profile(
                    id = 1,
                    firstName = "Carlos",
                    lastName = "Mendoza",
                    email = "contacto@proveedorlima.com",
                    phone = "+51 987 654 321",
                    address = "Av. Industrial 123, Lima",
                    country = "Peru",
                    avatar = null,
                    businessName = "Proveedor Lima",
                    businessAddress = "Av. Industrial 123, Cercado de Lima",
                    description = "Distribuidor mayorista de alimentos",
                    categories = emptyList()
                )
            ),
            requestedDate = "2025-11-02",
            partiallyAccepted = false,
            requestedProductsCount = 2,
            totalPrice = 150.50,
            state = OrderState.PREPARING,
            situation = OrderSituation.PENDING,
            batchItems = listOf(
                OrderBatchItem(
                    batchId = 1,
                    quantity = 10.0,
                    accepted = true,
                    batch = Batch(
                        id = "B001",
                        userId = 1,
                        stock = 20.0,
                        expirationDate = "2025-12-30",
                        customSupply = CustomSupply(
                            id = 1,
                            minStock = 5,
                            maxStock = 50,
                            price = 15.50,
                            userId = 1,
                            supplyId = 11,
                            currencyCode = "USD",
                            description = "Aceite vegetal de 1L",
                            unit = UnitModel(name = "Litro", abbreviation = "L"),
                            supply = Supply(
                                id = 11,
                                name = "Aceite vegetal",
                                description = "Aceite refinado para cocina",
                                perishable = false,
                                category = "Alimentos"
                            )
                        )
                    )
                ),
                OrderBatchItem(
                    batchId = 2,
                    quantity = 5.0,
                    accepted = false,
                    batch = Batch(
                        id = "B002",
                        userId = 1,
                        stock = 10.0,
                        expirationDate = "2025-12-15",
                        customSupply = CustomSupply(
                            id = 2,
                            minStock = 2,
                            maxStock = 20,
                            price = 30.0,
                            userId = 1,
                            supplyId = 12,
                            currencyCode = "USD",
                            description = "Harina de trigo premium 5kg",
                            unit = UnitModel(name = "Kilogramo", abbreviation = "kg"),
                            supply = Supply(
                                id = 12,
                                name = "Harina de trigo",
                                description = "Harina blanca para panadería",
                                perishable = false,
                                category = "Alimentos"
                            )
                        )
                    )
                )
            )
        ),
        Order(
            adminRestaurantId = 1,
            supplierId = 101,
            supplier = User(
                id = 101,
                username = "Proveedor Cusco",
                roleId = 2,
                profile = Profile(
                    id = 1,
                    firstName = "Carlos",
                    lastName = "Mendoza",
                    email = "contacto@proveedorlima.com",
                    phone = "+51 987 654 321",
                    address = "Av. Industrial 123, Lima",
                    country = "Peru",
                    avatar = null,
                    businessName = "Proveedor Lima",
                    businessAddress = "Av. Industrial 123, Cercado de Lima",
                    description = "Distribuidor mayorista de alimentos",
                    categories = emptyList()
                )
            ),
            requestedDate = "2025-10-20",
            partiallyAccepted = true,
            requestedProductsCount = 1,
            totalPrice = 80.0,
            state = OrderState.DELIVERED,
            situation = OrderSituation.APPROVED,
            batchItems = emptyList()
        )
    )

    LazyColumn {
        items(sampleOrders) { order ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = order.supplier?.profile?.businessName ?: "Proveedor desconocido",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.requestedDate,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "S/ ${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider()
        }
    }

    //=========================================

    val searchQuery by viewModel.searchQuery.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
                actions = {
                    /* RecipeSortButton(
                        isSortedByPriceDesc = sortByPrice,
                        onToggleSort = { viewModel.toggleSortByPrice() }
                    ) */
                }
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