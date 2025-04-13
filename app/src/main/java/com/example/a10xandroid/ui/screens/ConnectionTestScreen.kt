package com.example.a10xandroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.a10xandroid.ui.components.ConnectionStatus
import com.example.a10xandroid.ui.viewmodels.ConnectionTestViewModel

@Composable
fun ConnectionTestScreen(
    viewModel: ConnectionTestViewModel = hiltViewModel()
) {
    var isConnected by remember { mutableStateOf<Boolean?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ConnectionStatus(
            isConnected = isConnected,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                isLoading = true
                viewModel.checkConnection { success ->
                    isConnected = success
                    isLoading = false
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Test Connection")
        }
    }
} 