package com.uitopic.restockmobile.features.subscriptions.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uitopic.restockmobile.features.subscriptions.presentation.viewmodels.SubscriptionViewModel
import com.uitopic.restockmobile.ui.theme.RestockmobileTheme
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    planType: Int = 1,
    onPaymentSuccess: () -> Unit = {},
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("United States") }
    var zip by remember { mutableStateOf("") }

    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    val planName = if (planType == 1) "Anual Plan" else "Semester Plan"
    val planPrice = if (planType == 1) "S/. 39.99 / monthly" else "S/. 49.99 / monthly"

    // Observar cuando se complete la actualización exitosamente
    LaunchedEffect(state.success) {
        if (state.success) {
            onPaymentSuccess()
        }
    }

    // Mostrar errores
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(planName) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    Text(
                        text = planName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = planPrice,
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Card information") },
                trailingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("MM / YY") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = cvc,
                    onValueChange = { cvc = it },
                    label = { Text("CVC") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cardholderName,
                onValueChange = { cardholderName = it },
                label = { Text("Cardholder name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country or region") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = zip,
                onValueChange = { zip = it },
                label = { Text("ZIP") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Actualizar la suscripción en el backend
                    viewModel.updateSubscription(planType)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Pay", fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    RestockmobileTheme {
        PaymentScreen()
    }
}

