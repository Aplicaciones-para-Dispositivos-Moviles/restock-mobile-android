package com.uitopic.restockmobile.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestockScaffold(
    title: String,
    userName: String,
    userEmail: String,
    userAvatar: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
                userEmail = userEmail,
                userAvatar = userAvatar,
                onNavigateToHome = {
                    scope.launch { drawerState.close(); onNavigateToHome() }
                },
                onNavigateToInventory = {
                    scope.launch { drawerState.close(); onNavigateToInventory() }
                },
                onNavigateToRecipes = {
                    scope.launch { drawerState.close(); onNavigateToRecipes() }
                },
                onNavigateToSales = {
                    scope.launch { drawerState.close(); onNavigateToSales() }
                },
                onNavigateToProfile = {
                    scope.launch { drawerState.close(); onNavigateToProfile() }
                },
                onLogout = {
                    scope.launch { drawerState.close(); onLogout() }
                },
                onNavigateToOrders = {
                    scope.launch { drawerState.close(); onNavigateToOrders() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToProfile) {
                            if (userAvatar.isNotBlank()) {
                                AsyncImage(
                                    model = userAvatar,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.AccountCircle, "Profile")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            content = content
        )
    }
}


@Composable
fun DrawerContent(
    userName: String,
    userEmail: String,
    userAvatar: String,
    onNavigateToHome: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // User Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (userAvatar.isNotBlank()) {
                    AsyncImage(
                        model = userAvatar,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(.3f))

            // Navigation Items
            DrawerMenuItem(
                icon = Icons.Default.Home,
                label = "Home",
                onClick = onNavigateToHome
            )

            DrawerMenuItem(
                icon = Icons.Default.Inventory,
                label = "Inventory",
                onClick = onNavigateToInventory
            )

            DrawerMenuItem(
                icon = Icons.Default.ShoppingCart,
                label = "Orders",
                onClick = onNavigateToOrders
            )

            DrawerMenuItem(
                icon = Icons.Default.Restaurant,
                label = "Recipes",
                onClick = onNavigateToRecipes
            )

            DrawerMenuItem(
                icon = Icons.Outlined.PointOfSale,
                label = "Sales",
                onClick = onNavigateToSales
            )

            DrawerMenuItem(
                icon = Icons.Default.Person,
                label = "Profile",
                onClick = onNavigateToProfile
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(.3f))

            DrawerMenuItem(
                icon = Icons.Default.Logout,
                label = "Logout",
                onClick = onLogout,
                isDestructive = true
            )
        }
    }
}