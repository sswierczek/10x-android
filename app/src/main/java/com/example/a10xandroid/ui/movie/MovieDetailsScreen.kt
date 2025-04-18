package com.example.a10xandroid.ui.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a10xandroid.data.model.MovieEntry
import com.example.a10xandroid.ui.components.AppTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: String,
    navController: NavController,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = uiState.movieEntry?.title ?: "Movie Details",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                uiState.movieEntry != null -> {
                    MovieDetailsContent(
                        movieEntry = uiState.movieEntry!!,
                        isUpdating = uiState.isUpdating,
                        onUserRatingChange = viewModel::updateRating
                    )
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Movie") },
                text = { Text("Are you sure you want to delete this movie?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMovie()
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun MovieDetailsContent(
    movieEntry: MovieEntry,
    isUpdating: Boolean,
    onUserRatingChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = movieEntry.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Release date and TMDB rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display TMDB rating as read-only
            val tmdbRating = movieEntry.tmdbRating ?: 0.0
            Text(
                text = "TMDB Rating: ${
                    String.format(
                        locale = Locale.US,
                        "%.1f",
                        tmdbRating,
                    )
                }/10",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${movieEntry.releaseDate}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Movie Poster
        movieEntry.posterPath?.let { posterPath ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/w500$posterPath")
                    .crossfade(true)
                    .build(),
                contentDescription = "Movie poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // User Rating Section
        Text(
            text = "Your Rating",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Row {
                repeat(5) { index ->
                    val starValue = index + 1
                    // Convert to user rating scale (1-5)
                    val userRating = movieEntry.userRating ?: 1

                    IconButton(
                        onClick = {
                            if (!isUpdating) {
                                onUserRatingChange(starValue)
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = if (userRating >= starValue) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $starValue stars",
                            tint = if (userRating >= starValue) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.tertiaryContainer
                            },
                        )
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = movieEntry.overview ?: "No overview available",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Watched on: ${formatDateFromTimestamp(movieEntry.watchDate)}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (!movieEntry.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = movieEntry.notes,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatDateFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    return SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date)
}
