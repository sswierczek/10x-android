package com.example.a10xandroid.ui.recommendations

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private const val TAG = "RecommendationsScreen"

/**
 * Main screen for displaying movie recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    viewModel: RecommendationsViewModel = hiltViewModel()
) {
    Log.d(TAG, "RecommendationsScreen composable called")

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d(TAG, "RecommendationsScreen LaunchedEffect triggered")
        Log.d(TAG, "Initial UI state: status=${uiState.status}, isEmpty=${uiState.isEmpty}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommendations") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG, "Back button clicked")
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Log.d(
                TAG,
                "Rendering content with state: status=${uiState.status}, isEmpty=${uiState.isEmpty}"
            )
            LoadingStateHandler(
                state = uiState,
                onRetry = {
                    Log.d(TAG, "Retry button clicked")
                    viewModel.loadRecommendations()
                },
                content = {
                    if (uiState.isEmpty) {
                        Log.d(TAG, "Rendering EmptyStateView")
                        EmptyStateView()
                    } else {
                        Log.d(
                            TAG,
                            "Rendering RecommendationsList with ${uiState.recommendations.size} items"
                        )
                        RecommendationsList(
                            recommendations = uiState.recommendations,
                            onDismiss = { movieId ->
                                Log.d(TAG, "Dismissing recommendation: $movieId")
                                viewModel.dismissRecommendation(movieId)
                            },
                            onSave = { movieId ->
                                Log.d(TAG, "Saving recommendation: $movieId")
                                viewModel.saveRecommendation(movieId)
                            }
                        )
                    }
                }
            )
        }
    }
}
