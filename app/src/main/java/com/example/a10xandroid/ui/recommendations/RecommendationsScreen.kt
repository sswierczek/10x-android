package com.example.a10xandroid.ui.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.a10xandroid.ui.components.AppTopBar
import com.example.a10xandroid.ui.recommendations.model.RecommendationMovieViewModel

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
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Recommendations",
                onBackClick = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { viewModel.refreshRecommendations() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh recommendations",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }

                uiState.hasError -> {
                    ErrorView(
                        message = uiState.errorMessage ?: "An error occurred",
                        onRetry = { viewModel.loadRecommendations() }
                    )
                }

                uiState.isEmpty -> {
                    EmptyStateView(
                        message = "No recommendations available yet. Add some movies to your journal to get personalized recommendations!",
                        actionLabel = "Add Movies",
                        onActionClick = { navController.navigate("add_movie") }
                    )
                }

                else -> {
                    RecommendationsList(
                        recommendations = uiState.recommendations,
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshRecommendations() },
                        onAddToJournal = { movie -> viewModel.addToJournal(movie) }
                    )
                }
            }
        }
    }
}

/**
 * Loading indicator component
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

/**
 * Error view component
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

/**
 * Empty state view component
 */
@Composable
fun EmptyStateView(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * List of recommendations component
 */
@Composable
fun RecommendationsList(
    recommendations: List<RecommendationMovieViewModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onAddToJournal: (RecommendationMovieViewModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recommendations) { movie ->
            RecommendationMovieCard(
                movie = movie,
                onAddToJournal = { onAddToJournal(movie) }
            )
        }
    }
}

/**
 * Movie card component
 */
@Composable
fun RecommendationMovieCard(
    movie: RecommendationMovieViewModel,
    onAddToJournal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Movie poster and title row
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Movie poster
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "Poster for ${movie.title}",
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Movie details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "⭐ ${String.format("%.1f",movie.rating)}/10 • ${movie.year}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Overview
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium
            )

            // Recommendation reason
            if (movie.reason != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Why we recommend it:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = movie.reason,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Add to journal button
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (movie.saved) {
                    Text(
                        text = "Added to Journal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = onAddToJournal,
                        content = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add to journal"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add to Journal")
                            }
                        }
                    )
                }
            }
        }
    }
}
