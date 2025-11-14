package com.uitopic.restockmobile.features.subscriptions.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uitopic.restockmobile.features.subscriptions.domain.models.SubscriptionPlan
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlansScreen(onSubscribeClick: (planType: Int) -> Unit = {}) {
    val plans = listOf(
        SubscriptionPlan(
            name = "Anual Plan",
            price = "S/. 39.99 / monthly",
            features = listOf(
                "Automated inventory management",
                "Order and purchase control",
                "Reporting and analytics",
                "Critical stock notifications",
                "Integration with suppliers"
            ),
            popular = true
        ),
        SubscriptionPlan(
            name = "Semester Plan",
            price = "S/. 49.99 / monthly",
            features = listOf(
                "Automated inventory management",
                "Order and purchase control",
                "Reporting and analytics",
                "Critical stock notifications",
                "Integration with suppliers"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions") },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(plans) { index, plan ->
                // index 0 = Anual (planType 1), index 1 = Semester (planType 2)
                val planType = index + 1
                SubscriptionPlanItem(
                    plan = plan,
                    onSubscribeClick = { onSubscribeClick(planType) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlanItem(
    plan: SubscriptionPlan,
    onSubscribeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Text(
                    text = plan.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = plan.price,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                plan.features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feature, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSubscribeClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = "SUBSCRIBE")
                }
            }
            if (plan.popular) {
                Text(
                    text = "POPULAR",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF4CAF50), shape = RoundedCornerShape(bottomStart = 12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionPlansScreenPreview() {
    RestockmobileTheme {
        SubscriptionPlansScreen()
    }
}

