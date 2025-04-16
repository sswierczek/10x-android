package com.example.a10xandroid.ui.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a10xandroid.ui.common.StateStatus
import com.example.a10xandroid.ui.recommendations.model.RecommendationMovie
import com.example.a10xandroid.ui.recommendations.model.RecommendationsUiState

/**
 * Component managing different UI states
 */
@Composable
fun LoadingStateHandler(
    state: RecommendationsUiState,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.status) {
            StateStatus.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            StateStatus.ERROR -> {
                ErrorView(
                    errorMessage = state.errorMessage ?: "Unknown error occurred",
                    onRetry = onRetry
                )
            }

            StateStatus.SUCCESS -> {
                content()
            }
        }
    }
}

@Composable
fun ErrorView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No recommendations available",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check back later for new movie recommendations",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * List of recommended movies
 */
@Composable
fun RecommendationsList(
    recommendations: List<RecommendationMovie>,
    onDismiss: (String) -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(recommendations) { recommendation ->
            MovieCard(
                movie = recommendation,
                onDismiss = { onDismiss(recommendation.id) },
                onSave = { onSave(recommendation.id) }
            )
        }
    }
}

/**
 * Card displaying a recommended movie
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: RecommendationMovie,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            PosterImage(
                posterUrl = movie.posterUrl,
                modifier = Modifier.width(133.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.year,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.genre,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rating: ${movie.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dismiss")
                    }
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

/**
 * Component displaying a movie poster
 */
@Composable
fun PosterImage(
    posterUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        if (posterUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Movie poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Failed to load poster",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

